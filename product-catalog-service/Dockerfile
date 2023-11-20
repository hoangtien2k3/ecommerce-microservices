## start with a base image
FROM openjdk:17
## volumes
VOLUME /tmp
## add the application jar to the container
ADD target/product-catalog-service-0.0.1-SNAPSHOT.jar product-catalog-service-0.0.1-SNAPSHOT.jar
## extract the application jar entry to the container
ENTRYPOINT ["java", "-jar", "product-catalog-service-0.0.1-SNAPSHOT.jar"]
