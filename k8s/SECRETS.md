# Secrets management

> ⚠️ `k8s/secrets.yaml` and the inline `client_secret` / `redis_password` in
> `k8s/gateway/*.yaml` currently hold **plaintext** values committed to git. That is fine
> for local/dev only. **Do not ship plaintext secrets to a real cluster.** Migrate to one
> of the options below and remove the real values from git history.

## Secrets in this repo
| Where | Keys |
|-------|------|
| `k8s/secrets.yaml` | postgres / keycloak / redis / storage / elasticsearch / mail credentials |
| `k8s/gateway/00-plugin-config.yaml` | Keycloak `client_secret` (openid-connect) |
| `k8s/gateway/01-global-rule.yaml` | `redis_password` (rate-limit store) |

## Option A — Sealed Secrets (simplest GitOps fit)
Encrypt secrets so the *ciphertext* is safe to commit; only the in-cluster controller can decrypt.

```bash
# 1) install controller (once per cluster)
helm repo add sealed-secrets https://bitnami-labs.github.io/sealed-secrets
helm install sealed-secrets sealed-secrets/sealed-secrets -n kube-system

# 2) seal a plain Secret manifest → commit the SealedSecret, delete the plain one
kubeseal --format=yaml < k8s/secrets.yaml > k8s/sealed-secrets.yaml
kubectl apply -f k8s/sealed-secrets.yaml      # controller decrypts into real Secrets
```
Then delete `k8s/secrets.yaml` from git and `git filter-repo` to purge history.

## Option B — External Secrets Operator (secret manager backend)
Keep secrets in Vault / AWS Secrets Manager / GCP SM; sync into k8s at runtime.

```yaml
apiVersion: external-secrets.io/v1beta1
kind: ExternalSecret
metadata: { name: apisix-secret, namespace: ecommerce }
spec:
  secretStoreRef: { name: vault-store, kind: ClusterSecretStore }
  target: { name: apisix-secret }
  data:
    - secretKey: KEYCLOAK_CLIENT_SECRET
      remoteRef: { key: ecommerce/keycloak, property: client_secret }
```

## Getting secrets OUT of the gateway CRDs
The `client_secret` / `redis_password` baked into the APISIX CRDs should come from a Secret
instead of literals. Use the APISIX Ingress Controller secret reference (`secretRef`) or APISIX
secret backends (`$secret://`) so the CRD references a key rather than embedding the value.

## Minimum hygiene now
- Rotate any real credential that was ever committed (the Gmail app password, Keycloak/DB passwords).
- Add a non-committed override (`k8s/secrets.local.yaml`) to `.gitignore` for local runs.
