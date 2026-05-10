#!/usr/bin/env bash
set -euo pipefail

# Usage:
#   KEYCLOAK_URL=http://localhost:8089 \
#   REALM=ecommerce \
#   CLIENT_ID=ecommerce-client \
#   USERNAME=testuser \
#   PASSWORD=testpass \
#   GATEWAY_URL=http://localhost:8080 \
#   ./scripts/smoke-keycloak-gateway.sh

: "${KEYCLOAK_URL:=http://localhost:8089}"
: "${REALM:=ecommerce}"
: "${CLIENT_ID:=ecommerce-client}"
: "${CLIENT_SECRET:=}"
: "${USERNAME:=testuser}"
: "${PASSWORD:=testpass}"
: "${GATEWAY_URL:=http://localhost:8080}"

TOKEN_ENDPOINT="${KEYCLOAK_URL}/realms/${REALM}/protocol/openid-connect/token"

echo "Requesting token from ${TOKEN_ENDPOINT}"

if [[ -n "${CLIENT_SECRET}" ]]; then
  ACCESS_TOKEN="$(
    curl -sS -X POST "${TOKEN_ENDPOINT}" \
      -H "Content-Type: application/x-www-form-urlencoded" \
      --data-urlencode "grant_type=password" \
      --data-urlencode "client_id=${CLIENT_ID}" \
      --data-urlencode "client_secret=${CLIENT_SECRET}" \
      --data-urlencode "username=${USERNAME}" \
      --data-urlencode "password=${PASSWORD}" | \
      python3 -c 'import json,sys; print(json.load(sys.stdin)["access_token"])'
  )"
else
  ACCESS_TOKEN="$(
    curl -sS -X POST "${TOKEN_ENDPOINT}" \
      -H "Content-Type: application/x-www-form-urlencoded" \
      --data-urlencode "grant_type=password" \
      --data-urlencode "client_id=${CLIENT_ID}" \
      --data-urlencode "username=${USERNAME}" \
      --data-urlencode "password=${PASSWORD}" | \
      python3 -c 'import json,sys; print(json.load(sys.stdin)["access_token"])'
  )"
fi

echo "Token acquired. Running smoke calls via gateway..."

call() {
  local name="$1"
  local path="$2"
  local method="${3:-GET}"

  echo ""
  echo "== ${name} (${method} ${path}) =="
  curl -i -sS -X "${method}" \
    "${GATEWAY_URL}${path}" \
    -H "Authorization: Bearer ${ACCESS_TOKEN}" \
    -H "Content-Type: application/json"
  echo ""
}

call "Inventory check" "/api/inventory?productName=demo"
call "Product list" "/api/products"
call "Order list" "/api/orders"
call "Payment list" "/api/payments"
call "Shipping list" "/api/shippings"
call "Favourite list" "/api/favourites"

echo ""
echo "Smoke test finished."
