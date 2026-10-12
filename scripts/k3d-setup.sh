#!/usr/bin/env bash
# =============================================================================
# k3d-setup.sh — One-shot local deployment on Kubernetes (k3d / k3s)
#
# Container runtime: Docker or Podman, auto-detected (Docker wins when both
# are available). Force one with:  RUNTIME=podman bash scripts/k3d-setup.sh
# k3d and kubectl will be installed automatically if missing.
#
# Supports: macOS · Linux · Windows (Git Bash / WSL)
#
# Usage:  bash scripts/k3d-setup.sh
# =============================================================================

if [ -z "${BASH_VERSION:-}" ]; then exec bash "$0" "$@"; fi
set -euo pipefail

cd "$(dirname "$0")/.."

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
HOSTS_ENTRY="127.0.0.1 ecommerce.local admin.ecommerce.local api.ecommerce.local keycloak.ecommerce.local rustfs.ecommerce.local"


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

# Wait for an already-applied workload (used when manifests are applied up front so
# their image pulls / startup overlap, then awaited concurrently).
wait_only() {
  local label="$1" name="$2" timeout="${3:-300s}"
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
    if grep -qxF "$entry" /etc/hosts 2>/dev/null; then
      warn "Hosts file already up to date — skipping"
    else
      # Replace any stale managed line (old hostnames change between versions) then
      # append the current one, so the entry never drifts out of date.
      sudo sed -i.bak '/[[:space:]]ecommerce\.local[[:space:]]/d;/[[:space:]]ecommerce\.local$/d' /etc/hosts 2>/dev/null || true
      printf "%s\n" "$entry" | sudo tee -a /etc/hosts > /dev/null
      success "/etc/hosts updated"
    fi
  fi
}

# Container runtime detection — Docker preferred, Podman supported.
docker_ready()   { command -v docker &>/dev/null && docker info &>/dev/null; }
podman_present() { command -v podman &>/dev/null; }

# Point Docker-compatible clients (k3d) at the Podman API and, on Windows /
# macOS, make sure the Podman machine is running in rootful mode.
setup_podman() {
  podman_present || error "Podman not found.\n     Install Podman: https://podman.io/getting-started/installation"

  case "$OS" in
    windows)
      # Windows + Podman Desktop: k3d talks to the Podman Machine over its named pipe.
      local machine rootful state pipe
      machine="$(podman machine list --format '{{.Name}}' 2>/dev/null | head -1 | tr -d '*[:space:]')"
      [ -n "$machine" ] || error "No Podman machine found.\n     Create one first: podman machine init --rootful"

      rootful="$(podman machine inspect "$machine" --format '{{.Rootful}}' 2>/dev/null | tr -d '[:space:]')"
      if [ "$rootful" != "true" ]; then
        warn "Podman machine '$machine' is rootless — k3d requires rootful; reconfiguring..."
        podman machine stop "$machine" &>/dev/null || true
        podman machine set --rootful "$machine" &>/dev/null \
          || error "Failed to enable rootful mode.\n     Run: podman machine stop $machine && podman machine set --rootful $machine"
      fi

      state="$(podman machine inspect "$machine" --format '{{.State}}' 2>/dev/null | tr -d '[:space:]')"
      if [ "$state" != "running" ]; then
        start_spinner "Starting Podman machine '$machine'..."
        podman machine start "$machine" &>/dev/null || { stop_spinner; error "Failed to start Podman machine '$machine'."; }
        stop_spinner
      fi

      pipe="$(podman machine inspect "$machine" --format '{{.ConnectionInfo.PodmanPipe.Path}}' 2>/dev/null | head -1 | tr -d '\r')"
      [ -n "$pipe" ] || pipe='\\.\pipe\podman-machine-default'
      export DOCKER_HOST="npipe://$(printf '%s' "$pipe" | tr '\\' '/')"

      # Prepare the VM for k3d + OpenSearch: keep the API socket alive,
      # expose docker.sock and raise vm.max_map_count.
      podman machine ssh "$machine" "sudo mkdir -p /etc/containers/containers.conf.d && printf '[engine]\nservice_timeout=0\n' | sudo tee /etc/containers/containers.conf.d/timeout.conf >/dev/null; sudo systemctl enable --now podman.socket >/dev/null 2>&1 || true; sudo ln -sf /run/podman/podman.sock /var/run/docker.sock 2>/dev/null || true; sudo sysctl -w vm.max_map_count=262144 >/dev/null 2>&1 || true" &>/dev/null || true
      ;;
    darwin)
      # macOS: Podman runs inside a VM — use the machine's socket.
      local socket
      socket="$(podman machine inspect --format '{{.ConnectionInfo.PodmanSocket.Path}}' 2>/dev/null | head -1 | tr -d '\r')"
      if [ -z "$socket" ]; then
        podman machine start &>/dev/null || true
        socket="$(podman machine inspect --format '{{.ConnectionInfo.PodmanSocket.Path}}' 2>/dev/null | head -1 | tr -d '\r')"
      fi
      [ -n "$socket" ] || error "Could not determine the Podman socket.\n     Check: podman machine inspect"
      export DOCKER_HOST="unix://$socket"
      ;;
    *)
      # Linux: use the systemd-managed Podman API socket.
      local uid
      uid="$(id -u)"
      if [ -z "${DOCKER_HOST:-}" ]; then
        if [ -S "${XDG_RUNTIME_DIR:-/run/user/$uid}/podman/podman.sock" ]; then
          export DOCKER_HOST="unix://${XDG_RUNTIME_DIR:-/run/user/$uid}/podman/podman.sock"
        elif [ -S /run/podman/podman.sock ]; then
          export DOCKER_HOST="unix:///run/podman/podman.sock"
        else
          error "Podman API socket not found.\n     Start it with: systemctl --user start podman.socket"
        fi
      fi
      ;;
  esac

  success "Podman is running  (DOCKER_HOST=$DOCKER_HOST)"
}

# BANNER
detect_os_arch

printf "\n"
hr
printf "${BOLD}${CYAN} Ecommerce Microservices · K8s Setup ${NC}\n"
printf "  ${DIM}Platform  ${NC}  ${OS} · ${ARCH}\n"
printf "  ${DIM}Runtime   ${NC}  %s\n" "${RUNTIME:-auto} (auto-detect, Docker preferred)"
printf "  ${DIM}Cluster   ${NC}  ${CLUSTER_NAME}\n"
printf "  ${DIM}Started   ${NC}  $(date '+%H:%M:%S')\n"
hr

# 1. Detect environment
step "Detect environment"
success "OS=${OS}  ARCH=${ARCH}"
step_done

# 2. Container runtime (Docker preferred, Podman supported)
step "Check container runtime"
RUNTIME="${RUNTIME:-auto}"
case "$RUNTIME" in
  docker)
    docker_ready || error "Docker is installed but its daemon is not reachable.\n     Start Docker Desktop and try again, or use: RUNTIME=podman bash scripts/k3d-setup.sh"
    success "Docker is running  ($(docker version --format '{{.Server.Version}}' 2>/dev/null || echo 'unknown'))"
    ;;
  podman)
    setup_podman
    ;;
  *)
    if docker_ready; then
      success "Docker is running  ($(docker version --format '{{.Server.Version}}' 2>/dev/null || echo 'unknown'))"
      RUNTIME="docker"
    elif podman_present; then
      setup_podman
      RUNTIME="podman"
    else
      error "No container runtime found.\n     Install Docker (https://www.docker.com/get-started) or Podman (https://podman.io/getting-started/installation)"
    fi
    ;;
esac
success "Container runtime: $RUNTIME"
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
  if [ "$RUNTIME" = "podman" ]; then
    # Podman needs the default bridge network before k3d creates the cluster network
    podman network inspect podman &>/dev/null || podman network create podman &>/dev/null || true
  fi
  start_spinner "Creating cluster '${CLUSTER_NAME}'..."
  k3d cluster create --config scripts/k3d-config.yaml &>/dev/null
  stop_spinner
  success "Cluster '${CLUSTER_NAME}' created"
fi
k3d kubeconfig merge "$CLUSTER_NAME" --kubeconfig-merge-default --kubeconfig-switch-context &>/dev/null || true
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
kubectl apply -f deploy/k8s/namespace.yaml > /dev/null
success "Namespace '${NAMESPACE}'"

# Remove workloads that were dropped from the project so re-runs on an existing
# cluster don't leave zombie pods holding memory (api-gateway, MinIO and Zipkin
# are gone; MinIO was replaced by RustFS).
kubectl delete deploy,svc,statefulset api-gateway minio zipkin \
  -n "$NAMESPACE" --ignore-not-found > /dev/null 2>&1 || true

# Secrets — idempotent (apply, never skip) so secrets added later (e.g. storage-secret)
# are always present even when the cluster was created by an earlier run.
if [ -f .env ]; then
  info "Loading secrets from .env..."
  set -a; source .env; set +a
  mk_secret() { kubectl create secret generic "$@" -n "$NAMESPACE" --dry-run=client -o yaml | kubectl apply -f - > /dev/null; }
  mk_secret postgres-secret      --from-literal=POSTGRES_USER="${POSTGRES_USER:-admin}"         --from-literal=POSTGRES_PASSWORD="${POSTGRES_PASSWORD:-admin}"
  mk_secret keycloak-secret      --from-literal=KEYCLOAK_ADMIN="${KEYCLOAK_ADMIN:-admin}"           --from-literal=KEYCLOAK_ADMIN_PASSWORD="${KEYCLOAK_ADMIN_PASSWORD:-admin}"
  mk_secret redis-secret         --from-literal=REDIS_PASSWORD="${REDIS_PASSWORD:-admin}"
  mk_secret storage-secret       --from-literal=STORAGE_ACCESS_KEY="${STORAGE_ACCESS_KEY:-admin}"     --from-literal=STORAGE_SECRET_KEY="${STORAGE_SECRET_KEY:-admin}"
  mk_secret opensearch-secret    --from-literal=OPENSEARCH_INITIAL_ADMIN_PASSWORD="${OPENSEARCH_PASSWORD:-${ELASTIC_PASSWORD:-admin}}"
  mk_secret mail-secret          --from-literal=MAIL_USERNAME="${MAIL_USERNAME:-}"                  --from-literal=MAIL_PASSWORD="${MAIL_PASSWORD:-}"
else
  info "No .env found — applying deploy/k8s/secrets.yaml (default dev values)"
  kubectl apply -f deploy/k8s/secrets.yaml > /dev/null
fi
success "Secrets applied"

kubectl apply -f deploy/k8s/configmap.yaml > /dev/null
kubectl create configmap keycloak-realm        -n "$NAMESPACE" \
  --from-file=ecommerce-realm.json=deploy/keycloak/import/ecommerce-realm.json \
  --dry-run=client -o yaml | kubectl apply -f - > /dev/null
kubectl create configmap postgres-init-scripts -n "$NAMESPACE" \
  --from-file=create-all-databases.sql=deploy/postgres/init/create-all-databases.sql \
  --dry-run=client -o yaml | kubectl apply -f - > /dev/null
# Apache APISIX standalone config (data plane reads these two files directly).
kubectl create configmap apisix-config         -n "$NAMESPACE" \
  --from-file=config.yaml=deploy/apisix/config.yaml \
  --dry-run=client -o yaml | kubectl apply -f - > /dev/null
kubectl create configmap apisix-routes         -n "$NAMESPACE" \
  --from-file=apisix.yaml=deploy/apisix/apisix.yaml \
  --dry-run=client -o yaml | kubectl apply -f - > /dev/null
success "ConfigMaps applied"
step_done

# 8. Infrastructure — fire-and-forget: apply everything, let Kubernetes converge on
# its own. Pod ordering is handled by the pods (Keycloak + backends carry
# wait-for-postgres / wait-for-kafka initContainers), so we do NOT block on readiness.
step "Deploy infrastructure"
for f in postgres redis kafka opensearch rustfs keycloak; do
  kubectl apply -f "deploy/k8s/infra/${f}.yaml" > /dev/null
done
success "Infrastructure manifests applied (pods start in the background)"
step_done

# 9. API gateway + Backend + Frontend + Ingress — fire-and-forget as well.
step "Deploy API gateway, backend services & frontend"
for yaml in deploy/k8s/backend/*.yaml; do
  kubectl apply -f "$yaml" > /dev/null
done
# Ingress BEFORE APISIX: reconcile the shared ecommerce-ingress first so the
# apisix-api Ingress (api.ecommerce.local) can't clash with a stale host rule.
kubectl apply -f deploy/k8s/ingress/ingress.yaml   > /dev/null
kubectl apply -f deploy/k8s/infra/apisix.yaml      > /dev/null   # Apache APISIX (standalone edge)
kubectl apply -f deploy/k8s/frontend/frontend.yaml > /dev/null
success "API gateway + 11 backend services + frontend applied"
info   "Pods pull images & start in the background — nothing to wait for here"
step_done

# 10. /etc/hosts
step "Update hosts file"
update_hosts "$HOSTS_ENTRY"
step_done

# Summary
TOTAL_ELAPSED=$(elapsed_fmt $((SECONDS - GLOBAL_START)))

printf "\n"
hr
printf "  ${BOLD}${GREEN}✓ All manifests applied!${NC}  ${DIM}Total time: %s${NC}\n" "$TOTAL_ELAPSED"
printf "  ${DIM}Kubernetes now pulls images & starts pods in the background — they come up on their own.${NC}\n"
printf "\n"
printf "  ${BOLD}%-16s${NC}  %s\n" "Service" "URL"
printf "  ${DIM}%-16s  %s${NC}\n"  "───────────────" "───────────────────────────────────────────"
printf "  ${BOLD}%-16s${NC}  ${CYAN}%s${NC}\n" "Frontend"       "http://ecommerce.local"
printf "  ${BOLD}%-16s${NC}  ${CYAN}%s${NC}\n" "Admin"          "http://admin.ecommerce.local"
printf "  ${BOLD}%-16s${NC}  ${CYAN}%s${NC}\n" "API Gateway"    "http://api.ecommerce.local"
printf "  ${BOLD}%-16s${NC}  ${CYAN}%s${NC}\n" "Keycloak"       "http://keycloak.ecommerce.local"
printf "  ${BOLD}%-16s${NC}  ${CYAN}%s${NC}\n" "RustFS Console" "http://rustfs.ecommerce.local/rustfs/console/"
printf "\n"
printf "  ${DIM}Pods are still pulling/starting — give them a few minutes.${NC}\n"
printf "  ${DIM}Watch:        kubectl get pods -n %s -w${NC}\n" "$NAMESPACE"
printf "  ${DIM}Wait-all:     kubectl wait --for=condition=Ready pod --all -n %s --timeout=600s${NC}\n" "$NAMESPACE"
hr
