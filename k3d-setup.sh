#!/usr/bin/env bash
# =============================================================================
# k3d-setup.sh — One-shot local deployment on Kubernetes (k3d / k3s)
#
# Only requirement: Docker must be running on your machine.
# k3d and kubectl will be installed automatically if missing.
#
# Supports: macOS · Linux · Windows (Git Bash / WSL)
#
# Usage:  bash k3d-setup.sh
# =============================================================================

if [ -z "${BASH_VERSION:-}" ]; then exec bash "$0" "$@"; fi
set -euo pipefail

# Colours
RED='\033[0;31m';   GREEN='\033[0;32m';  YELLOW='\033[1;33m'
CYAN='\033[0;36m';  BLUE='\033[0;34m';   MAGENTA='\033[0;35m'
BOLD='\033[1m';     DIM='\033[2m';        NC='\033[0m'

# UI helpers
TOTAL_STEPS=10
CURRENT_STEP=0
STEP_START=0
GLOBAL_START=$SECONDS

hr()      { printf "${DIM}%s${NC}\n" "──────────────────────────────────────────────────────────────"; }
info()    { printf "  ${CYAN}→${NC}  %s\n" "$*"; }
success() { printf "  ${GREEN}✓${NC}  %s\n" "$*"; }
warn()    { printf "  ${YELLOW}!${NC}  %s\n" "$*"; }
error()   { printf "\n  ${RED}✗  ERROR:${NC} %s\n\n" "$*"; exit 1; }

step() {
  CURRENT_STEP=$((CURRENT_STEP + 1))
  STEP_START=$SECONDS
  printf "\n${BOLD}  [%d/%d]${NC}  %s\n" "$CURRENT_STEP" "$TOTAL_STEPS" "$*"
}

step_done() {
  local elapsed=$((SECONDS - STEP_START))
  printf "  ${DIM}└─ done in %ds${NC}\n" "$elapsed"
}

# Spinner for long-running background waits
spinner() {
  local msg="$1"
  local chars='/-\|'
  local i=0
  while true; do
    printf "\r  ${CYAN}%s${NC}  %s " "${chars:$((i % 4)):1}" "$msg"
    sleep 0.15
    i=$((i + 1))
  done
}

start_spinner() { spinner "$1" & SPINNER_PID=$!; }
stop_spinner()  {
  kill "$SPINNER_PID" 2>/dev/null || true
  wait "$SPINNER_PID" 2>/dev/null || true
  printf "\r%-60s\r" " "
}

elapsed_fmt() {
  local s=$1
  if [ "$s" -ge 60 ]; then printf "%dm %ds" $((s/60)) $((s%60))
  else printf "%ds" "$s"; fi
}

# Config
CLUSTER_NAME="ecommerce"
NAMESPACE="ecommerce"
REGISTRY="ghcr.io/hoangtien2k3"
HOSTS_ENTRY="127.0.0.1 ecommerce.local api.ecommerce.local auth.ecommerce.local zipkin.ecommerce.local minio.ecommerce.local"

SERVICES=(
  api-gateway auth-service product-service order-service
  payment-service shipping-service inventory-service favourite-service
  rating-service media-service tax-service promotion-service
  search-service notification-service
)

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
}

# Install binary
install_bin() {
  local name="$1" url="$2" dest
  if [ -w "/usr/local/bin" ] || sudo -n true 2>/dev/null; then
    dest="/usr/local/bin/${name}"
    info "Downloading ${name}..."
    curl -fsSL "$url" -o "/tmp/${name}"
    chmod +x "/tmp/${name}"
    sudo mv "/tmp/${name}" "$dest" 2>/dev/null || mv "/tmp/${name}" "$dest"
  else
    dest="${HOME}/.local/bin/${name}"
    mkdir -p "${HOME}/.local/bin"
    info "Downloading ${name} to ~/.local/bin..."
    curl -fsSL "$url" -o "$dest"
    chmod +x "$dest"
    export PATH="${HOME}/.local/bin:${PATH}"
  fi
  success "${name} installed → ${dest}"
}

install_k3d() {
  local K3D_VERSION="v5.8.3"
  local ext=""; [ "$OS" = "windows" ] && ext=".exe"
  install_bin "k3d${ext}" \
    "https://github.com/k3d-io/k3d/releases/download/${K3D_VERSION}/k3d-${OS}-${ARCH}${ext}"
}

install_kubectl() {
  local ext=""; [ "$OS" = "windows" ] && ext=".exe"
  local ver; ver=$(curl -fsSL https://dl.k8s.io/release/stable.txt)
  install_bin "kubectl${ext}" \
    "https://dl.k8s.io/release/${ver}/bin/${OS}/${ARCH}/kubectl${ext}"
}

# kubectl wait with retry
wait_for_pod() {
  local label="$1" name="$2" timeout="${3:-300s}"
  for i in 1 2 3; do
    if kubectl wait pod -n "$NAMESPACE" -l "app=${label}" \
        --for=condition=Ready --timeout="$timeout" 2>/dev/null; then
      return 0
    fi
    warn "${name}: attempt ${i}/3 timed out — retrying..."
  done
  stop_spinner 2>/dev/null || true
  error "${name} failed to become ready.\n     Run: kubectl describe pod -n ${NAMESPACE} -l app=${label}"
}

deploy_and_wait() {
  local yaml="$1" label="$2" name="$3" timeout="${4:-300s}"
  kubectl apply -f "$yaml" > /dev/null
  start_spinner "Waiting for ${name}..."
  wait_for_pod "$label" "$name" "$timeout"
  stop_spinner
  success "${name} is ready"
}

# Update /etc/hosts
update_hosts() {
  local entry="$1"
  if [ "$OS" = "windows" ]; then
    if grep -qF "ecommerce.local" "/c/Windows/System32/drivers/etc/hosts" 2>/dev/null; then
      warn "Hosts file already configured — skipping"
    else
      warn "Run this as Administrator in PowerShell:"
      printf "\n     Add-Content -Path C:\\Windows\\System32\\drivers\\etc\\hosts -Value '%s'\n\n" "$entry"
    fi
  else
    if grep -qF "ecommerce.local" /etc/hosts 2>/dev/null; then
      warn "Hosts file already configured — skipping"
    else
      printf "%s\n" "$entry" | sudo tee -a /etc/hosts > /dev/null
      success "/etc/hosts updated"
    fi
  fi
}

# BANNER
detect_os_arch

printf "\n"
hr
printf "${BOLD}${CYAN} Ecommerce Microservices · K8s Setup ${NC}\n"
printf "  ${DIM}Platform  ${NC}  ${OS} · ${ARCH}\n"
printf "  ${DIM}Cluster   ${NC}  ${CLUSTER_NAME}\n"
printf "  ${DIM}Started   ${NC}  $(date '+%H:%M:%S')\n"
hr

# 1. Detect environment
step "Detect environment"
success "OS=${OS}  ARCH=${ARCH}"
step_done

# 2. Docker
step "Check Docker"
command -v docker &>/dev/null \
  || error "Docker not found.\n     Install Docker Desktop: https://www.docker.com/get-started"
docker info &>/dev/null \
  || error "Docker is not running.\n     Please start Docker Desktop and try again."
success "Docker is running  ($(docker version --format '{{.Server.Version}}' 2>/dev/null || echo 'unknown'))"
step_done

# 3. k3d
step "Check k3d"
if command -v k3d &>/dev/null; then
  success "$(k3d version | head -1)"
else
  warn "k3d not found — installing automatically..."
  install_k3d
fi
step_done

# 4. kubectl
step "Check kubectl"
if command -v kubectl &>/dev/null; then
  success "$(kubectl version --client 2>/dev/null | head -1)"
else
  warn "kubectl not found — installing automatically..."
  install_kubectl
fi
step_done

# 5. k3d cluster
step "Set up k3d cluster"
if k3d cluster list 2>/dev/null | grep -q "^${CLUSTER_NAME}"; then
  if k3d cluster list 2>/dev/null | grep "^${CLUSTER_NAME}" | grep -q "running"; then
    warn "Cluster '${CLUSTER_NAME}' already running — skipping creation"
  else
    start_spinner "Cluster stopped — restarting '${CLUSTER_NAME}'..."
    k3d cluster start "$CLUSTER_NAME" &>/dev/null
    stop_spinner
    success "Cluster started"
  fi
else
  start_spinner "Creating cluster '${CLUSTER_NAME}'..."
  k3d cluster create --config k3d-config.yaml &>/dev/null
  stop_spinner
  success "Cluster '${CLUSTER_NAME}' created"
fi
kubectl config use-context "k3d-${CLUSTER_NAME}" > /dev/null
start_spinner "Waiting for API server..."
until kubectl get nodes &>/dev/null 2>&1; do sleep 2; done
stop_spinner
success "API server is ready"
step_done

# 6. NGINX Ingress Controller
step "Install NGINX Ingress Controller"
if kubectl get ns ingress-nginx &>/dev/null; then
  warn "ingress-nginx already installed — skipping"
else
  info "Applying NGINX Ingress manifests..."
  kubectl apply --validate=false \
    -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/cloud/deploy.yaml \
    > /dev/null
fi
start_spinner "Waiting for ingress controller..."
kubectl wait --namespace ingress-nginx \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/component=controller \
  --timeout=120s > /dev/null
stop_spinner
success "NGINX Ingress Controller is ready"
step_done

# 7. Namespace + Secrets + ConfigMaps
step "Apply Namespace, Secrets & ConfigMaps"
kubectl apply -f k8s/namespace.yaml > /dev/null
success "Namespace '${NAMESPACE}'"

if kubectl get secret postgres-secret -n "$NAMESPACE" &>/dev/null; then
  warn "Secrets already exist — skipping  (delete manually to reset)"
else
  if [ -f .env ]; then
    info "Loading secrets from .env..."
    set -a; source .env; set +a
    kubectl create secret generic postgres-secret      -n "$NAMESPACE" --from-literal=POSTGRES_USER="${POSTGRES_USER:-postgres}"         --from-literal=POSTGRES_PASSWORD="${POSTGRES_PASSWORD:-ecommerce@!@#}" > /dev/null
    kubectl create secret generic keycloak-secret      -n "$NAMESPACE" --from-literal=KEYCLOAK_ADMIN="${KEYCLOAK_ADMIN:-admin}"           --from-literal=KEYCLOAK_ADMIN_PASSWORD="${KEYCLOAK_ADMIN_PASSWORD:-ecommerce@!@#}" > /dev/null
    kubectl create secret generic redis-secret         -n "$NAMESPACE" --from-literal=REDIS_PASSWORD="${REDIS_PASSWORD:-ecommerce@!@#}" > /dev/null
    kubectl create secret generic minio-secret         -n "$NAMESPACE" --from-literal=MINIO_ROOT_USER="${MINIO_ROOT_USER:-admin}"         --from-literal=MINIO_ROOT_PASSWORD="${MINIO_ROOT_PASSWORD:-ecommerce@!@#}" > /dev/null
    kubectl create secret generic elasticsearch-secret -n "$NAMESPACE" --from-literal=ELASTIC_PASSWORD="${ELASTIC_PASSWORD:-ecommerce@!@#}" > /dev/null
    kubectl create secret generic mail-secret          -n "$NAMESPACE" --from-literal=MAIL_USERNAME="${MAIL_USERNAME:-}"                  --from-literal=MAIL_PASSWORD="${MAIL_PASSWORD:-}" > /dev/null
  else
    info "No .env found — using k8s/secrets.yaml (default dev values)"
    kubectl apply -f k8s/secrets.yaml > /dev/null
  fi
  success "Secrets created"
fi

kubectl apply -f k8s/configmap.yaml > /dev/null
kubectl create configmap keycloak-realm        -n "$NAMESPACE" \
  --from-file=ecommerce-realm.json=docker/keycloak/import/ecommerce-realm.json \
  --dry-run=client -o yaml | kubectl apply -f - > /dev/null
kubectl create configmap postgres-init-scripts -n "$NAMESPACE" \
  --from-file=create-all-databases.sql=docker/postgres/init/create-all-databases.sql \
  --dry-run=client -o yaml | kubectl apply -f - > /dev/null
success "ConfigMaps applied"
step_done

# 8. Infrastructure
step "Deploy infrastructure"
deploy_and_wait k8s/infra/postgres.yaml       postgres       "PostgreSQL"    600s
deploy_and_wait k8s/infra/redis.yaml          redis          "Redis"         300s
deploy_and_wait k8s/infra/kafka.yaml          kafka          "Kafka"         300s
deploy_and_wait k8s/infra/elasticsearch.yaml  elasticsearch  "Elasticsearch" 600s
deploy_and_wait k8s/infra/minio.yaml          minio          "MinIO"         300s
deploy_and_wait k8s/infra/keycloak.yaml       keycloak       "Keycloak"      600s
deploy_and_wait k8s/infra/zipkin.yaml         zipkin         "Zipkin"        300s
step_done

# 9. Backend + Frontend + Ingress
step "Deploy backend services & frontend"
for yaml in k8s/backend/*.yaml; do
  kubectl apply -f "$yaml" > /dev/null
done
kubectl apply -f k8s/frontend/frontend.yaml > /dev/null
kubectl apply -f k8s/ingress/ingress.yaml   > /dev/null
success "14 backend services + frontend applied"
info   "Pods are pulling images from GHCR (imagePullPolicy: Always)"
step_done

# 10. /etc/hosts
step "Update hosts file"
update_hosts "$HOSTS_ENTRY"
step_done

# Summary
TOTAL_ELAPSED=$(elapsed_fmt $((SECONDS - GLOBAL_START)))

printf "\n"
hr
printf "  ${BOLD}${GREEN}✓ Deployment complete!${NC}  ${DIM}Total time: %s${NC}\n" "$TOTAL_ELAPSED"
printf "\n"
printf "  ${BOLD}%-16s${NC}  %s\n" "Service" "URL"
printf "  ${DIM}%-16s  %s${NC}\n"  "───────────────" "───────────────────────────────────────────"
printf "  ${BOLD}%-16s${NC}  ${CYAN}%s${NC}\n" "Frontend"       "http://ecommerce.local:9090"
printf "  ${BOLD}%-16s${NC}  ${CYAN}%s${NC}\n" "API Gateway"    "http://api.ecommerce.local:9090"
printf "  ${BOLD}%-16s${NC}  ${CYAN}%s${NC}\n" "Keycloak"       "http://auth.ecommerce.local:9090"
printf "  ${BOLD}%-16s${NC}  ${CYAN}%s${NC}\n" "MinIO Console"  "http://minio.ecommerce.local:9090"
printf "  ${BOLD}%-16s${NC}  ${CYAN}%s${NC}\n" "Zipkin"         "http://zipkin.ecommerce.local:9090"
printf "\n"
printf "  ${DIM}Pods may take 1-2 min to pull images and become ready.${NC}\n"
printf "  ${DIM}Monitor: kubectl get pods -n %s -w${NC}\n" "$NAMESPACE"
hr
