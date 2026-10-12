# =============================================================================
# ecommerce-microservices — developer workflow
#
#   make help          Show every target
#   make build         Build all Go modules
#   make test          Run the Go + Bun test suites
#   make up            Start the full stack with Docker Compose
#
# Windows without GNU make: scripts/dev.ps1 mirrors every target 1:1
#   pwsh scripts/dev.ps1 test
#   pwsh scripts/dev.ps1 go-run product-service
# =============================================================================

GO_MODULES     = services/shared services/auth-service services/product-service \
                 services/order-service services/payment-service services/inventory-service \
                 services/shipping-service services/search-service
GO_PATTERNS    = $(addsuffix /...,$(addprefix ./,$(GO_MODULES)))
BUN_SERVICES   = promotion-service rating-service media-service notification-service
INFRA_SERVICES = postgres redis kafka opensearch rustfs keycloak apisix

.DEFAULT_GOAL := help

# =============================================================================
# Help
# =============================================================================

.PHONY: help
help: ## Show this help
	@echo "ecommerce-microservices — available targets"
	@echo ""
	@grep -hE '^[a-zA-Z0-9_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  %-22s %s\n", $$1, $$2}'

# =============================================================================
# Go workspace (8 modules, see go.work)
# =============================================================================

.PHONY: build go-build go-test go-vet go-fmt go-fmt-check go-tidy go-run

build: go-build ## Build every Go module

go-build: ## Build all Go modules
	go build $(GO_PATTERNS)

go-test: ## Run go test for all Go modules
	go test $(GO_PATTERNS)

go-vet: ## Run go vet for all Go modules
	go vet $(GO_PATTERNS)

go-fmt: ## Format all Go code (gofmt -w)
	gofmt -w $(GO_MODULES)

go-fmt-check: ## Fail if any Go file is not gofmt-formatted
	@unformatted="$$(gofmt -l $(GO_MODULES))"; \
	if [ -n "$$unformatted" ]; then \
		echo "Unformatted Go files:"; echo "$$unformatted"; exit 1; \
	fi

go-tidy: ## go mod tidy in every module
	@for m in $(GO_MODULES); do \
		echo "==> go mod tidy: $$m"; \
		(cd $$m && go mod tidy) || exit 1; \
	done

go-run: ## Run one Go service locally (SVC=product-service)
	@test -n "$(SVC)" || { echo "Usage: make go-run SVC=<module>"; exit 1; }
	cd services/$(SVC) && go run .

# =============================================================================
# Bun services (4 services)
# =============================================================================

.PHONY: bun-install bun-test bun-dev

bun-install: ## Install dependencies for the 4 Bun services
	@for s in $(BUN_SERVICES); do \
		echo "==> bun install: $$s"; \
		(cd services/$$s && bun install) || exit 1; \
	done

bun-test: bun-install ## Run tests for the 4 Bun services
	@fail=0; for s in $(BUN_SERVICES); do \
		echo "==> bun test: $$s"; \
		(cd services/$$s && bun test) || fail=1; \
	done; exit $$fail

bun-dev: ## Run one Bun service in watch mode (SVC=rating-service)
	@test -n "$(SVC)" || { echo "Usage: make bun-dev SVC=<service>"; exit 1; }
	cd services/$(SVC) && bun install && bun run dev

# =============================================================================
# Frontend (Bun workspace — SvelteKit 5)
# =============================================================================

.PHONY: web-install web-dev web-build web-check web-lint web-test

web-install: ## Install frontend workspace dependencies (Bun)
	cd frontend && bun install

web-dev: ## Start both SvelteKit apps (web :3000, admin :3001)
	cd frontend && bun run dev

web-build: ## Production build of both SvelteKit apps
	cd frontend && bun run build

web-check: ## Type-check both apps and packages (svelte-check + tsc)
	cd frontend && bun run check

web-lint: ## Prettier + ESLint across the frontend workspace
	cd frontend && bun run lint

web-test: ## Run frontend unit tests (bun test)
	cd frontend && bun test

# =============================================================================
# Testing
# =============================================================================

.PHONY: test check

test: go-test bun-test ## Run the Go and Bun test suites

check: go-fmt-check go-vet go-test bun-test ## gofmt check + go vet + all tests

# =============================================================================
# Docker Compose
# =============================================================================

.PHONY: up infra-up down ps logs restart images

up: ## Build & start the full stack (infra + 11 services + frontend)
	docker compose up -d --build

infra-up: ## Start only the infra containers (postgres, redis, kafka, opensearch, rustfs, keycloak, apisix)
	docker compose up -d $(INFRA_SERVICES)

down: ## Stop and remove containers
	docker compose down

ps: ## Show container status
	docker compose ps

logs: ## Follow logs (all services, or SVC=product-service)
	docker compose logs -f $(SVC)

restart: ## Restart one container (SVC=product-service)
	@test -n "$(SVC)" || { echo "Usage: make restart SVC=<service>"; exit 1; }
	docker compose restart $(SVC)

images: ## Build all Docker images without starting them
	docker compose build

# =============================================================================
# Kubernetes (k3d — legacy path, superseded by the ecommerce-gitops repo)
# =============================================================================

.PHONY: cluster-up cluster-status cluster-down

cluster-up: ## Create/refresh the k3d cluster and deploy the manifests (Docker or Podman)
	bash scripts/k3d-setup.sh

cluster-status: ## Show pods in the ecommerce namespace
	kubectl get pods -n ecommerce -o wide

cluster-down: ## Delete the k3d cluster
	k3d cluster delete ecommerce
