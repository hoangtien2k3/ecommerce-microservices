#!/bin/bash
set -e

CLUSTER_NAME="ecommerce"
NAMESPACE="ecommerce"
REGISTRY="ghcr.io/hoangtien2k3"
SERVICES="discovery-service api-gateway auth-service product-service order-service \
          payment-service shipping-service inventory-service favourite-service \
          rating-service media-service tax-service promotion-service search-service \
          notification-service"

# --- Prerequisites ---
for cmd in kind kubectl docker; do
  command -v "$cmd" &>/dev/null || { echo "Error: '$cmd' is not installed."; exit 1; }
done

# --- Cluster ---
if kind get clusters 2>/dev/null | grep -q "^${CLUSTER_NAME}$"; then
  echo "Cluster '${CLUSTER_NAME}' already exists — skipping creation."
else
  kind create cluster --name "$CLUSTER_NAME" --config kind-config.yaml
  kubectl label node "${CLUSTER_NAME}-control-plane" ingress-ready=true
fi

kubectl config use-context "kind-${CLUSTER_NAME}"

# --- Pre-load infra images into kind node ---
echo "Pre-loading infra images..."
INFRA_IMAGES=(
  "postgres:16"
  "redis:7.4-alpine"
  "apache/kafka:3.9.0"
  "docker.elastic.co/elasticsearch/elasticsearch:8.15.0"
  "minio/minio:latest"
  "quay.io/keycloak/keycloak:26.0"
  "openzipkin/zipkin:3"
)
for img in "${INFRA_IMAGES[@]}"; do
  docker pull "$img" 2>/dev/null || true
  kind load docker-image "$img" --name "$CLUSTER_NAME" 2>/dev/null || true
done

# --- Pre-load service images into kind node ---
echo "Pre-loading service images..."
for svc in $SERVICES frontend; do
  img="${REGISTRY}/${svc}:latest"
  # Pull if not present locally
  docker image inspect "$img" &>/dev/null || docker pull "$img" 2>/dev/null || true
  kind load docker-image "$img" --name "$CLUSTER_NAME" 2>/dev/null || true
done

# --- Ingress NGINX ---
if ! kubectl get ns ingress-nginx &>/dev/null; then
  kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml
fi
kubectl wait --namespace ingress-nginx \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/component=controller \
  --timeout=300s

# --- Namespace ---
kubectl apply -f k8s/namespace.yaml

# --- Secrets ---
if ! kubectl get secret postgres-secret -n "$NAMESPACE" &>/dev/null; then
  if [ -f .env ]; then
    source .env
    kubectl create secret generic postgres-secret -n "$NAMESPACE" \
      --from-literal=POSTGRES_USER="${POSTGRES_USER}" \
      --from-literal=POSTGRES_PASSWORD="${POSTGRES_PASSWORD}"
    kubectl create secret generic keycloak-secret -n "$NAMESPACE" \
      --from-literal=KEYCLOAK_ADMIN="${KEYCLOAK_ADMIN}" \
      --from-literal=KEYCLOAK_ADMIN_PASSWORD="${KEYCLOAK_ADMIN_PASSWORD}"
    kubectl create secret generic redis-secret -n "$NAMESPACE" \
      --from-literal=REDIS_PASSWORD="${REDIS_PASSWORD}"
    kubectl create secret generic minio-secret -n "$NAMESPACE" \
      --from-literal=MINIO_ROOT_USER="${MINIO_ROOT_USER}" \
      --from-literal=MINIO_ROOT_PASSWORD="${MINIO_ROOT_PASSWORD}"
    kubectl create secret generic elasticsearch-secret -n "$NAMESPACE" \
      --from-literal=ELASTIC_PASSWORD="${REDIS_PASSWORD}"
    kubectl create secret generic mail-secret -n "$NAMESPACE" \
      --from-literal=MAIL_USERNAME="${MAIL_USERNAME}" \
      --from-literal=MAIL_PASSWORD="${MAIL_PASSWORD}"
  else
    kubectl apply -f k8s/secrets.yaml
  fi
fi

# --- Config ---
kubectl apply -f k8s/configmap.yaml
kubectl create configmap keycloak-realm -n "$NAMESPACE" \
  --from-file=ecommerce-realm.json=docker/keycloak/import/ecommerce-realm.json \
  --dry-run=client -o yaml | kubectl apply -f -
kubectl create configmap postgres-init-scripts -n "$NAMESPACE" \
  --from-file=create-all-databases.sql=docker/postgres/init/create-all-databases.sql \
  --dry-run=client -o yaml | kubectl apply -f -

# --- Infrastructure ---
kubectl apply -f k8s/infra/postgres.yaml
kubectl apply -f k8s/infra/redis.yaml
kubectl apply -f k8s/infra/kafka.yaml
kubectl apply -f k8s/infra/elasticsearch.yaml
kubectl apply -f k8s/infra/minio.yaml
kubectl apply -f k8s/infra/keycloak.yaml
kubectl apply -f k8s/infra/zipkin.yaml

echo "Waiting for infrastructure to be ready..."
kubectl wait pod -n "$NAMESPACE" -l app=postgres  --for=condition=Ready --timeout=600s
kubectl wait pod -n "$NAMESPACE" -l app=redis     --for=condition=Ready --timeout=300s
kubectl wait pod -n "$NAMESPACE" -l app=kafka     --for=condition=Ready --timeout=300s
kubectl wait pod -n "$NAMESPACE" -l app=keycloak  --for=condition=Ready --timeout=600s

# --- Backend services ---
echo "Deploying backend services..."
kubectl apply -f k8s/backend/discovery-service.yaml
kubectl wait pod -n "$NAMESPACE" -l app=discovery-service --for=condition=Ready --timeout=600s

for yaml in k8s/backend/*.yaml; do
  [[ "$yaml" == *"discovery-service"* ]] && continue
  kubectl apply -f "$yaml"
done

# --- Frontend & Ingress ---
kubectl apply -f k8s/frontend/frontend.yaml
kubectl apply -f k8s/ingress/ingress.yaml

echo "K8S Deploy complete"
