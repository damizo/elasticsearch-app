FROM openjdk:8-jdk-alpine

LABEL maintainer="damiane@interia.eu"

VOLUME /tmp

EXPOSE 8080

ARG JAR_FILE=target/demo-elk-0.0.1-SNAPSHOT.jar

ADD ${JAR_FILE} demo-elk-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/ demo-elk-0.0.1-SNAPSHOT.jar"]