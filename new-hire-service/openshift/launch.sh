#!/bin/bash

ocp_admin_url=localhost:8443
ocp_user=developer
ocp_user_token=$(oc whoami -t)
ocp_namespace=new-hire

#create ocp project
oc login ${ocp_admin_url} -u ${ocp_user} -p ${ocp_user_token}
oc delete project ${ocp_namespace}
oc new-project ${ocp_namespace}
oc project ${ocp_namespace}

############################ BUSINESS CENTRAL

#create business central application
oc import-image jbpm-console-new-hire:1.0 --from=hifly81/jbpm-console-new-hire:1.0 --confirm
oc new-app jbpm-console-new-hire:1.0 -e JAVA_OPTS_APPEND="-Dorg.kie.server.user=user -Dorg.kie.server.password=user -Dorg.kie.server.id=new-hire-service -Dorg.kie.server.location=http://new-hire-service:8090/rest/server"

#create route for business central application
oc expose service jbpm-console-new-hire --port=8080

############################ KIE SERVER

#create config map
oc create configmap new-hire-service-kie-server-state --from-file=kie-server/new-hire-service.xml

#create new hire application
oc import-image kie-server-new-hire:1.0 --from=hifly81/kie-server-new-hire:1.0 --confirm
oc new-app --file=kie-server/template.yml

############################ PROMETHEUS
#create config map
oc apply -f prometheus/prometheus-config-map.yml

#create prometheus application
oc import-image my-openshift3/prometheus --from=registry.access.redhat.com/openshift3/prometheus --confirm
oc new-app prometheus:latest
oc set volumes dc/prometheus --add --overwrite=true --name=prometheus-2 --mount-path=/etc/prometheus -t configmap --configmap-name=prometheus-config-map

#create route for business central application
oc expose service prometheus --port=9090 --hostname=prometheus.example.com


############################ GRAFANA
oc new-app --file=grafana/grafana.yml