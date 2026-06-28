<p align="center">
  <picture>
    <source media="(prefers-color-scheme: light)" srcset="https://socialify.git.ci/hoangtien2k3/ecommerce-microservices/image?description=1&descriptionEditable=%E2%9A%A1%EF%B8%8F%2013%20Microservices%20%E2%80%A2%20Spring%20Boot%203%20%E2%80%A2%20Kubernetes&font=Inter&forks=1&language=1&logo=https%3A%2F%2Fi.ibb.co%2FN366vtQ%2Fhoangtien2k3.png&owner=1&pattern=Floating%20Cogs&pulls=1&stargazers=1&theme=Light"/>
    <source media="(prefers-color-scheme: dark)" srcset="https://socialify.git.ci/hoangtien2k3/ecommerce-microservices/image?description=1&descriptionEditable=%E2%9A%A1%EF%B8%8F%2013%20Microservices%20%E2%80%A2%20Spring%20Boot%203%20%E2%80%A2%20Kubernetes&font=Inter&forks=1&language=1&logo=https%3A%2F%2Fi.ibb.co%2FN366vtQ%2Fhoangtien2k3.png&owner=1&pattern=Floating%20Cogs&pulls=1&stargazers=1&theme=Dark"/>
    <img alt="ecommerce-microservices" src="https://socialify.git.ci/hoangtien2k3/ecommerce-microservices/image?description=1&descriptionEditable=%E2%9A%A1%EF%B8%8F%2013%20Microservices%20%E2%80%A2%20Spring%20Boot%203%20%E2%80%A2%20Kubernetes&font=Inter&forks=1&language=1&logo=https%3A%2F%2Fi.ibb.co%2FN366vtQ%2Fhoangtien2k3.png&owner=1&pattern=Floating%20Cogs&pulls=1&stargazers=1&theme=Auto"/>
  </picture>
</p>

<p align="center">
  <a href="https://sonarcloud.io/project/configuration?id=hoangtien2k3_ecommerce-microservices">
    <img src="https://sonarcloud.io/api/project_badges/measure?project=hoangtien2k3_ecommerce-microservices&metric=alert_status" alt="Quality Gate">
  </a>
  <a href="https://sonarcloud.io/project/configuration?id=hoangtien2k3_ecommerce-microservices">
    <img src="https://sonarcloud.io/api/project_badges/measure?project=hoangtien2k3_ecommerce-microservices&metric=sqale_index" alt="Maintainability">
  </a>
  <a href="LICENSE">
    <img src="https://img.shields.io/badge/license-MIT-blue.svg" alt="License">
  </a>
  <a href="https://github.com/hoangtien2k3/ecommerce-microservices/releases">
    <img src="https://img.shields.io/github/v/release/hoangtien2k3/ecommerce-microservices" alt="Release">
  </a>
  <a href="https://github.com/hoangtien2k3/ecommerce-microservices/stargazers">
    <img src="https://img.shields.io/github/stars/hoangtien2k3/ecommerce-microservices?style=social" alt="Stars">
  </a>
</p>

---

## 📋 Overview

**ecommerce-microservices** is a production-grade, cloud-native e-commerce platform built with a microservice
architecture. It consists of **13 Spring Boot 3** backend services, an **Apache APISIX** API Gateway, a **Next.js 16**
frontend, and a full suite of infrastructure — all deployable on **Kubernetes (k3d)** with a single command.

|                   |                                                    |
|-------------------|----------------------------------------------------|
| ⚡ **Backend**     | 13 microservices · Java 21 · Spring Boot 3.3.5     |
| 🚪 **Gateway**    | Apache APISIX 3.9 · Rate limiting · JWT validation |
| 🗄️ **Databases** | PostgreSQL 16 · Redis 7 · Elasticsearch 8          |
| 📨 **Messaging**  | Apache Kafka 3.9 (KRaft mode, no Zookeeper)        |
| 🔐 **Auth**       | Keycloak 26 · OAuth2 / OIDC · JWT                  |
| ☁️ **Storage**    | RustFS (S3-compatible)                             |
| 🐳 **Deploy**     | Docker Compose · k3d / Kubernetes · ArgoCD         |

---

## 🛠️ Technology Stack

### Backend

|                   |                                                                  |
|-------------------|------------------------------------------------------------------|
| **Runtime**       | Java 21 (Virtual Threads)                                        |
| **Framework**     | Spring Boot 3.3.5, Spring Security 6, Spring Data JPA            |
| **Database**      | PostgreSQL 16, Liquibase migrations                              |
| **Search**        | Elasticsearch 8, Spring Data Elasticsearch                       |
| **Messaging**     | Apache Kafka 3.9 (KRaft), Spring Kafka                           |
| **Security**      | Keycloak 26, OAuth2 / OIDC, JWT, Spring Security Resource Server |
| **Gateway**       | Apache APISIX 3.9 (OpenID Connect, rate limiting, CORS)          |
| **Storage**       | RustFS (S3-compatible), AWS SDK v2                               |
| **Observability** | Micrometer, Prometheus, Spring Boot Actuator                     |
| **API Docs**      | Springdoc OpenAPI 3, Swagger UI w/ PKCE                          |
| **Resilience**    | Resilience4j (circuit breaker, retry)                            |
| **Build**         | Maven, Jib (Dockerless containerization)                         |

### Frontend

|               |                              |
|---------------|------------------------------|
| **Framework** | Next.js 16.2, React 19.2     |
| **State**     | Zustand 5, TanStack Query 5  |
| **Styling**   | Tailwind CSS 4, Lucide icons |
| **HTTP**      | Axios                        |

### Infrastructure

|                |                                               |
|----------------|-----------------------------------------------|
| **Local K8s**  | k3d (K3s in Docker), NGINX Ingress Controller |
| **Production** | Kubernetes, ArgoCD (GitOps)                   |
| **Registry**   | GitHub Container Registry (ghcr.io)           |
| **CI**         | GitHub Actions, SonarCloud                    |

---

## 🚀 Quick Start

### Prerequisites

- [Docker Desktop](https://www.docker.com/get-started) (macOS / Windows) or Docker Engine (Linux)
- 8 GB+ RAM allocated to Docker

### One-Command Setup (k3d)

```bash
git clone https://github.com/hoangtien2k3/ecommerce-microservices.git
cd ecommerce-microservices
bash start-ecommerce.sh
```

The script automatically:

1. Installs k3d and kubectl (if missing)
2. Creates a local Kubernetes cluster
3. Deploys NGINX Ingress Controller
4. Applies secrets, config maps, and infrastructure (PostgreSQL, Redis, Kafka, Elasticsearch, RustFS, Keycloak)
5. Deploys APISIX gateway + all 13 backend services + frontend
6. Updates `/etc/hosts`

Wait ~5 minutes for all pods to become ready:

```bash
kubectl get pods -n ecommerce -w
```

### URLs

| Service               | URL                                                                                           |
|-----------------------|-----------------------------------------------------------------------------------------------|
| 🏠 **Frontend**       | [http://ecommerce.local](http://ecommerce.local)                                              |
| 🚪 **API Gateway**    | [http://api.ecommerce.local](http://api.ecommerce.local)                                      |
| 🔐 **Keycloak Admin** | [http://keycloak.ecommerce.local](http://keycloak.ecommerce.local/admin/master/console)       |
| 📦 **RustFS Console** | [http://rustfs.ecommerce.local/rustfs/console](http://rustfs.ecommerce.local/rustfs/console/) |

---

## 📁 Project Structure

```
ecommerce-microservices/
├── auth-service/                   # Port 8088
├── common-lib/                     # Shared library
│   ├── common-core/                #   Contracts, exceptions, i18n
│   ├── common-spring/              #   Auto-configurations
│   ├── common-security/            #   JWT / OAuth2 security
│   ├── common-keycloak/            #   Keycloak admin client
│   ├── common-kafka/               #   Kafka / CDC helpers
│   ├── common-logging/             #   AOP performance logging
│   └── common-storage/             #   S3 object storage abstraction
├── deploy/
│   └── apisix/                     # APISIX standalone config
├── docker/
│   ├── keycloak/import/            # Keycloak realm configuration
│   └── postgres/init/              # Database creation scripts
├── docker-compose.yml              # Full-stack local orchestration
├── favourite-service/              # Port 8081
├── frontend/                       # Next.js 16 application
├── inventory-service/              # Port 8082
├── k3d-config.yaml                 # k3d cluster definition
├── k3d-setup.sh                    # One-shot K8s deployment script
├── k8s/                            # Kubernetes manifests
│   ├── argocd/                     # ArgoCD GitOps configurations
│   ├── backend/                    # 13 service deployments
│   ├── frontend/                   # Frontend deployment
│   ├── gateway/                    # APISIX Ingress CRDs
│   ├── infra/                      # Infrastructure deployments
│   ├── ingress/                    # NGINX Ingress rules
│   ├── configmap.yaml
│   ├── namespace.yaml
│   └── secrets.yaml
├── Makefile                        # Build & deploy automation
├── media-service/                  # Port 8083
├── notification-service/           # Port 8090
├── order-service/                  # Port 8084
├── payment-service/                # Port 8085
├── pom.xml                         # Parent POM
├── product-service/                # Port 8086
├── promotion-service/              # Port 8093
├── rating-service/                 # Port 8089
├── search-service/                 # Port 8094
├── shipping-service/               # Port 8087
├── start-ecommerce.sh              # Entry point
└── tax-service/                    # Port 8091
```

---

## 📊 Observability

Every service exposes:

- **Health:** `/actuator/health` (liveness + readiness on port 9000)
- **Metrics:** `/actuator/prometheus` (Micrometer + Prometheus)
- **Distributed Tracing:** Correlation ID (`X-Correlation-Id`) propagated across all services
- **APISIX Metrics:** Prometheus metrics on port 9091

---

## 📈 Stats

<a href="https://star-history.com/#hoangtien2k3/ecommerce-microservices&Date">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="https://api.star-history.com/svg?repos=hoangtien2k3/ecommerce-microservices&type=Date&theme=dark" />
    <source media="(prefers-color-scheme: light)" srcset="https://api.star-history.com/svg?repos=hoangtien2k3/ecommerce-microservices&type=Date" />
    <img alt="Star History Chart" src="https://api.star-history.com/svg?repos=hoangtien2k3/ecommerce-microservices&type=Date" />
  </picture>
</a>

## Contributing

If you would like to contribute to the development of this project, please follow our contribution guidelines.

<a href="https://repobeats.axiom.co/api/embed/1897bc523b54b43aefb19c65195f32377f8aab85.svg">
  <img src="https://repobeats.axiom.co/api/embed/1897bc523b54b43aefb19c65195f32377f8aab85.svg" alt="Repo analytics" width="600">
</a>

---

## 📄 License

```
MIT License
Copyright (c) 2026 Hoàng Anh Tiến
```
