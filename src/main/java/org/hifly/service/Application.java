package org.hifly.service;

import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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


            @Override
            public void run(String... args)  {

                if(args.length < 3)
                    throw new IllegalStateException("3 arguments are required: groupId, artifactId, version");

                //deploy kjar
                KModuleDeploymentUnit unit = new KModuleDeploymentUnit(args[0], args[1], args[2]);
                deploymentService.deploy(unit);


            }
        };
    }

}