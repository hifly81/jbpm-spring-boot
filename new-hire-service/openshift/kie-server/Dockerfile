FROM fabric8/java-jboss-openjdk8-jdk
ENV JAVA_APP_JAR new-hire-service-1.0-SNAPSHOT.jar
ENV AB_OFF true
EXPOSE 8090

COPY maven/ /tmp/maven/
ADD $JAVA_APP_JAR /deployments/