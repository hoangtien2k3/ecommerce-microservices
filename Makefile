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
