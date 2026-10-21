# Override for a private registry, e.g. --build-arg BASE_IMAGE=<harbor>/dockerhub-proxy/library/eclipse-temurin:21-jre-alpine
ARG BASE_IMAGE=eclipse-temurin:21-jre-alpine
FROM ${BASE_IMAGE}
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
