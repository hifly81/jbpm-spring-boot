jbpm-spring-boot example
=============================

Example of a jbpm service with spring boot.<br>
The project is compose by a kjar, representing a sample new-hire BPMN process and by a kie-server running on spring boot.<br>

This is an image showing the BPMN process:
![ScreenShot 1](images/newhire.png)

## OpenShift Environment installation

### Prerequisites

You need an OpenShift cluster version 3.11 to run the application.<br>
You can also use minishift or oc cluster.

You need the OpenShift CLI (oc command) on your machine in order to use the launch.sh script.

You need Docker on your machine to build the images.

### Install on OpenShift

The bash script new-hire-service/openshift/launch.sh will create an OpenShift project named "new-hire"

You only need the OpenShift admin node url, an Openshift user and the endpoint of the Openshift registry.

You can get the OpenShift registry address from the default namespace:

```bash
oc project default
oc get svc
NAME              TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)                   AGE
docker-registry   ClusterIP   172.30.1.1      <none>        5000/TCP                  59d
```

Open the file new-hire-service/openshift/launch.sh and modify the properties:<br><br>
ocp_admin_url= --> ocp master url, example: localhost:8443<br>
ocp_docker_registry= --> ocp registry host and port, example: 172.30.1.1:5000<br>
ocp_user= --> ocp user, example: developer

according with your values.

Launch the bootstrap script to create your namespace:

```bash
cd new-hire-service/openshift/
./launch.sh
```

When completed, verify that your cluster contains the following pods with state Running:

```bash
oc get pods
NAME                            READY     STATUS    RESTARTS   AGE
jbpm-console-new-hire-1-78bxg   1/1       Running   0          2m
new-hire-service-1-8qltt        1/1       Running   0          1m
prometheus-1-kjlbs              1/1       Running   0          1m
```

Verify that the following routes are created:

```bash
oc get routes
NAME                    HOST/PORT                      PATH      SERVICES                PORT      TERMINATION   WILDCARD
jbpm-console-new-hire   business-central.example.com             jbpm-console-new-hire   8080                    None
new-hire-service        new-hire-service.example.com             new-hire-service        8090                    None
prometheus              prometheus.example.com                   prometheus              9090                    None
```

Business central (user/user) will be available at url:<br>
http://business-central.example.com

Kie server (user/user) will be available at url:<br>
http://new-hire-service.example.com

Prometheus will be available at url:<br>
http://prometheus.example.com

### Prometheus metrics

Process instances and human tasks basic metrics are exposed using prometheus; they are available at url:<br>
http://new-hire-service.example.com/metrics

Prometheus is already configured to scrape these metrics; verify at url:<br>
http://prometheus.example.com/targets

### Postman collection

A postman collection named postman_openshift.json for testing the API is available inside postman directory.

```bash
cd new-hire-service/postman
```

## Local Environment installation

### Prerequisites

You need an existing PAM business central listening at localhost:8080 (for monitoring the kie server).<br>
You need to define these two properties for the business central in order to monitor the kie-server:
```bash
<property name="org.kie.server.user" value="user"/>
<property name="org.kie.server.pwd" value="user"/>
```

### Install the kjar in your .m2 repo

```bash
  cd new-hire-kjar
  mvn clean install
```

### Define the kie server properties

The list of kie containers (groupId, artifactId version) to deploy at startup must be defined inside the new-hire-service.xml file.<br>
The kjars must exists inside your local .m2 maven repository.

Several application.properties are defined, each one with a specific database configuration:
 - h2 (default)
 - mysql
 - postgres
 - oracle

You can configure the user/password to connect with the controller (Business Central) through the following system properties inside the new-hire-service.xml file:

```bash
 org.kie.server.controller.user=<user>
 org.kie.server.controller.pwd=<password>
```


### Custom Rest endpoint

A custom rest endpoint, registered under path /rest/pam is available and it adds additional APIs to the kie server.

### Run a kie-server and deploy a kjar

```bash
  cd new-hire-service
  mvn spring-boot:run -Dorg.kie.server.startup.strategy=LocalContainersStartupStrategy
```

### Run a kie-server and deploy a kjar with mysql dbms

```bash
  cd new-hire-service
  mvn spring-boot:run -Dorg.kie.server.startup.strategy=LocalContainersStartupStrategy -Pmysql
```

### Run a kie-server and deploy a kjar with postgres dbms

```bash
  cd new-hire-service
  mvn spring-boot:run -Dorg.kie.server.startup.strategy=LocalContainersStartupStrategy -Ppostgres
```

### Run a kie-server and deploy a kjar with oracle dbms

```bash
  cd new-hire-service
  mvn spring-boot:run -Dorg.kie.server.startup.strategy=LocalContainersStartupStrategy -Poracle
```

### Prometheus metrics

Process instances and human tasks basic metrics are exposed using prometheus; they are available at url:<br>
http://localhost:8090/metrics

### Postman collection

A postman collection named postman.json for testing the API is available inside postman directory.

```bash
cd new-hire-service/postman
```
