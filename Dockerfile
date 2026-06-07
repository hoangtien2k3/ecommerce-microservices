# Universal Dockerfile for all Spring Boot services in this multi-module Maven project.
# Usage:
#   docker build --build-arg SERVICE_NAME=product-service -t ecommerce/product-service .
#
# Build context must be the REPO ROOT (not the service subdirectory).

ARG SERVICE_NAME=product-service

# ── Stage 1: Build ────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS build
ARG SERVICE_NAME

WORKDIR /workspace

# 1. Root pom (parent declaration + dependencyManagement used by all modules)
COPY pom.xml .

# 2. All child pom.xml files so Maven can resolve the multi-module graph
COPY common-lib/pom.xml          common-lib/pom.xml
COPY api-gateway/pom.xml         api-gateway/pom.xml
COPY discovery-service/pom.xml   discovery-service/pom.xml
COPY auth-service/pom.xml        auth-service/pom.xml
COPY product-service/pom.xml     product-service/pom.xml
COPY order-service/pom.xml       order-service/pom.xml
COPY payment-service/pom.xml     payment-service/pom.xml
COPY inventory-service/pom.xml   inventory-service/pom.xml
COPY shipping-service/pom.xml    shipping-service/pom.xml
COPY notification-service/pom.xml notification-service/pom.xml
COPY rating-service/pom.xml      rating-service/pom.xml
COPY search-service/pom.xml      search-service/pom.xml
COPY promotion-service/pom.xml   promotion-service/pom.xml
COPY tax-service/pom.xml         tax-service/pom.xml
COPY favourite-service/pom.xml   favourite-service/pom.xml
COPY media-service/pom.xml       media-service/pom.xml

# 3. Maven wrapper (taken from the target service — all services have one)
COPY ${SERVICE_NAME}/mvnw   mvnw
COPY ${SERVICE_NAME}/.mvn   .mvn/
RUN chmod +x mvnw

# 4. Pre-download dependencies (cached layer — only re-runs when a pom changes)
RUN ./mvnw -pl common-lib,${SERVICE_NAME} -am dependency:go-offline -B -q

# 5. Copy sources for common-lib + target service only
COPY common-lib/src   common-lib/src
COPY ${SERVICE_NAME}/src ${SERVICE_NAME}/src

# 6. Build
RUN ./mvnw -pl common-lib,${SERVICE_NAME} -am package -DskipTests -B -q

# ── Stage 2: Runtime ──────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
ARG SERVICE_NAME

RUN apk add --no-cache curl && \
    addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app
COPY --from=build /workspace/${SERVICE_NAME}/target/*.jar app.jar

USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
