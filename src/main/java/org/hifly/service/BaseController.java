package org.hifly.service;

import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.DeploymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/pam")
public class BaseController {

    @Autowired
    private DeploymentService deploymentService;


    @GetMapping(value = "/deploy/{artifactId}/{groupId}/{version}")
    public ResponseEntity deploy(
            @PathVariable("artifactId") String artifactId,
            @PathVariable("groupId") String groupId,
            @PathVariable("version") String version) {

        //deploy kjar
        KModuleDeploymentUnit unit = new KModuleDeploymentUnit(artifactId, groupId, version);
        deploymentService.deploy(unit);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
