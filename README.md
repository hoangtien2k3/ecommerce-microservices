# E-Commerce Cloud-Native Microservices Platform

<p align="center">
  <picture>
    <source media="(prefers-color-scheme: light)" srcset="https://socialify.git.ci/Ecommerce-INT/ecommerce-microservices/image?description=1&descriptionEditable=%E2%9A%A1%EF%B8%8F%2011%20Microservices%20%E2%80%A2%20Golang%201.27%20%E2%80%A2%20Bun%20%E2%80%A2%20Kubernetes&font=Inter&forks=1&language=1&owner=1&pattern=Floating%20Cogs&pulls=1&stargazers=1&theme=Light"/>
    <source media="(prefers-color-scheme: dark)" srcset="https://socialify.git.ci/Ecommerce-INT/ecommerce-microservices/image?description=1&descriptionEditable=%E2%9A%A1%EF%B8%8F%2011%20Microservices%20%E2%80%A2%20Golang%201.27%20%E2%80%A2%20Bun%20%E2%80%A2%20Kubernetes&font=Inter&forks=1&language=1&owner=1&pattern=Floating%20Cogs&pulls=1&stargazers=1&theme=Dark"/>
    <img alt="ecommerce-microservices" src="https://socialify.git.ci/Ecommerce-INT/ecommerce-microservices/image?description=1&descriptionEditable=%E2%9A%A1%EF%B8%8F%2011%20Microservices%20%E2%80%A2%20Golang%201.27%20%E2%80%A2%20Bun%20%E2%80%A2%20Kubernetes&font=Inter&forks=1&language=1&owner=1&pattern=Floating%20Cogs&pulls=1&stargazers=1&theme=Auto"/>
  </picture>
</p>

<p align="center">
  <a href="LICENSE">
    <img src="https://img.shields.io/badge/license-MIT-blue.svg" alt="License">
  </a>
  <img src="https://img.shields.io/badge/Go-1.27-00ADD8?logo=go&logoColor=white" alt="Go 1.27">
  <img src="https://img.shields.io/badge/Router-chi%2Fv5-00599C" alt="chi/v5">
  <img src="https://img.shields.io/badge/Data-bob%20%2B%20pgx%2Fv5-336791" alt="bob + pgx/v5">
  <img src="https://img.shields.io/badge/Bun-1.4+-f472b6?logo=bun&logoColor=white" alt="Bun 1.4+">
  <img src="https://img.shields.io/badge/ORM-Drizzle%20v1-C5F74F?logo=drizzle&logoColor=black" alt="Drizzle v1">
  <img src="https://img.shields.io/badge/SvelteKit-3-FF3E00?logo=svelte&logoColor=white" alt="SvelteKit 3">
  <img src="https://img.shields.io/badge/Architecture-11_Microservices-brightgreen" alt="11 Microservices">
</p>

---

## Overview & Stack

A cloud-native e-commerce platform built around 11 backend microservices and 2 SvelteKit frontend applications (BFFs).

- **Backend (Go)**: 7 services using Go 1.27, `chi/v5` (routing), and `bob/pgx` (PostgreSQL).
- **Backend (Bun)**: 4 services using Bun 1.4+, Hono, and Drizzle ORM v1 (`Bun.sql` / `Bun.S3Client`).
- **Frontend**: 2 applications using SvelteKit 3, Svelte 5 (runes), and Tailwind CSS 4.
- **Infrastructure**: Apache APISIX (API Gateway), Keycloak (OIDC), PostgreSQL 16, Redis 7.4, Kafka 3.9, OpenSearch 3.9, and RustFS (S3-compatible).

### Services and Applications

| Service / App | Path | Stack | Role |
| :--- | :--- | :--- | :--- |
| **Auth** | `services/auth-service/` | Go | OIDC integration, SSO ticket exchange, user/role management |
| **Product** | `services/product-service/` | Go | Catalog, categories, brands, user wishlists |
| **Order** | `services/order-service/` | Go | Cart, order creation, checkout lifecycle |
| **Payment** | `services/payment-service/` | Go | Transactions, payment status webhooks, Kafka publisher |
| **Inventory** | `services/inventory-service/` | Go | Stock queries, reservations, warehouse ledger |
| **Shipping** | `services/shipping-service/` | Go | Fee calculation, carrier management, shipment tracking |
| **Search** | `services/search-service/` | Go | OpenSearch full-text search, autocomplete, Kafka sync |
| **Promotion** | `services/promotion-service/` | Bun / TS | Coupons, discount rules, tax rate calculation |
| **Rating** | `services/rating-service/` | Bun / TS | Product reviews, verified purchase checks, star aggregates |
| **Media** | `services/media-service/` | Bun / TS | S3 multipart uploads, streaming, presigned URLs |
| **Notification** | `services/notification-service/` | Bun / TS | Transactional emails, in-app inbox, Kafka consumer |
| **Storefront** | `frontend/apps/web/` | SvelteKit | Customer-facing web app (catalog, cart, checkout) |
| **Backoffice** | `frontend/apps/admin/` | SvelteKit | Internal admin dashboard (inventory, order management) |

## Getting Started

### Prerequisites

- [Docker](https://docs.docker.com/get-docker/) + Compose v2
- [Go](https://go.dev/dl/) 1.27+ and [Bun](https://bun.sh/) 1.4+ (for local development)
- GNU `make` or PowerShell 5.1+

### Local Environment (Docker Compose)

Run the entire platform locally, including all services and infrastructure:

```bash
docker compose up -d --build
```

- Storefront: `http://localhost:3000`
- Backoffice: `http://localhost:3001`
- API Gateway (APISIX): `http://localhost:9080`
- Keycloak Admin: `http://localhost:8080` (admin/admin)

### Kubernetes (k3d)

To spin up a local Kubernetes cluster using `k3d`, run:

```bash
# Windows
.\scripts\start-ecommerce.bat

# Linux / macOS
./scripts/start-ecommerce.sh
```

Update your `hosts` file to resolve the local domain:
`127.0.0.1 ecommerce.local admin.ecommerce.local api.ecommerce.local keycloak.ecommerce.local rustfs.ecommerce.local`

### Developer Workflow

A `Makefile` (and matching `scripts/dev.ps1` for Windows) provides centralized task management:

```bash
# Start only the infrastructure (DBs, Cache, Brokers, Gateway)
make infra-up

# Run an individual backend service locally
make go-run SVC=product-service
make bun-dev SVC=rating-service

# Run the frontend applications
make web-install
make web-dev

# Run tests and formatting
make check      # Go formatting and tests
make web-check  # Frontend linting and type checks
```

*On Windows, replace `make <target>` with `.\scripts\dev.ps1 <target>`.*

## Documentation

Comprehensive project documentation is available in the [`docs/`](docs/) directory:

- [Frontend Documentation](docs/web/README.md) - Deep dive into SvelteKit setup, architecture, and shared packages.
- [Architecture & Roadmap](docs/architecture-revamp-plan.md) - Details on internal API communication, gateway configuration, and upcoming architectural changes (e.g., migrating to NATS and Better-Auth).
- [Environment Configuration](.env.example) - Reference for service environment variables (PostgreSQL, Kafka, S3, Keycloak, etc.).

## License

[MIT](LICENSE)
