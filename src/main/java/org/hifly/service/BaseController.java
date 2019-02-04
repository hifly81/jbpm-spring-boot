package org.hifly.service;

import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping(value = "/pam")
public class BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    private DeploymentService deploymentService;

    @Autowired
    private RuntimeDataService runtimeDataService;

    @Autowired
    private ProcessService processService;

    @Autowired
    private UserTaskService userTaskService;


    @GetMapping(value = "/deploy/{groupId}/{artifactId}/{version}")
    public ResponseEntity deploy(
            @PathVariable("groupId") String groupId,
            @PathVariable("artifactId") String artifactId,
            @PathVariable("version") String version) {

        //deploy kjar
        KModuleDeploymentUnit unit = new KModuleDeploymentUnit(groupId, artifactId, version);
        deploymentService.deploy(unit);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/instances")
    public ResponseEntity<List<ProcessInstanceDesc>> instances() {
        List<ProcessInstanceDesc> result = new ArrayList<>();

        //query instances
        Collection<ProcessInstanceDesc> processInstanceDescs = runtimeDataService.getProcessInstances(null);
        if(processInstanceDescs != null && !processInstanceDescs.isEmpty()) {
            for(ProcessInstanceDesc processInstanceDesc: processInstanceDescs)
                result.add(processInstanceDesc);

        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/start/{groupId}/{artifactId}/{version}/{processId}")
    public ResponseEntity<Long> start(
            @PathVariable("groupId") String groupId,
            @PathVariable("artifactId") String artifactId,
            @PathVariable("version") String version,
            @PathVariable("processId") String processId) {
        long processInstanceId = processService.startProcess(groupId + ":" + artifactId + ":" + version, processId);
        return new ResponseEntity<>(processInstanceId, HttpStatus.OK);
    }

    @GetMapping(value = "/tasks/{processInstanceId}")
    public ResponseEntity<List<TaskSummary>> tasks(
            @PathVariable("processInstanceId") Long processInstanceId) {
        //check human tasks
        List<TaskSummary> tasks = runtimeDataService.getTasksByStatusByProcessInstanceId(processInstanceId, null, null);
        return new ResponseEntity(tasks, HttpStatus.OK);
    }

    @GetMapping(value = "/tasks/{taskId}/{actor}")
    public ResponseEntity<Task> completeTask(
            @PathVariable("taskId") Long taskId,
            @PathVariable("actor") String actor) {
        Task task = userTaskService.getTask(taskId);
        if(task != null) {
            LOGGER.info("Task {} status {}", task.getId(), task.getTaskData().getStatus());
            if(task.getTaskData().getStatus() == Status.Reserved) {
                userTaskService.start(task.getId(), actor);
                userTaskService.complete(task.getId(), actor, null);
            }
            else if(task.getTaskData().getStatus() == Status.Ready) {
                userTaskService.claim(task.getId(), actor);
                userTaskService.start(task.getId(), actor);
                userTaskService.complete(task.getId(), actor, null);
            }
        }

        return new ResponseEntity(task, HttpStatus.OK);
    }



}

