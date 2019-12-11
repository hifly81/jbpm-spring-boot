package org.hifly.service;

import org.apache.commons.lang3.StringUtils;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationKeyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProcessUtilService {

    private static final Logger logger = LoggerFactory.getLogger(ProcessUtilService.class);

    public ProcessInstanceInfo getProcessInstance(ProcessService processService, RuntimeDataService runtimeDataService, String deploymentId, Long ProcessInstanceId, CorrelationKey correlationKey, boolean getProcessVariables) throws Exception{
        checkProcessService(processService);

        logger.info("ProcessUtilService - getProcessInstance - starting...");

        ProcessInstance pi;
        if(StringUtils.isNotBlank(deploymentId)) {
            if(correlationKey != null) {
                logger.info("ProcessUtilService - getProcessInstance - getting instance for deploymentId:{} and correlationKey:{}..",deploymentId,correlationKey.toExternalForm());
                ProcessInstanceDesc processInstanceDesc = runtimeDataService.getProcessInstanceByCorrelationKey(correlationKey);
                if(processInstanceDesc.getDeploymentId().equalsIgnoreCase(deploymentId))
                    return buildProcessInstanceInfo(processInstanceDesc, processService, getProcessVariables);
                else
                    return null;
            }else {
                logger.info("ProcessUtilService - getProcessInstance - getting instance for deploymentId:{} and processInstanceId:{}..",deploymentId,ProcessInstanceId);
                pi = processService.getProcessInstance(deploymentId, ProcessInstanceId);
            }
        }else {
            if(correlationKey != null) {
                logger.info("ProcessUtilService - getProcessInstance - getting instance for correlationKey:{}. No deploymenetId specified..",correlationKey.toExternalForm());
                ProcessInstanceDesc processInstanceDesc = runtimeDataService.getProcessInstanceByCorrelationKey(correlationKey);
                return buildProcessInstanceInfo(processInstanceDesc, processService,getProcessVariables);
            }else {
                logger.info("ProcessUtilService - getProcessInstance - getting instance for processInstanceId:{}. No deploymenetId specified..",ProcessInstanceId);
                pi = processService.getProcessInstance(ProcessInstanceId);
            }
        }

        if(pi != null) {
            logger.info("ProcessUtilService - getProcessInstance - instance retrieved. creating ProcessInstanceInfo object...");
            return buildProcessInstanceInfo(pi, processService,getProcessVariables);
        }
        logger.info("ProcessUtilService - getProcessInstance - no instance found. return null...");
        return null;
    }

    public ProcessInstanceInfo getProcessInstance(ProcessService processService, RuntimeDataService runtimeDataService, String deploymentId, List<String> businessKeys, boolean getProcessVariables) throws Exception{
        return getProcessInstance(processService, runtimeDataService, deploymentId, null, generateCorrelationKey(businessKeys),getProcessVariables);
    }

    private CorrelationKey generateCorrelationKey(List<String> businessKeys) {
        CorrelationKeyFactory factory = KieInternalServices.Factory.get().newCorrelationKeyFactory();
        CorrelationKey correlationKey = factory.newCorrelationKey(businessKeys);
        return correlationKey;
    }

    private void checkProcessService(org.jbpm.services.api.ProcessService processService) throws Exception {
        if(processService == null) {
            logger.error("ProcessUtilService - processService instance is null.");
            throw new Exception("ProcessUtilService - processService instance is null.");
        }
    }

    private ProcessInstanceInfo buildProcessInstanceInfo(ProcessInstance processInstance, org.jbpm.services.api.ProcessService processService, boolean getProcessVariables) {
        ProcessInstanceInfo pii = new ProcessInstanceInfo();

        pii.setProcessId(processInstance.getProcessId());
        pii.setId(processInstance.getId());
        pii.setProcessStateCode(processInstance.getState());
        if(getProcessVariables) {
            pii.getProcessInstanceVariables().putAll(processService.getProcessInstanceVariables(processInstance.getId()));
        }
        return pii;
    }

    private ProcessInstanceInfo buildProcessInstanceInfo(ProcessInstanceDesc processInstanceDesc, org.jbpm.services.api.ProcessService processService, boolean getProcessVariables) {
        ProcessInstanceInfo pii = new ProcessInstanceInfo();

        pii.setProcessId(processInstanceDesc.getProcessId());
        pii.setId(processInstanceDesc.getId());
        pii.setProcessStateCode(processInstanceDesc.getState());
        if(getProcessVariables) {
            pii.getProcessInstanceVariables().putAll(processService.getProcessInstanceVariables(processInstanceDesc.getId()));
        }
        return pii;
    }


}
