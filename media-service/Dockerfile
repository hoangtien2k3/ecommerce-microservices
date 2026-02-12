FROM eclipse-temurin:21.0.10_7-jre-alpine
COPY target/media*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]