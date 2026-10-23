# 백엔드 운영 보강 작업 개요

- 브랜치: `fix/backend-hardening` (main `ff86e2e` 기준)
- 범위: auth-service, order-service, product-service, promotion-service, common-lib, Dockerfile, 레포 설정 파일
- 프론트엔드(`frontend/`)는 수정하지 않음

## 목적

#37 이후 남은 항목 중 별도 결정 없이 고칠 수 있는 것을 정리한다. 여러 replica/클라우드에서 로그인이 깨지지 않게 하고, 로그와 레포에 비밀번호·토큰이 남지 않게 하며, 쓰이지 않는 코드를 걷어낸다.

## 변경 내용

| 커밋 | 내용 |
|---|---|
| `d7b6800` | **auth-service** SSO 로그인의 `state`와 `ticket`을 메모리(`ConcurrentHashMap`) 대신 MySQL `sso_handshake` 테이블에 저장. `/login`, `/callback`, `/session`이 서로 다른 replica나 클라우드로 가도 로그인이 이어짐. 꺼낼 때 행 잠금 후 삭제하므로 한 번만 사용 가능. 만료 행은 새 항목을 만들 때 정리 |
| `ee8e7f7` | **common-lib** 요청 로그(`HttpLoggingFilter`)의 본문과 쿼리스트링에서 비밀번호, access/refresh/id token, client secret, ticket, OAuth `code`/`state`를 `***`로 마스킹 (`SensitiveDataMasker`). 가입 API의 `@LogPerformance(logInput = true)` 제거 |
| `8f72e94` | `k8s/secrets.yaml`, `k8s/gateway/01-global-rule.yaml`의 평문 비밀번호를 `change-me`로 교체. 함께 넣었던 `.env` 추적 제외와 `.env.example` 추가는 `2730c24`에서 되돌림 (`.env`는 계속 추적하기로 결정) |
| `b3a27ac` | Dockerfile 12개의 베이스 이미지를 `ARG BASE_IMAGE`(기본값 `eclipse-temurin:21-jre-alpine`)로 변경. Harbor 미러 등 다른 레지스트리 이미지로 빌드 가능 |
| `a290acc` | **product-service** 카테고리 ID 경로 변수를 `Integer`로 받아 잘못된 형식은 400, 없는 카테고리는 404 (`product.category.not.found`) |
| `520c211` | **order-service** 존재하지 않는 경로를 호출하고 예외를 무시하던 `CallAPI.receiverUserDto`와 장바구니의 호출 3곳 제거 |
| `bf3df7d` | **promotion-service** 호출하는 곳이 없는 `ProductService`와 관련 설정(`RestClientConfig`, `ServiceUrlConfig`, 서킷브레이커 fallback) 제거 |

### sso_handshake 테이블

Liquibase changeset `auth-mysql-ddl-0002`가 기동 시 자동으로 만든다. 별도 작업은 필요 없다.

| 컬럼 | 설명 |
|---|---|
| `handle` (PK) | state 또는 ticket 값 (랜덤 32바이트, base64url) |
| `kind` | `STATE` 또는 `TICKET` |
| `payload` | STATE는 프론트 복귀 URL, TICKET은 토큰 JSON |
| `expires_at` | 만료 시각 (인덱스) |

## 검증

- `mvn verify` (`frontend/` 제외 전체 모듈) 통과, 테스트 49개 통과
- 추가한 테스트
  - `SsoSessionStoreTest` (5개): state·ticket 1회 사용, state를 ticket으로 쓸 수 없음, 만료 거부, 없는 값 거부
  - `SensitiveDataMaskerTest` (6개): JSON·폼·쿼리 마스킹, ApiResponse의 `code` 필드는 유지
- 로컬 docker에서 auth-service 2개 replica 기동과 `sso_handshake` 테이블 생성 확인
- **하지 않은 것**: 브라우저/curl로 replica를 넘나드는 SSO 로그인 전체 흐름은 확인하지 않았다 (단위 테스트까지만)

## 배포 시 주의

- **`.env`**: 로컬 개발용으로 계속 git에 추적한다. 비밀번호 6개(MySQL, Redis, Keycloak admin, 스토리지 키)는 모두 원본 프로젝트의 기본값 `admin`이므로, 실제 환경에서는 이 값을 쓰지 말고 gitops Secret 값을 사용한다.
- **Liquibase 동시 기동**: 빈 DB에 replica 여러 개가 동시에 처음 뜨면 `DATABASECHANGELOG` 생성이 겹쳐 하나가 실패할 수 있다 (이 브랜치 이전부터 있던 문제). 실패한 pod는 재시작하면 정상 기동한다. 최초 배포는 replica 1개로 올린 뒤 늘리는 것을 권장.

## gitops 반영 항목

- Secret 값은 gitops가 기준. 앱 레포의 `k8s/secrets.yaml`은 `change-me`이므로 그대로 적용하지 말 것
- 교체한 비밀번호를 gitops Secret과 APISIX `limit-count`의 `redis_password`에 반영
- (선택) Jenkins에서 Harbor 미러 이미지를 쓰려면 `docker build --build-arg BASE_IMAGE=<harbor>/eclipse-temurin:21-jre-alpine`
- 환경변수·ConfigMap 추가는 없음

## 남은 것 (결정 필요)

- 외부(QA VM 등)에서 받은 토큰의 issuer 불일치: `KC_HOSTNAME` 공개 주소 고정 + 서비스·APISIX 검증 issuer 변경 여부
- `/users/me` 응답 형식(`fullname` vs `fullName`, `roles` 포함 여부) 프론트와 확정
- storefront 쿠폰 목록 API
- QA IP(`10.0.1.99/32`) rate limit 예외 적용
