#!/bin/bash
set -e

ROOT="/Volumes/DATA/Backend/ecommerce-microservices"

# Point Docker to Minikube's daemon
eval $(minikube docker-env)

echo "========================================"
echo " Building Docker images into Minikube"
echo "========================================"

build_image() {
  local SVC=$1
  local PORT=$2
  local DIR="$ROOT/$SVC"
  local JAR=$(ls "$DIR/target/"*.jar 2>/dev/null | head -1)

  if [ -z "$JAR" ]; then
    echo "⚠️  SKIP $SVC — no JAR found"
    return
  fi

  echo ""
  echo "📦 Building ecommerce/$SVC:latest  (port $PORT)"

  cat > "$DIR/Dockerfile.slim" <<EOF
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app
COPY target/$(basename "$JAR") app.jar
EXPOSE $PORT
USER appuser
ENTRYPOINT ["java", "-jar", "app.jar"]
EOF

  docker build -f "$DIR/Dockerfile.slim" -t "ecommerce/$SVC:latest" "$DIR"
  rm -f "$DIR/Dockerfile.slim"
  echo "✅ $SVC done"
}

# Build frontend
build_frontend() {
  local DIR="$ROOT/frontend"
  echo ""
  echo "📦 Building ecommerce/frontend:latest"
  docker build -f "$DIR/Dockerfile" -t "ecommerce/frontend:latest" "$DIR"
  echo "✅ frontend done"
}

# ---- Backend services ----
build_image "discovery-service"   8761
build_image "api-gateway"         8888
build_image "auth-service"        8088
build_image "product-service"     8086
build_image "order-service"       8084
build_image "payment-service"     8085
build_image "shipping-service"    8087
build_image "inventory-service"   8082
build_image "favourite-service"   8081
build_image "rating-service"      8089
build_image "notification-service" 8090
build_image "media-service"       8083
build_image "search-service"      8092
build_image "promotion-service"   8092
build_image "tax-service"         8091

# ---- Frontend ----
build_frontend

echo ""
echo "========================================"
echo " All images built. Deploying to k8s..."
echo "========================================"

K8S="$ROOT/k8s"

# Reset kubectl context to minikube
kubectl config use-context minikube

echo ""
echo "▶ Applying namespace..."
kubectl apply -f "$K8S/namespace.yaml"

echo "▶ Applying secrets..."
kubectl apply -f "$K8S/secrets.yaml"

echo "▶ Applying configmap..."
kubectl apply -f "$K8S/configmap.yaml"

echo "▶ Applying infra (postgres, redis, kafka, ...)..."
kubectl apply -f "$K8S/infra/"

echo "▶ Waiting 30s for infra to initialise..."
sleep 30

echo "▶ Applying backend services..."
# discovery-service first so others can register
kubectl apply -f "$K8S/backend/discovery-service.yaml"
echo "  Waiting 20s for discovery-service..."
sleep 20
kubectl apply -f "$K8S/backend/"

echo "▶ Applying frontend..."
kubectl apply -f "$K8S/frontend/"

echo "▶ Applying ingress..."
kubectl apply -f "$K8S/ingress/" 2>/dev/null || true

echo ""
echo "========================================"
echo " Deploy complete! Checking status..."
echo "========================================"
kubectl get pods -n ecommerce
