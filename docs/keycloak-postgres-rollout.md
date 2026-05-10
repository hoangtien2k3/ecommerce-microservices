# PostgreSQL + Keycloak Rollout Guide

## 1) Architecture baseline

- Identity Provider duy nhất: Keycloak.
- Mọi service backend chạy chế độ `oauth2-resource-server`.
- Bearer token được lấy từ Keycloak và gửi qua API Gateway.
- Gateway relay header `Authorization` xuống các service phía sau.
- Các service JPA chuyển sang PostgreSQL.

## 2) Required environment variables

Set tối thiểu các biến môi trường sau trước khi chạy:

- `JWT_ISSUER_URI` (vd: `http://localhost:8080/realms/ecommerce`)
- `KEYCLOAK_SERVER_URL` (vd: `http://localhost:8080`)
- `KEYCLOAK_REALM` (mặc định: `ecommerce`)
- `KEYCLOAK_CLIENT_ID` (mặc định: `ecommerce-client`)
- `KEYCLOAK_CLIENT_SECRET` (để trống với public client)
- `POSTGRES_USER` (vd: `postgres`)
- `POSTGRES_PASSWORD` (vd: `postgres`)
- `KAFKA_SERVERS`, `EUREKA_URI` theo môi trường hiện tại của bạn.

## 2.1) Boot Keycloak with preloaded realm

Repo đã có sẵn realm import mẫu tại `docker/keycloak/import/ecommerce-realm.json`.

Chạy Keycloak local:

```bash
docker compose -f docker-compose.yml up -d keycloak
```

Tài khoản quản trị Keycloak:

- username: `admin`
- password: `admin`

Test users đã import sẵn:

- `testuser` / `testpass` (role: `USER`)
- `adminuser` / `adminpass` (roles: `ADMIN`, `MANAGER`)

Client đã import sẵn:

- `ecommerce-client` (public client, bật direct access grants)

## 3) Keycloak role convention

Realm roles chuẩn toàn hệ thống:

- `ADMIN`
- `USER`
- `MANAGER`

Converter hiện tại chấp nhận cả `ROLE_ADMIN` và `ADMIN` để tương thích annotation cũ.

## 4) Expected token flow

1. Client gọi `AUTH-SERVICE` (`/api/auth/login`) để lấy access token/refresh token từ Keycloak.
2. Client gọi API qua `api-gateway` với header `Authorization: Bearer <token>`.
3. Gateway relay token xuống service đích.
4. Service đích verify token qua `issuer-uri` của Keycloak và authorize theo role.

## 5) Quick verification checklist

- [ ] `mvn -DskipTests compile` pass toàn repo.
- [ ] Keycloak realm có đủ roles (`ADMIN`, `USER`, `MANAGER`).
- [ ] User test đã được gán role đúng.
- [ ] Token từ Keycloak gọi được endpoint qua gateway.
- [ ] Endpoint role-protected trả `403` nếu role không phù hợp.
- [ ] Service kết nối PostgreSQL thành công, Liquibase chạy sạch.

## 5.1) Smoke test command

Sau khi service đã chạy:

```bash
./scripts/smoke-keycloak-gateway.sh
```

Hoặc override runtime:

```bash
KEYCLOAK_URL=http://localhost:8080 \
REALM=ecommerce \
CLIENT_ID=ecommerce-client \
USERNAME=testuser \
PASSWORD=testpass \
GATEWAY_URL=http://localhost:8080 \
./scripts/smoke-keycloak-gateway.sh
```

## 6) Production hardening suggestions

- Bật HTTPS cho Keycloak + Gateway.
- Rotation secret/credential qua vault.
- Tách DB từng service bằng schema/instance riêng.
- Bật metrics + alert cho `401/403` spikes và DB migration failures.
