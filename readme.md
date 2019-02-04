jbpm-spring-boot example
=============================

Example of a jbpm service with spring boot.

Several application.properties are defined, each one with a specific database configuration:
 - h2 (default)
 - mysql
 - postgres
 - oracle


## Run and deploy a kjar

```bash
  mvn spring-boot:run -Drun.arguments=<groupId>,<artifactId>,<version>
```

## Run and deploy a kjar with mysql dbms

```bash
  mvn spring-boot:run -Drun.arguments=<groupId>,<artifactId>,<version> -Pmysql
```

## Run and deploy a kjar with postgres dbms

```bash
  mvn spring-boot:run -Drun.arguments=<groupId>,<artifactId>,<version> -Ppostgres
```

## Run and deploy a kjar with oracle dbms

```bash
  mvn spring-boot:run -Drun.arguments=<groupId>,<artifactId>,<version> -Poracle
```

## Postman collection

A postman collection for testing the API is available inside postman directory.