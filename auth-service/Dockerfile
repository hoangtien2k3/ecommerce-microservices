FROM eclipse-temurin:21-jre-alpine
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-Duser.timezone=UTC", "-jar", "app.jar"]
