package org.hifly.service;

import io.swagger.annotations.*;
import org.jbpm.services.api.UserTaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Api(value = "New Hire Additional Endpoints", produces = MediaType.APPLICATION_JSON)
@Path("pam")
public class NewHireController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewHireController.class);


    @Autowired
    private UserTaskService userTaskService;


    @ApiOperation(value = "Complete a task in Ready or Reserved state")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Task completed")
    })
    @POST
    @Path(value = "/tasks/{taskId}/{actor}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response autoCompleteTask(
            @ApiParam(value = "task id", required = true) @PathParam("taskId") Long taskId,
            @ApiParam(value = "name of the actor", required = true) @PathParam("actor") String actor) {
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

        return Response.ok().build();
    }



}

