FROM openjdk:17
ARG PROJECT_VERSION=0.1.0
RUN mkdir -p /home/app
WORKDIR /home/app
ENV SPRING_PROFILES_ACTIVE dev
COPY proxy-client/ .
ADD proxy-client/target/proxy-client-v${PROJECT_VERSION}.jar proxy-client.jar
EXPOSE 8900
ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "proxy-client.jar"]