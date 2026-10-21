# #44 Redis 선착순 쿠폰 발급과 사용자용 쿠폰 목록 API 작업 개요

- 브랜치: `feat/44-coupon-redis-issue` (main `a394719` 기준, #45 머지 이후)
- 범위: promotion-service, common-lib(`ErrorCode`, 메시지), `deploy/apisix/apisix.yaml`(compose용 게이트웨이), `deploy/seed/`, `docker-compose.yml`
- 프론트엔드(`frontend/`)는 수정하지 않음

## 목적

QA가 쿠폰 발급에 1만~10만 건 부하를 건다. 기존 방식은 모든 발급 요청이 쿠폰 행(`promotion`) 하나의 잠금을 차례로 기다려서, 요청이 몰리거나 DB가 멀면(AWS → 온프렘) DB 연결 대기 시간 초과로 500이 난다. 수량 차감을 Redis로 옮겨 줄 서기를 없애고, 프론트가 기다리던 사용자용 쿠폰 목록 API를 같이 만든다.

## 결정 사항

| 항목 | 결정 |
|---|---|
| Redis 장애 시 | 발급을 503(`PRM-6004`)으로 막는다. MySQL 방식으로 자동 전환하면 복구 후 수량이 어긋나 초과 발급될 수 있다 |
| 기본 저장소 | `redis`. `PROMOTION_ISSUE_STORE=db`로 기존 방식을 쓸 수 있다(부하 비교용) |
| 목록 API | 이 브랜치에서 같이 만든다. 남은 수량과 발급 여부가 Redis 작업과 같은 데이터이기 때문 |

## 동작 방식

```
발급 요청
 → 오픈 여부 확인 (pod 메모리 캐시, 3초)
 → Redis Lua 한 번: 이미 받았나? → 남았나? → 수량 -1, 발급자 추가, 처리 중 기록
 → 당첨자만 MySQL promotion_issue INSERT
     성공: 처리 중 기록 삭제 → 201
     실패: Redis 되돌림 → 오류 응답
```

| Redis 키 | 내용 |
|---|---|
| `coupon:{id}:stock` | 남은 수량 |
| `coupon:{id}:users` | 발급받은 사용자 id 집합 |
| `coupon:{id}:pending` | MySQL 저장이 확인되지 않은 발급 (사용자 → 발급 시각) |

- `{id}` 해시 태그로 세 키가 같은 슬롯에 있어 Redis Cluster에서도 한 스크립트로 처리된다.
- **MySQL이 기준이다.** 키가 없으면(첫 발급, Redis 데이터 유실, 초기화) 다음 발급 때 `promotion_issue`로 다시 채운다. pod 안에서는 `ReentrantLock`, pod 사이에서는 Redis 잠금 키로 한 곳만 채운다.
- `issued_count`는 발급할 때마다 갱신하지 않고 5초마다 발급 행 수로 맞춘다. 발급마다 쿠폰 행을 UPDATE하면 잠금 병목이 다시 생기기 때문이다.

## 변경 내용

| 커밋 | 내용 |
|---|---|
| `115b035` | Redis 의존성과 접속 설정, `promotion.issue.store` 설정, compose 연결. Redis 기본 health는 끄고 `couponStore` 지표로 대신 표시 (아래 "health" 참고) |
| `ebe0a48` | `CouponStockStore`: 키 구조, MySQL에서 다시 채우기, 동시에 채우는 것 방지 |
| `b31421b` | 발급을 Redis Lua 차감으로 전환. MySQL 실패 시 되돌림. Redis 장애는 503. **실패 응답 코드 구분** (`PRM-6001~6004`) |
| `fecf2be` | 쿠폰 오픈 정보(활성 여부, 수량, 기간)를 pod 메모리에 3초 캐시 |
| `e84913c` | `issued_count`를 5초마다 발급 행 수로 동기화 |
| `631a87b` | 쿠폰 생성 시 Redis 수량 미리 채움, 삭제 시 키 삭제, 수정 시 캐시 비움. Redis 오류가 관리자 API를 실패시키지 않음 |
| `4be1b38` | 사용자용 쿠폰 목록 API `GET /promotion/storefront/promotions`, compose 게이트웨이 라우트 |
| `a583f8d` | 회차별 초기화용 `deploy/seed/promotion-coupon-reset-redis.sh` |
| `cfb9b1d` | 단위·통합 테스트 (Testcontainers로 실제 MySQL·Redis) |
| `c445175` | **검증 중 발견한 버그 수정**: Redis 시간 초과 뒤 늦게 실행된 발급을 되돌림 (아래 참고) |

### 검증 중 발견해서 고친 것

1. **스케줄 간격 표기 (`e84913c`에 포함)**: `@Scheduled(fixedDelayString = "5s")`는 이 프로젝트의 Spring 6.1에서 읽지 못해 **서비스가 기동에 실패**한다. 통합 테스트에서 발견했고, 설정값을 `Duration`으로 읽어 직접 등록하도록 바꿨다.
2. **시간 초과 뒤 남는 발급 (`c445175`)**: Redis가 잠시 멈춘 사이 발급 요청이 1초 시간 초과로 503을 받았다. 그런데 요청은 이미 Redis 연결에 실려 있어서, Redis가 돌아오자 실행됐다. 그 결과 Redis에는 "발급됨", MySQL에는 행이 없어 사용자는 쿠폰 없이 "이미 받음"을 받고 수량 1개가 사라졌다. 발급할 때 `pending`에 기록하고, 60초가 지나도 MySQL 행이 없으면 되돌리도록 고쳤다. 초과 발급은 일어나지 않고, 최대 약 70초 동안 1장이 덜 발급될 뿐이다.

## 프론트 연동 안내

### 쿠폰 목록

`GET /promotion/storefront/promotions` (토큰 없어도 됨. 토큰이 있으면 `claimed`가 채워짐)

```json
{
  "success": true,
  "code": "OK",
  "data": [
    {
      "promotionId": 6,
      "name": "선착순 10,000원 할인",
      "description": "Open now, 100 coupons",
      "discountType": "FIXED",
      "discountAmount": 10000,
      "discountPercentage": null,
      "minimumOrderPurchaseAmount": null,
      "issueLimit": 100,
      "issuedCount": 2,
      "startDate": "2026-10-21T01:20:16.489800Z",
      "endDate": "2026-10-28T02:20:16.489800Z",
      "claimed": false
    }
  ]
}
```

- 필드는 `couponMock.ts`의 `CouponEvent`와 같다(`discountType`, `discountPercentage` 추가).
- 대상: 활성 선착순 쿠폰 중 오픈 예정, 진행 중, 종료 후 1일 이내. 시작 시각 순, 최대 100개.
- 시각은 ISO 8601 UTC다.
- `issuedCount`는 Redis의 실시간 값이다. Redis가 안 되면 몇 초 늦은 `issued_count`로 대신한다.

### 발급 응답

`POST /promotion/storefront/promotions/{promotionId}/issue` (토큰 필요)

| 결과 | HTTP | `code` | `IssueResult` |
|---|---|---|---|
| 발급 | 201 | (본문은 발급 정보) | `success` |
| 이미 받음 | 409 | `PRM-6003` | `alreadyClaimed` |
| 매진 | 409 | `PRM-6002` | `soldOut` |
| 오픈 전·종료·비활성 | 400 | `PRM-6001` | `notOpen` |
| 일시적으로 발급 불가 (Redis 장애) | 503 | `PRM-6004` | `error` (잠시 후 재시도) |
| 없는 쿠폰 | 404 | `ERR-0404` | `error` |

이전에는 이미 받음과 매진이 모두 `ERR-0409`이었다. 이제 `code`로 구분한다.

## 설정

| 환경변수 | 기본값 | 설명 |
|---|---|---|
| `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD` | `localhost`, `6379`, 없음 | 쿠폰용 Redis |
| `REDIS_TIMEOUT` | `1s` | Redis 명령 시간 초과 |
| `PROMOTION_ISSUE_STORE` | `redis` | `redis` 또는 `db` |
| `PROMOTION_ISSUE_WINDOW_CACHE_TTL` | `3s` | 쿠폰 오픈 정보 캐시 시간 |
| `PROMOTION_ISSUE_COUNT_SYNC_INTERVAL` | `5s` | `issued_count` 동기화 간격 |
| `PROMOTION_ISSUE_STALE_CLAIM_AFTER` | `60s` | MySQL 행 없이 이 시간이 지난 발급을 되돌림. DB 연결 대기(30초)보다 길어야 한다 |

### health

k8s 프로브가 `/actuator/health`를 보므로, Redis 기본 health가 켜져 있으면 Redis 장애 때 promotion-service 전체가 빠진다(주문의 쿠폰 사용, 목록까지 중단). 그래서 Redis 기본 health는 끄고 `couponStore` 항목으로 보여준다. Redis가 안 되면 `couponStore`만 `UNKNOWN`이 되고 서비스 전체 상태는 `UP`을 유지한다.

## 검증

### 테스트
- 전체 모듈 빌드 통과. promotion-service 테스트 15개 통과 (단위 5, 통합 10)
- 통합 테스트(실제 MySQL·Redis 컨테이너):
  - 50장에 300명 동시 → 발급 50, 매진 250, 발급 행 50, Redis 수량 0
  - 같은 사용자 20회 동시 → 발급 1, 이미 받음 19
  - Redis 키가 없을 때 MySQL로 다시 채움, MySQL 중복이면 수량 되돌림
  - 시간 초과로 남은 발급 되돌림, MySQL 행이 있으면 유지, 처리 중인 최근 발급은 건드리지 않음
  - 목록의 `issuedCount`·`claimed`, `issued_count` 동기화
- Docker가 없으면 통합 테스트는 건너뛴다. Docker Engine 29 이상은 Testcontainers 1.19의 기본 API 버전을 거부하므로 `src/test/resources/docker-java.properties`에 `api.version=1.44`를 넣었다.

### 게이트웨이 경유 (로컬 compose, 34개 항목 통과)
- 목록: 비로그인 200·`claimed=false`, 로그인 `claimed=true`, 실시간 `issuedCount`, 필드가 `CouponEvent`와 일치
- 발급: 201, 이미 받음 409 `PRM-6003`, 매진 409 `PRM-6002`, 오픈 전 400 `PRM-6001`, 없는 쿠폰 404, 토큰 없음 401
- 시드 → 초기화 SQL + Redis 초기화 → 다시 발급 가능, 쿠폰 ID 유지, 수량은 MySQL로 다시 채워짐
- 관리자 쿠폰 생성 시 Redis 수량 채움, 삭제 시 키 삭제
- Redis 일시 정지: 발급 503 `PRM-6004`, 목록 200, 서비스 health `UP`(couponStore `UNKNOWN`), 복구 후 남은 발급이 되돌려지고 다시 발급, Redis 수량 = 100 - MySQL 발급 행

### DB 방식과 비교 (로컬, 같은 MySQL, 동시 요청 600건)

| 시나리오 | 방식 | 전체 시간 | p50 | p95 | 결과 |
|---|---|---|---|---|---|
| 100장에 600명 | db (pod 1) | 1.21초 | 797ms | 1,021ms | 201 100 / 409 500 |
| 100장에 600명 | redis (pod 1) | 0.51초 | 281ms | 423ms | 201 100 / 409 500 |
| 100장에 600명 | redis (pod 2개에 분산) | 0.59초 | 399ms | 524ms | 201 100 / 409 500 |
| 1,000장에 600명 (전원 당첨) | db | 1.32초 | 670ms | 1,202ms | 201 600 |
| 1,000장에 600명 (전원 당첨) | redis | 0.55초 | 347ms | 475ms | 201 600 |

- 모든 경우 발급 행 수가 한도와 같고 사용자 중복이 없었다.
- 로컬은 DB 왕복 시간이 거의 0이라 차이가 작게 나온다. DB가 멀수록(AWS → 온프렘) db 방식은 잠금 시간이 늘어 차이가 커진다. QA 부하 테스트에서 `PROMOTION_ISSUE_STORE`를 바꿔 같은 시나리오로 비교하면 된다.

## 회차별 초기화 (QA)

순서대로 실행한다. 쿠폰 ID는 바뀌지 않는다.

```bash
kubectl exec -i <mysql-pod> -- mysql -uroot -p"$MYSQL_ROOT_PASSWORD" promotionservice < deploy/seed/promotion-coupon-reset.sql
kubectl exec -i <redis-pod> -- env REDIS_PASSWORD="$REDIS_PASSWORD" sh < deploy/seed/promotion-coupon-reset-redis.sh
```

SQL만 실행하면 Redis에 이전 수량이 남아 쿠폰이 계속 매진으로 보인다. 시드를 처음 넣을 때는 Redis 작업이 필요 없다(첫 발급 때 채워짐).

## gitops 반영 항목

- **쿠폰용 Redis**
  - 전체(OCI·AWS)에 **하나**만 둔다. 클라우드마다 따로 두면 각자 수량을 차감해 초과 발급된다. AWS 쪽 promotion-service도 edge-proxy를 지나 이 Redis에 접속해야 한다
  - 쿠폰 수량이 들어 있으므로 데이터를 잃으면 안 된다: AOF(`appendonly yes`)와 HA(Sentinel 등)
  - APISIX rate limit용 Redis와 같은 인스턴스를 써도 되지만, 클라우드별로 둔 rate limit용 Redis와는 구분해야 한다
- **promotion-service 환경변수**: `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`(Secret). 나머지는 기본값으로 충분
- **ApisixRoute**: `GET /promotion/storefront/promotions`를 promotion-service로. 게이트웨이 인증 플러그인 없이 둔다(비로그인 조회 허용, 토큰은 서비스가 검증)
- **프로브**: 변경 없음. Redis 장애로 프로브가 실패하지 않는다

## 알려진 제약

- 쿠폰 정보를 다른 pod에서 수정하면 최대 3초 늦게 반영된다.
- `issued_count`(관리자 화면)는 최대 5초 늦다. 목록 API는 Redis 실시간 값을 쓴다.
- Redis 키에 만료 시간이 없다. 쿠폰 삭제 시 지워지고, 남아도 쿠폰당 키 3개라 부담은 작다.
- Redis 데이터가 사라진 순간에 MySQL 저장 중이던 발급은, 다시 채울 때 아직 MySQL에 없어 수량에서 빠지지 않는다. 그만큼(그 순간 진행 중이던 요청 수, 보통 수 건 이하) **초과 발급될 수 있다**. AOF와 HA로 Redis 데이터 유실 자체를 막는 것이 전제다.

## 남은 것

- 프론트: `hooks/useCoupons.ts`의 `fetchCoupons`, `issueCoupon`을 실제 API로 교체하고 `code`로 결과를 구분 (프론트 담당)
- QA: `PROMOTION_ISSUE_STORE=db`와 `redis`로 같은 시나리오를 돌려 비교
