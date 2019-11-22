FROM openjdk18-openshift

ENV AB_ENABLED off
ENV AB_JOLOKIA_AUTH_OPENSHIFT true

EXPOSE 8090

RUN mkdir -p /deployments/maven/repository/com/redhat/new-hire/1.0.0

RUN curl http://${nexus-repo}/com/redhat/new-hire/1.0.0/new-hire-1.0.0.pom --output new-hire-1.0.0.pom --silent
RUN curl http://${nexus-repo}/com/redhat/new-hire/1.0.0/new-hire-1.0.0.jar --output new-hire-1.0.0.jar --silent

RUN mv new-hire-1.0.0.pom /deployments/maven/repository/com/redhat/new-hire/1.0.0
RUN mv new-hire-1.0.0.jar /deployments/maven/repository/com/redhat/new-hire/1.0.0

# Copy artifact
COPY target/new-hire-service-1.0.0.jar /deployments/

USER root

RUN chmod -R 777 /deployments/maven/repository

USER jboss
