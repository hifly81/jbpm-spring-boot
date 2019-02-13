jbpm-spring-boot example
=============================

Example of a jbpm service with spring boot.

Several application.properties are defined, each one with a specific database configuration:
 - h2 (default)
 - mysql
 - postgres
 - oracle

## Define the kie containers to deploy at startup

The list of kie containers (groupId, artifactId version) to deploy at startup must be defined inside the new-hire-service.xml file.<br>
The kjars must exists inside your local .m2 maven repository.


## Run and deploy a kjar

```bash
  mvn spring-boot:run -Drun.arguments=<groupId>,<artifactId>,<version>
```

## Run and deploy a kjar with mysql dbms

```bash
  mvn spring-boot:run -Dorg.kie.server.startup.strategy=LocalContainersStartupStrategy -Pmysql
```

## Run and deploy a kjar with postgres dbms

```bash
  mvn spring-boot:run -Dorg.kie.server.startup.strategy=LocalContainersStartupStrategy -Ppostgres
```

## Run and deploy a kjar with oracle dbms

```bash
  mvn spring-boot:run -Dorg.kie.server.startup.strategy=LocalContainersStartupStrategy -Poracle
```

## Postman collection

A postman collection for testing the API is available inside postman directory.
