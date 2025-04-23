FROM eclipse-temurin:21.0.7_6-jre-alpine
COPY target/promotion*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]