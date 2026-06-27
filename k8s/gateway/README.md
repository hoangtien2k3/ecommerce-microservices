# API Gateway — Apache APISIX (Kubernetes-native)

Routing is **declarative and auto-synced**. The APISIX Ingress Controller watches the
CRDs in this folder and pushes them into APISIX. No gateway app, no per-service config.

```
Client ──▶ apisix-gateway (LoadBalancer)         ← APISIX is the single API edge (no NGINX hop)
                 │  APISIX Ingress Controller
                 ▼  reconciles CRDs → APISIX (etcd)
       ApisixRoute / ApisixPluginConfig / ApisixGlobalRule
                 ▼
       backend Services (ecommerce ns)
```

## Files
| File | Kind | Purpose |
|------|------|---------|
| `00-plugin-config.yaml` | `ApisixPluginConfig` | Reusable Keycloak JWT (`openid-connect`) named `jwt-auth` |
| `01-global-rule.yaml`   | `ApisixGlobalRule`   | CORS + Prometheus + rate-limit for every route |
| `02-routes.yaml`        | `ApisixRoute` × 13   | One per service; `plugin_config_name: jwt-auth` = secured |

## Install order
1. **Platform** (gateway + etcd + controller + CRDs) — via Helm/ArgoCD:
   ```bash
   kubectl apply -f k8s/argocd/apisix.yaml          # ArgoCD (recommended), or:
   helm repo add apisix https://apache.github.io/apisix-helm-chart && helm repo update
   helm install apisix apisix/apisix -n ecommerce --create-namespace \
     --set ingress-controller.enabled=true --set etcd.enabled=true --set service.type=ClusterIP
   ```
2. **Routes** — applied by the main `ecommerce-dev` ArgoCD app (recurses `k8s/`), or:
   ```bash
   kubectl apply -f k8s/gateway/
   ```
   (CRDs from step 1 must exist first; ArgoCD retries until they do.)

Each backend runs under a servlet `context-path` (`/auth`, `/product`, ...), so all of a
service's URLs are namespaced by that prefix and APISIX forwards them straight through.

## Adding / changing APIs — what each case needs
- **New endpoint under an existing prefix** (e.g. `/order/api/orders/refunds`): nothing —
  the `/order/api/orders/*` wildcard already covers it.
- **New path prefix**: add one `match.paths` entry to that service's `ApisixRoute`.
- **New service**: set its `server.servlet.context-path` + add one `ApisixRoute` block.

## Notes
- `client_secret` in `00-plugin-config.yaml` is a placeholder — replace it (Keycloak client
  `ecommerce-client`). Backends still re-validate the JWT (`common-security`) = defense in depth.
- Verify the Helm chart version/values in `k8s/argocd/apisix.yaml` against `helm show values apisix/apisix`.
