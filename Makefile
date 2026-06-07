REGISTRY  ?= ghcr.io/hoangtien2k3/ecommerce-microservices
TAG       ?= latest
NAMESPACE ?= ecommerce
SHA       := $(shell git rev-parse --short HEAD 2>/dev/null || echo "local")

SERVICES = discovery-service api-gateway auth-service \
           product-service order-service payment-service \
           shipping-service inventory-service favourite-service \
           rating-service media-service tax-service \
           promotion-service search-service notification-service

# ============================================================
# DOCKER COMPOSE  (local dev / CI)
# ============================================================

.PHONY: up down logs ps restart build-all

## Start infrastructure only (postgres, kafka, redis, keycloak…)
infra-up:
	docker compose up -d postgres mongodb redis kafka elasticsearch minio keycloak zipkin

## Wait until infra is healthy, then start all backend + frontend
up:
	docker compose up -d

## Build all images without cache
build-all:
	docker compose build --no-cache --parallel

## Build a single service: make build SVC=product-service
build:
	docker compose build $(SVC)

down:
	docker compose down

## Remove containers, networks, and volumes (destructive!)
clean:
	docker compose down -v --remove-orphans

logs:
	docker compose logs -f $(SVC)

ps:
	docker compose ps

restart:
	docker compose restart $(SVC)

# ============================================================
# KIND (local Kubernetes)
# ============================================================

.PHONY: kind-setup kind-delete kind-load kind-status kind-logs

## Tạo kind cluster + deploy toàn bộ (one-shot)
kind-setup:
	./kind-setup.sh

## Xóa kind cluster
kind-delete:
	kind delete cluster --name ecommerce

## Load lại images vào kind sau khi build mới
kind-load:
	@for svc in $(SERVICES); do \
		docker tag ecommerce/$$svc:latest $(REGISTRY)/$$svc:latest 2>/dev/null || true; \
		echo "==> Loading $$svc"; \
		kind load docker-image $(REGISTRY)/$$svc:latest --name ecommerce; \
	done
	docker tag ecommerce/frontend:latest $(REGISTRY)/frontend:latest 2>/dev/null || true
	kind load docker-image $(REGISTRY)/frontend:latest --name ecommerce

## Xem trạng thái pods trong kind cluster
kind-status:
	kubectl get pods -n $(NAMESPACE) -o wide

## Xem logs của một service: make kind-logs SVC=auth-service
kind-logs:
	kubectl logs -n $(NAMESPACE) -l app=$(SVC) --tail=100 -f

## Restart một service: make kind-restart SVC=auth-service
kind-restart:
	kubectl rollout restart deployment/$(SVC) -n $(NAMESPACE)

# ============================================================
# KUBERNETES
# ============================================================

.PHONY: k8s-build k8s-deploy k8s-infra k8s-backend k8s-frontend k8s-all k8s-delete

## Build & tag all images for K8S (set REGISTRY to your registry, e.g. docker.io/yourname)
k8s-build:
	@for svc in $(SERVICES); do \
		echo "==> Building $$svc"; \
		docker build -t $(REGISTRY)/$$svc:$(TAG) ./$$svc; \
	done
	@echo "==> Building frontend"
	docker build -t $(REGISTRY)/frontend:$(TAG) ./frontend

## Push all images to registry
k8s-push:
	@for svc in $(SERVICES); do \
		echo "==> Pushing $$svc"; \
		docker push $(REGISTRY)/$$svc:$(TAG); \
	done
	docker push $(REGISTRY)/frontend:$(TAG)

## Apply namespace + secrets + configmap
k8s-config:
	kubectl apply -f k8s/namespace.yaml
	kubectl apply -f k8s/secrets.yaml
	kubectl apply -f k8s/configmap.yaml

## Deploy infrastructure (databases, kafka, etc.)
k8s-infra: k8s-config
	kubectl apply -f k8s/infra/postgres.yaml
	kubectl apply -f k8s/infra/mongodb.yaml
	kubectl apply -f k8s/infra/redis.yaml
	kubectl apply -f k8s/infra/kafka.yaml
	kubectl apply -f k8s/infra/elasticsearch.yaml
	kubectl apply -f k8s/infra/minio.yaml
	kubectl apply -f k8s/infra/keycloak.yaml
	kubectl apply -f k8s/infra/zipkin.yaml
	@echo "Waiting 60s for infra to stabilise…"
	sleep 60

## Deploy backend microservices
k8s-backend:
	kubectl apply -f k8s/backend/discovery-service.yaml
	@echo "Waiting for discovery-service to be Ready…"
	kubectl wait pod -n $(NAMESPACE) -l app=discovery-service --for=condition=Ready --timeout=240s
	kubectl apply -f k8s/backend/api-gateway.yaml
	kubectl apply -f k8s/backend/auth-service.yaml
	kubectl apply -f k8s/backend/product-service.yaml
	kubectl apply -f k8s/backend/order-service.yaml
	kubectl apply -f k8s/backend/payment-service.yaml
	kubectl apply -f k8s/backend/shipping-service.yaml
	kubectl apply -f k8s/backend/inventory-service.yaml
	kubectl apply -f k8s/backend/favourite-service.yaml
	kubectl apply -f k8s/backend/rating-service.yaml
	kubectl apply -f k8s/backend/media-service.yaml
	kubectl apply -f k8s/backend/tax-service.yaml
	kubectl apply -f k8s/backend/promotion-service.yaml
	kubectl apply -f k8s/backend/search-service.yaml
	kubectl apply -f k8s/backend/notification-service.yaml

## Deploy frontend
k8s-frontend:
	kubectl apply -f k8s/frontend/frontend.yaml

## Deploy ingress
k8s-ingress:
	kubectl apply -f k8s/ingress/ingress.yaml

## Full K8S deploy: infra → backend → frontend → ingress
k8s-all: k8s-infra k8s-backend k8s-frontend k8s-ingress
	@echo ""
	@echo "==> All services deployed to namespace '$(NAMESPACE)'"
	@echo "==> Add these entries to /etc/hosts:"
	@echo "    127.0.0.1  ecommerce.local api.ecommerce.local auth.ecommerce.local"
	@echo "    127.0.0.1  discovery.ecommerce.local zipkin.ecommerce.local minio.ecommerce.local"

## Delete all K8S resources (keeps PVCs to preserve data)
k8s-delete:
	kubectl delete -f k8s/ingress/ --ignore-not-found
	kubectl delete -f k8s/frontend/ --ignore-not-found
	kubectl delete -f k8s/backend/ --ignore-not-found
	kubectl delete -f k8s/infra/ --ignore-not-found
	kubectl delete -f k8s/configmap.yaml --ignore-not-found
	kubectl delete -f k8s/secrets.yaml --ignore-not-found

## Delete namespace and EVERYTHING (destructive!)
k8s-nuke:
	kubectl delete namespace $(NAMESPACE) --ignore-not-found

## Login to GHCR (requires GITHUB_TOKEN env var or gh auth token)
ghcr-login:
	echo "$(GITHUB_TOKEN)" | docker login ghcr.io -u hoangtien2k3 --password-stdin

## Push images with git SHA tag (used by CI/CD)
k8s-push-sha:
	@for svc in $(SERVICES); do \
		docker tag $(REGISTRY)/$$svc:latest $(REGISTRY)/$$svc:$(SHA); \
		docker push $(REGISTRY)/$$svc:$(SHA); \
		docker push $(REGISTRY)/$$svc:latest; \
	done

# ============================================================
# ARGOCD
# ============================================================

.PHONY: argocd-install argocd-apply argocd-ui argocd-password

## Install ArgoCD into cluster
argocd-install:
	kubectl create namespace argocd --dry-run=client -o yaml | kubectl apply -f -
	kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
	kubectl wait pod -n argocd -l app.kubernetes.io/name=argocd-server --for=condition=Ready --timeout=300s
	@echo "ArgoCD installed. Run: make argocd-password && make argocd-ui"

## Get ArgoCD admin password
argocd-password:
	@kubectl -n argocd get secret argocd-initial-admin-secret \
		-o jsonpath='{.data.password}' | base64 -d && echo

## Open ArgoCD UI (port-forward)
argocd-ui:
	kubectl port-forward svc/argocd-server -n argocd 8080:443

## Apply ArgoCD Application manifests
argocd-apply:
	kubectl apply -f k8s/argocd/application.yaml

## Generate ArgoCD API token (requires argocd CLI + port-forward active)
argocd-token:
	@echo "Generating ArgoCD token for GitHub Actions..."
	argocd login argocd-server.argocd.svc.cluster.local:80 \
		--auth-token $$(kubectl -n argocd get secret argocd-initial-admin-secret \
		  -o jsonpath='{.data.password}' | base64 -d) \
		--insecure --plaintext 2>/dev/null || \
	argocd login localhost:8080 \
		--username admin \
		--password $$(kubectl -n argocd get secret argocd-initial-admin-secret \
		  -o jsonpath='{.data.password}' | base64 -d) \
		--insecure
	@echo ""
	@echo "==> ARGOCD_TOKEN:"
	argocd account generate-token --account admin
	@echo ""
	@echo "==> ARGOCD_SERVER (for self-hosted runner):"
	@echo "    argocd-server.argocd.svc.cluster.local:80"

# ============================================================
# GITHUB ACTIONS SELF-HOSTED RUNNER (ARC)
# ============================================================

.PHONY: arc-install arc-runner arc-status

## Install Actions Runner Controller (ARC) — requires helm
arc-install:
	kubectl apply -f https://github.com/cert-manager/cert-manager/releases/latest/download/cert-manager.yaml
	kubectl wait pod -n cert-manager -l app=cert-manager --for=condition=Ready --timeout=120s
	helm install arc \
		--namespace arc-systems --create-namespace \
		oci://ghcr.io/actions/actions-runner-controller-charts/gha-runner-scale-set-controller
	@echo "ARC controller installed. Now run: make arc-runner GITHUB_PAT=<your_pat>"

## Deploy runner scale set — usage: make arc-runner GITHUB_PAT=ghp_xxx
arc-runner:
	@[ -n "$(GITHUB_PAT)" ] || (echo "Usage: make arc-runner GITHUB_PAT=ghp_xxx" && exit 1)
	helm install arc-runner-set \
		--namespace arc-runners --create-namespace \
		--set githubConfigUrl="https://github.com/hoangtien2k3/ecommerce-microservices" \
		--set githubConfigSecret.github_token="$(GITHUB_PAT)" \
		oci://ghcr.io/actions/actions-runner-controller-charts/gha-runner-scale-set
	@echo "Runner deployed! Check: kubectl get pods -n arc-runners"

## Check runner status
arc-status:
	@echo "=== ARC Controller ==="
	kubectl get pods -n arc-systems
	@echo ""
	@echo "=== Runners ==="
	kubectl get pods -n arc-runners

## Watch pod status
k8s-status:
	kubectl get pods -n $(NAMESPACE) -w

## Show all K8S resources
k8s-ps:
	kubectl get all -n $(NAMESPACE)

## Get logs for a K8S pod: make k8s-logs SVC=product-service
k8s-logs:
	kubectl logs -n $(NAMESPACE) -l app=$(SVC) --tail=100 -f

# ============================================================
# MAVEN BUILD (build JARs before Docker)
# ============================================================

.PHONY: mvn-build mvn-clean

mvn-build:
	./mvnw clean package -DskipTests -T 4

mvn-clean:
	./mvnw clean
