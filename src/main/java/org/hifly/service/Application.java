package org.hifly.service;

import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.api.model.ProcessDefinition;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.kie.api.runtime.query.QueryContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Collection;
import java.util.List;

@SpringBootApplication
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    @Autowired
    private DeploymentService deploymentService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner deploy() {
        return new CommandLineRunner() {

            @Autowired
            private DeploymentService deploymentService;

            @Autowired
            private ProcessService processService;

            @Autowired
            private RuntimeDataService runtimeDataService;

            @Autowired
            private UserTaskService userTaskService;

            @Override
            public void run(String... args) throws Exception {

                if(args.length < 3)
                    throw new IllegalStateException("3 arguments are required: groupId, artifcatId, version");

                //deploy kjar
                KModuleDeploymentUnit unit = new KModuleDeploymentUnit(args[0], args[1], args[2]);
                deploymentService.deploy(unit);

                Thread.sleep(5000);

                //query instances
                Collection<ProcessInstanceDesc> processInstanceDescs = runtimeDataService.getProcessInstances(null);
                LOGGER.info("list instances {}", processInstanceDescs);
                if(processInstanceDescs != null && !processInstanceDescs.isEmpty()) {
                    for(ProcessInstanceDesc processInstanceDesc: processInstanceDescs)
                        LOGGER.info("instance {}", processInstanceDesc.getId());
                }

                //check available processes and start
                Collection<ProcessDefinition> processes = runtimeDataService.getProcesses(new QueryContext());
                if (unit != null && !processes.isEmpty()) {
                    for (ProcessDefinition def : processes) {
                        String processId = processes.iterator().next().getId();
                        LOGGER.info("About to start process with id {}", processId);
                        long processInstanceId = processService.startProcess(unit.getIdentifier(), processId);

                        LOGGER.info("Process id {}", processInstanceId);

                        //check human tasks
                        List<Long> tasks = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
                        if(tasks != null && !tasks.isEmpty()) {
                            for(Long task: tasks) {
                                LOGGER.info("Task id {}", task);
                                //start and complete task
                                userTaskService.start(task, "kermit");
                                userTaskService.complete(task, "kermit", null);
                            }
                        }


                    }
                }


            }
        };
    }

}