package org.hifly.service;

import org.apache.commons.lang3.StringUtils;
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

    public synchronized ProcessInstanceInfo getProcessInstance(org.jbpm.services.api.ProcessService processService, String deploymentId, Long ProcessInstanceId, CorrelationKey correlationKey, boolean getProcessVariables) throws Exception{
        checkProcessService(processService);

        logger.info("ProcessUtilService - getProcessInstance - starting...");

        ProcessInstance pi = null;
        if(StringUtils.isNotBlank(deploymentId)) {
            if(correlationKey != null) {

                logger.info("ProcessUtilService - getProcessInstance - getting instance for deploymentId:{} and correlationKey:{}..",deploymentId,correlationKey.toExternalForm());
                pi =  processService.getProcessInstance(deploymentId, correlationKey);
            }else {
                logger.info("ProcessUtilService - getProcessInstance - getting instance for deploymentId:{} and processInstanceId:{}..",deploymentId,ProcessInstanceId);
                pi = processService.getProcessInstance(deploymentId, ProcessInstanceId);
            }
        }else {
            if(correlationKey != null) {
                logger.info("ProcessUtilService - getProcessInstance - getting instance for correlationKey:{}. No deploymenetId specified..",correlationKey.toExternalForm());
                pi =  processService.getProcessInstance(correlationKey);
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

    public ProcessInstanceInfo getProcessInstance(org.jbpm.services.api.ProcessService processService, String deploymentId, List<String> businessKeys, boolean getProcessVariables) throws Exception{
        return getProcessInstance(processService, deploymentId, null, generateCorrelationKey(businessKeys),getProcessVariables);
        //return getProcessInstance(processService, deploymentId, 1l, getProcessVariables);
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


}
