#!/bin/bash

ocp_admin_url=<changeme>
ocp_docker_registry=<changeme>
ocp_user=<changeme>
ocp_user_token=$(oc whoami -t)
ocp_namespace=new-hire
docker_tag_name=jbpm-console-new-hire
docker_tag_name2=kie-server-new-hire
image_version=latest

#create ocp project
oc login ${ocp_admin_url} -u ${ocp_user} -p ${ocp_user_token}
oc delete project ${ocp_namespace}
oc new-project ${ocp_namespace}

############################ BUSINESS CENTRAL

#create business central image
cd business-central/
docker build -t ${docker_tag_name}:${image_version} .

#push business central image in ocp namespace registry
docker tag ${docker_tag_name}:${image_version} ${ocp_docker_registry}/${ocp_namespace}/${docker_tag_name}:${image_version}
docker login -u ${ocp_user} -p ${ocp_user_token} ${ocp_docker_registry}
docker push ${ocp_docker_registry}/${ocp_namespace}/${docker_tag_name}:${image_version}

#create business central application
oc new-app ${ocp_namespace}/${docker_tag_name}:${image_version} -e JAVA_OPTS_APPEND="-Dorg.kie.server.user=user -Dorg.kie.server.password=user"

#create route for business central application
oc expose service ${docker_tag_name} --port=8080 --hostname=business-central.example.com

############################ KIE SERVER

#create config map
cd ../../
oc create configmap new-hire-service-kie-server-state --from-file=openshift/kie-server/new-hire-service.xml

#compile and package
mvn clean package
cp target/new-hire-service-1.0-SNAPSHOT.jar openshift/kie-server

#create kie server image
cd openshift/kie-server
docker build -t ${docker_tag_name2}:${image_version} .

#push kie server image in ocp namespace registry
docker tag ${docker_tag_name2}:${image_version} ${ocp_docker_registry}/${ocp_namespace}/${docker_tag_name2}:${image_version}
docker login -u ${ocp_user} -p ${ocp_user_token} ${ocp_docker_registry}
docker push ${ocp_docker_registry}/${ocp_namespace}/${docker_tag_name2}:${image_version}

#create new hire application
oc new-app --file=template.yml

#cleanup
rm -rf new-hire-service-1.0-SNAPSHOT.jar