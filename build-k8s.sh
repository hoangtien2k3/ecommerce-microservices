#!/bin/bash
set -e

# ============================================================
#  build-k8s.sh — Build & deploy ecommerce microservices
#  Prerequisites: kubectl, minikube, docker, java, mvn
# ============================================================

ROOT="$(cd "$(dirname "$0")" && pwd)"
K8S="$ROOT/k8s"
SKIP_BUILD="${SKIP_BUILD:-false}"   # set SKIP_BUILD=true to skip Maven & Docker build

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; CYAN='\033[0;36m'; NC='\033[0m'
info()    { echo -e "${CYAN}▶ $*${NC}"; }
success() { echo -e "${GREEN}✅ $*${NC}"; }
warn()    { echo -e "${YELLOW}⚠️  $*${NC}"; }
error()   { echo -e "${RED}❌ $*${NC}"; exit 1; }

# ── 1. Check prerequisites ───────────────────────────────────
echo ""
echo "============================================================"
echo "  Checking prerequisites..."
echo "============================================================"

for cmd in kubectl minikube docker; do
  command -v "$cmd" &>/dev/null || error "$cmd not found. Install it first."
  success "$cmd found"
done

minikube status --format='{{.Host}}' 2>/dev/null | grep -q "Running" \
  || error "Minikube is not running. Run: minikube start --cpus=4 --memory=6144 --driver=docker"
success "Minikube is running"

# ── 2. Maven build (all services) ───────────────────────────
if [ "$SKIP_BUILD" != "true" ]; then
  echo ""
  echo "============================================================"
  echo "  Building all Java services with Maven..."
  echo "============================================================"

  for svc in discovery-service api-gateway auth-service product-service order-service \
             payment-service shipping-service inventory-service favourite-service \
             rating-service notification-service media-service search-service \
             promotion-service tax-service; do
    if [ -f "$ROOT/$svc/pom.xml" ]; then
      info "Maven build: $svc"
      (cd "$ROOT/$svc" && ./mvnw package -DskipTests -q) \
        && success "$svc built" \
        || error "Maven build failed for $svc"
    fi
  done
else
  warn "Skipping Maven build (SKIP_BUILD=true)"
fi

# ── 3. Docker images ─────────────────────────────────────────
echo ""
echo "============================================================"
echo "  Building Docker images into Minikube..."
echo "============================================================"

eval $(minikube docker-env)

build_image() {
  local SVC=$1 PORT=$2
  local DIR="$ROOT/$SVC"
  local JAR
  JAR=$(ls "$DIR/target/"*.jar 2>/dev/null | head -1)

  if [ -z "$JAR" ]; then
    warn "SKIP $SVC — no JAR found in $DIR/target/"
    return
  fi

  info "Building ecommerce/$SVC:latest"
  cat > "$DIR/Dockerfile.slim" <<DEOF
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app
COPY target/$(basename "$JAR") app.jar
EXPOSE $PORT
USER appuser
ENTRYPOINT ["java", "-jar", "app.jar"]
DEOF

  docker build -f "$DIR/Dockerfile.slim" -t "ecommerce/$SVC:latest" "$DIR" --quiet
  rm -f "$DIR/Dockerfile.slim"
  success "$SVC image ready"
}

build_image "discovery-service"    8761
build_image "api-gateway"          8888
build_image "auth-service"         8088
build_image "product-service"      8086
build_image "order-service"        8084
build_image "payment-service"      8085
build_image "shipping-service"     8087
build_image "inventory-service"    8082
build_image "favourite-service"    8081
build_image "rating-service"       8089
build_image "notification-service" 8090
build_image "media-service"        8083
build_image "search-service"       8092
build_image "promotion-service"    8092
build_image "tax-service"          8091

info "Building ecommerce/frontend:latest"
docker build -f "$ROOT/frontend/Dockerfile" -t "ecommerce/frontend:latest" "$ROOT/frontend" --quiet
success "frontend image ready"

# ── 4. Deploy to Kubernetes ──────────────────────────────────
echo ""
echo "============================================================"
echo "  Deploying to Kubernetes..."
echo "============================================================"

kubectl config use-context minikube

info "Namespace, secrets, configmap..."
kubectl apply -f "$K8S/namespace.yaml"
kubectl apply -f "$K8S/secrets.yaml"
kubectl apply -f "$K8S/configmap.yaml"

info "Infrastructure (postgres, redis, kafka, elasticsearch, mongodb, keycloak, minio, zipkin)..."
kubectl apply -f "$K8S/infra/"

info "Waiting for core infra to be ready (postgres, redis, keycloak)..."
for svc in postgres redis; do
  kubectl rollout status statefulset/$svc -n ecommerce --timeout=180s \
    && success "$svc ready" || warn "$svc not ready yet, continuing..."
done
kubectl rollout status deployment/keycloak -n ecommerce --timeout=180s \
  && success "keycloak ready" || warn "keycloak not ready yet, continuing..."

info "Deploying discovery-service first..."
kubectl apply -f "$K8S/backend/discovery-service.yaml"

info "Waiting for discovery-service to be Ready (up to 4 minutes)..."
kubectl wait pod -n ecommerce -l app=discovery-service \
  --for=condition=Ready --timeout=240s \
  && success "discovery-service is Ready!" \
  || error "discovery-service failed to start. Run: kubectl logs -n ecommerce -l app=discovery-service"

info "Deploying all backend services..."
for yaml in "$K8S/backend/"*.yaml; do
  [[ "$yaml" == *"discovery-service"* ]] && continue
  kubectl apply -f "$yaml"
done

info "Deploying frontend..."
kubectl apply -f "$K8S/frontend/"

info "Applying ingress (if exists)..."
kubectl apply -f "$K8S/ingress/" 2>/dev/null || true

# ── 5. Status ────────────────────────────────────────────────
echo ""
echo "============================================================"
echo "  Deploy complete! Pod status:"
echo "============================================================"
kubectl get pods -n ecommerce

echo ""
echo -e "${GREEN}Done! Services are starting up (Spring Boot takes ~2–3 min to boot).${NC}"
echo ""
echo "Useful commands:"
echo "  Watch pods:        kubectl get pods -n ecommerce -w"
echo "  Minikube dashboard: minikube dashboard"
echo "  Port-forward gateway: kubectl port-forward -n ecommerce svc/api-gateway 8888:8888"
