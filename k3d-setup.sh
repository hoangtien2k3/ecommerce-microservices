#!/usr/bin/env bash
# =============================================================================
# k3d-setup.sh — One-shot local deployment on Kubernetes (k3d / k3s)
#
# Only requirement: Docker must be running on your machine.
# k3d and kubectl will be installed automatically if missing.
#
# Supports: macOS · Linux · Windows (Git Bash / WSL)
#
# Usage:
#   bash k3d-setup.sh
# =============================================================================

# Re-exec with bash if invoked via plain sh
if [ -z "${BASH_VERSION:-}" ]; then
  exec bash "$0" "$@"
fi

set -euo pipefail

# Colours
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'
CYAN='\033[0;36m'; BOLD='\033[1m'; NC='\033[0m'

info()    { printf "${CYAN}[INFO]${NC}  %s\n" "$*"; }
success() { printf "${GREEN}[OK]${NC}    %s\n" "$*"; }
warn()    { printf "${YELLOW}[WARN]${NC}  %s\n" "$*"; }
error()   { printf "${RED}[ERROR]${NC} %s\n" "$*"; exit 1; }
step()    { printf "\n${BOLD}▶ %s${NC}\n" "$*"; }

# Detect OS & Architecture
detect_os_arch() {
  case "$(uname -s)" in
    Darwin*)              OS="darwin"  ;;
    Linux*)               OS="linux"   ;;
    MINGW*|MSYS*|CYGWIN*) OS="windows" ;;
    *)                    error "Unsupported OS: $(uname -s)" ;;
  esac
  case "$(uname -m)" in
    x86_64|amd64)  ARCH="amd64" ;;
    arm64|aarch64) ARCH="arm64" ;;
    *)             error "Unsupported architecture: $(uname -m)" ;;
  esac
  info "Detected: OS=${OS}, ARCH=${ARCH}"
}

# Install binary
install_bin() {
  local name="$1" url="$2" dest
  if [ -w "/usr/local/bin" ] || sudo -n true 2>/dev/null; then
    dest="/usr/local/bin/${name}"
    info "Installing ${name} to /usr/local/bin..."
    curl -fsSL "$url" -o "/tmp/${name}"
    chmod +x "/tmp/${name}"
    sudo mv "/tmp/${name}" "$dest" 2>/dev/null || mv "/tmp/${name}" "$dest"
  else
    dest="${HOME}/.local/bin/${name}"
    mkdir -p "${HOME}/.local/bin"
    info "Installing ${name} to ~/.local/bin (no sudo)..."
    curl -fsSL "$url" -o "$dest"
    chmod +x "$dest"
    export PATH="${HOME}/.local/bin:${PATH}"
  fi
  success "${name} installed → $dest"
}

# Auto-install k3d
install_k3d() {
  local K3D_VERSION="v5.8.3"
  local ext=""; [ "$OS" = "windows" ] && ext=".exe"
  install_bin "k3d${ext}" \
    "https://github.com/k3d-io/k3d/releases/download/${K3D_VERSION}/k3d-${OS}-${ARCH}${ext}"
}

# Auto-install kubectl
install_kubectl() {
  local ext=""; [ "$OS" = "windows" ] && ext=".exe"
  local ver; ver=$(curl -fsSL https://dl.k8s.io/release/stable.txt)
  install_bin "kubectl${ext}" \
    "https://dl.k8s.io/release/${ver}/bin/${OS}/${ARCH}/kubectl${ext}"
}

# kubectl wait with retry (handles transient API timeouts)
wait_for_pod() {
  local label="$1" name="$2" timeout="${3:-300s}"
  for i in 1 2 3; do
    if kubectl wait pod -n "$NAMESPACE" -l "app=${label}" \
        --for=condition=Ready --timeout="$timeout" 2>/dev/null; then
      success "${name} ready"
      return 0
    fi
    warn "${name}: attempt ${i}/3 timed out — retrying..."
  done
  error "${name} failed to become ready. Check: kubectl describe pod -n ${NAMESPACE} -l app=${label}"
}

deploy_and_wait() {
  local yaml="$1" label="$2" name="$3" timeout="${4:-300s}"
  kubectl apply -f "$yaml"
  info "Waiting for ${name}..."
  wait_for_pod "$label" "$name" "$timeout"
}

# Update /etc/hosts───
update_hosts() {
  local entry="$1"
  if [ "$OS" = "windows" ]; then
    if grep -qF "ecommerce.local" "/c/Windows/System32/drivers/etc/hosts" 2>/dev/null; then
      warn "Hosts file already has ecommerce entries — skipping"
    else
      warn "Windows: please run the following as Administrator in PowerShell:"
      printf "\n    Add-Content -Path C:\\Windows\\System32\\drivers\\etc\\hosts -Value '%s'\n\n" "$entry"
    fi
  else
    if grep -qF "ecommerce.local" /etc/hosts 2>/dev/null; then
      warn "Hosts file already has ecommerce entries — skipping"
    else
      info "Appending to /etc/hosts (requires sudo)..."
      printf "%s\n" "$entry" | sudo tee -a /etc/hosts > /dev/null
      success "/etc/hosts updated"
    fi
  fi
}

# ═════════════════════════════════════════════════════════════════════════════
# Config
# ═════════════════════════════════════════════════════════════════════════════
CLUSTER_NAME="ecommerce"
NAMESPACE="ecommerce"
REGISTRY="ghcr.io/hoangtien2k3"
HOSTS_ENTRY="127.0.0.1 ecommerce.local api.ecommerce.local auth.ecommerce.local zipkin.ecommerce.local minio.ecommerce.local"

INFRA_IMAGES=(
  "postgres:16"
  "redis:7.4-alpine"
  "apache/kafka:3.9.0"
  "docker.elastic.co/elasticsearch/elasticsearch:8.15.0"
  "minio/minio:latest"
  "quay.io/keycloak/keycloak:26.0"
  "openzipkin/zipkin:3"
)

SERVICES=(
  api-gateway auth-service product-service order-service
  payment-service shipping-service inventory-service favourite-service
  rating-service media-service tax-service promotion-service
  search-service notification-service
)

# ═════════════════════════════════════════════════════════════════════════════
printf "\n"
printf "${BOLD}${CYAN}╔══════════════════════════════════════════════════╗${NC}\n"
printf "${BOLD}${CYAN}║   Ecommerce Microservices — k3d Setup            ║${NC}\n"
printf "${BOLD}${CYAN}╚══════════════════════════════════════════════════╝${NC}\n\n"

# 1. Detect OS
step "1/11 · Detecting environment"
detect_os_arch

# 2. Docker
step "2/11 · Checking Docker"
command -v docker &>/dev/null \
  || error "Docker not found. Install Docker Desktop at: https://www.docker.com/get-started"
docker info &>/dev/null \
  || error "Docker is not running. Please start Docker Desktop and try again."
success "Docker is running"

# 3. k3d
step "3/11 · Checking k3d"
if command -v k3d &>/dev/null; then
  success "k3d already installed: $(k3d version | head -1)"
else
  warn "k3d not found — installing automatically..."
  install_k3d
fi

# 4. kubectl
step "4/11 · Checking kubectl"
if command -v kubectl &>/dev/null; then
  success "kubectl already installed: $(kubectl version --client 2>/dev/null | head -1)"
else
  warn "kubectl not found — installing automatically..."
  install_kubectl
fi

# 5. k3d cluster
step "5/11 · Setting up k3d cluster"
if k3d cluster list 2>/dev/null | grep -q "^${CLUSTER_NAME}"; then
  warn "Cluster '${CLUSTER_NAME}' already exists — skipping"
else
  info "Creating cluster '${CLUSTER_NAME}' with k3d..."
  k3d cluster create --config k3d-config.yaml
  success "Cluster created"
fi
kubectl config use-context "k3d-${CLUSTER_NAME}"

# 6. NGINX Ingress Controller
step "6/11 · Installing NGINX Ingress Controller"
if kubectl get ns ingress-nginx &>/dev/null; then
  warn "ingress-nginx already installed — skipping"
else
  # Use cloud/LoadBalancer manifest so k3d's built-in lb picks it up
  kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/cloud/deploy.yaml
fi
info "Waiting for ingress controller to be ready..."
kubectl wait --namespace ingress-nginx \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/component=controller \
  --timeout=120s
success "Ingress controller ready"

# 7. Pre-load images (best-effort)
step "7/11 · Pre-loading images into k3d cluster"
info "Pulling and loading infrastructure images..."
for img in "${INFRA_IMAGES[@]}"; do
  if docker pull "$img" --quiet 2>/dev/null; then
    k3d image import "$img" -c "$CLUSTER_NAME" 2>/dev/null \
      && info "Loaded ${img}" \
      || warn "k3d import failed for ${img} — will pull at runtime"
  else
    warn "Could not pull ${img} — will pull at runtime"
  fi
done

# Detect the SHA tag used in k8s manifests (e.g. sha-374ea13)
MANIFEST_SHA=$(grep -h "image:.*${REGISTRY}" k8s/backend/*.yaml k8s/frontend/*.yaml 2>/dev/null \
  | grep -o 'sha-[a-f0-9]*' | head -1 || true)
info "Manifest image tag: ${MANIFEST_SHA:-not detected, using latest}"

info "Loading service images into k3d..."
for svc in "${SERVICES[@]}" frontend; do
  img_latest="${REGISTRY}/${svc}:latest"
  img_sha="${REGISTRY}/${svc}:${MANIFEST_SHA}"

  # Pull :latest from GHCR (always exists), retag to SHA used in manifests, import
  if docker pull "$img_latest" --quiet 2>/dev/null; then
    if [ -n "$MANIFEST_SHA" ]; then
      docker tag "$img_latest" "$img_sha"
      k3d image import "$img_sha" -c "$CLUSTER_NAME" 2>/dev/null
      success "${svc}: GHCR :latest → :${MANIFEST_SHA} imported"
    else
      k3d image import "$img_latest" -c "$CLUSTER_NAME" 2>/dev/null
      success "${svc}: GHCR :latest imported"
    fi
  else
    warn "${svc}: could not pull from GHCR — pod will stay in ImagePullBackOff"
  fi
done

# 8. Namespace + Secrets + ConfigMaps
step "8/11 · Applying Namespace, Secrets, ConfigMaps"
kubectl apply -f k8s/namespace.yaml
success "Namespace '${NAMESPACE}' ready"

if kubectl get secret postgres-secret -n "$NAMESPACE" &>/dev/null; then
  warn "Secrets already exist — skipping (delete manually to reset)"
else
  if [ -f .env ]; then
    info "Loading secrets from .env file..."
    set -a; source .env; set +a
    kubectl create secret generic postgres-secret      -n "$NAMESPACE" --from-literal=POSTGRES_USER="${POSTGRES_USER:-postgres}"         --from-literal=POSTGRES_PASSWORD="${POSTGRES_PASSWORD:-ecommerce@!@#}"
    kubectl create secret generic keycloak-secret      -n "$NAMESPACE" --from-literal=KEYCLOAK_ADMIN="${KEYCLOAK_ADMIN:-admin}"           --from-literal=KEYCLOAK_ADMIN_PASSWORD="${KEYCLOAK_ADMIN_PASSWORD:-ecommerce@!@#}"
    kubectl create secret generic redis-secret         -n "$NAMESPACE" --from-literal=REDIS_PASSWORD="${REDIS_PASSWORD:-ecommerce@!@#}"
    kubectl create secret generic minio-secret         -n "$NAMESPACE" --from-literal=MINIO_ROOT_USER="${MINIO_ROOT_USER:-admin}"         --from-literal=MINIO_ROOT_PASSWORD="${MINIO_ROOT_PASSWORD:-ecommerce@!@#}"
    kubectl create secret generic elasticsearch-secret -n "$NAMESPACE" --from-literal=ELASTIC_PASSWORD="${ELASTIC_PASSWORD:-ecommerce@!@#}"
    kubectl create secret generic mail-secret          -n "$NAMESPACE" --from-literal=MAIL_USERNAME="${MAIL_USERNAME:-}"                  --from-literal=MAIL_PASSWORD="${MAIL_PASSWORD:-}"
  else
    info "No .env file found — applying k8s/secrets.yaml (default dev values)"
    kubectl apply -f k8s/secrets.yaml
  fi
fi
success "Secrets ready"

kubectl apply -f k8s/configmap.yaml
kubectl create configmap keycloak-realm        -n "$NAMESPACE" \
  --from-file=ecommerce-realm.json=docker/keycloak/import/ecommerce-realm.json \
  --dry-run=client -o yaml | kubectl apply -f -
kubectl create configmap postgres-init-scripts -n "$NAMESPACE" \
  --from-file=create-all-databases.sql=docker/postgres/init/create-all-databases.sql \
  --dry-run=client -o yaml | kubectl apply -f -
success "ConfigMaps ready"

# 9. Infrastructure
step "9/11 · Deploying infrastructure"
deploy_and_wait k8s/infra/postgres.yaml       postgres       "PostgreSQL"    600s
deploy_and_wait k8s/infra/redis.yaml          redis          "Redis"         300s
deploy_and_wait k8s/infra/kafka.yaml          kafka          "Kafka"         300s
deploy_and_wait k8s/infra/elasticsearch.yaml  elasticsearch  "Elasticsearch" 600s
deploy_and_wait k8s/infra/minio.yaml          minio          "MinIO"         300s
deploy_and_wait k8s/infra/keycloak.yaml       keycloak       "Keycloak"      600s
deploy_and_wait k8s/infra/zipkin.yaml         zipkin         "Zipkin"        300s

# 10. Backend + Frontend + Ingress
step "10/11 · Deploying backend services & frontend"
for yaml in k8s/backend/*.yaml; do
  kubectl apply -f "$yaml"
  info "Applied $(basename "$yaml" .yaml)"
done
kubectl apply -f k8s/frontend/frontend.yaml
kubectl apply -f k8s/ingress/ingress.yaml
success "All services applied"

# 11. /etc/hosts
step "11/11 · Updating hosts file"
update_hosts "$HOSTS_ENTRY"

# Done
printf "\n"
printf "${BOLD}${GREEN}╔══════════════════════════════════════════════════╗${NC}\n"
printf "${BOLD}${GREEN}║          Deployment complete!                    ║${NC}\n"
printf "${BOLD}${GREEN}╚══════════════════════════════════════════════════╝${NC}\n\n"
printf "${BOLD}Frontend${NC}       →  http://ecommerce.local:9090\n"
printf "${BOLD}API Gateway${NC}    →  http://api.ecommerce.local:9090\n"
printf "${BOLD}Keycloak${NC}       →  http://auth.ecommerce.local:9090\n"
printf "${BOLD}MinIO Console${NC}  →  http://minio.ecommerce.local:9090\n"
printf "${BOLD}Zipkin${NC}         →  http://zipkin.ecommerce.local:9090\n\n"
printf "${YELLOW}Backend services may take an additional 1-2 minutes to fully start.${NC}\n"
printf "${YELLOW}Monitor: kubectl get pods -n %s -w${NC}\n\n" "$NAMESPACE"
