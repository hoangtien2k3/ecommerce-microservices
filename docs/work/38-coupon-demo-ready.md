# #38 선착순 쿠폰 시연·부하 검증 환경 준비 작업 개요

- 브랜치: `chore/38-coupon-demo-ready` (main `ff86e2e` 기준, #37 머지 이후)
- 범위: `deploy/apisix/apisix.yaml`(compose용 게이트웨이), 서비스 4개의 `Dockerfile`과 `application.yml`, `deploy/seed/`
- 프론트엔드(`frontend/`)와 앱 레포의 `k8s/`는 수정하지 않음

## 목적

선착순 쿠폰을 게이트웨이를 통해 시연하고, 부하 상황에서 발급 한도가 지켜지는지 검증할 수 있는 환경을 준비한다.

## 체크리스트 진행 상황

| 항목 | 상태 |
|---|---|
| 게이트웨이 라우트 정리 | 완료. backoffice만 수정, 발급 경로는 정상이라 유지 |
| APISIX CORS에 admin origin 추가 | 완료 |
| 쿠폰 발급 rate limit 조정 | 완료 (2개 커밋) |
| JVM·JDBC 시간대 UTC 통일 | 완료. JDBC URL 대신 Hibernate 설정 사용 (아래 참고) |
| 쿠폰 생성용 시드 SQL | 완료. QA 요청으로 1,000장 쿠폰과 회차별 초기화 SQL 추가 |
| (QA 요청) 부하 발생기 IP rate limit 예외 | 완료. compose 설정만, k8s는 gitops 반영 필요 |
| 선착순 검증용 k6 시나리오 | 하지 않음. QA 담당 |
| (팀 확인 후) 앱 레포의 `k8s/` 폴더 제거 | 하지 않음. 팀 확인이 필요 |

## 변경 내용

| 커밋 | 내용 |
|---|---|
| `fc80fee` | `promotion-backoffice` 라우트에 `/promotion/backoffice/promotions`(끝 슬래시 없음)를 추가. 쿠폰 목록(GET), 생성(POST), 수정(PUT)이 404였음 |
| `cb16373` | CORS `allow_origins`에 `http://localhost:3001`, `http://admin.ecommerce.local`, `http://admin.ecommerce.local:9090` 추가 |
| `21a3c44` | 전역 `limit-count` 키를 `remote_addr`에서 `$remote_addr $http_authorization` 조합으로 변경. 한 IP에서 여러 사용자가 요청해도 사용자마다 분당 1200회를 따로 가짐 |
| `0110efd` | 서비스 4개의 `Dockerfile` ENTRYPOINT에 `-Duser.timezone=UTC` 추가, `application.yml`에 `spring.jpa.properties.hibernate.jdbc.time_zone: UTC` 추가 |
| `ede92ab` | `deploy/seed/promotion-coupon-demo.sql` 추가 |
| `5e9bd8e` | rate limit에 IP 기준 분당 12000회 상한을 별도 global rule로 추가 |
| `e8dc0ad` | 시드에 `demo-first-come-1000`(1,000장) 추가, 회차별 초기화 `deploy/seed/promotion-coupon-reset.sql` 추가 |
| `cf7b7f5` | limit-count 두 규칙 모두 `_meta.filter`로 QA 부하 발생기 `10.0.1.99/32`(OCI qa-01)를 제외 |

### 쿠폰 발급 경로를 바꾸지 않은 이유
`/promotion/storefront/promotions/*/issue`처럼 중간에 `*`가 있는 패턴을 APISIX 3.9.1이 정상 매칭하는 것을 실제로 확인했다. 토큰이 있으면 201, 없으면 401이 난다.

### rate limit을 두 규칙으로 나눈 이유
global rule은 라우트의 인증 플러그인보다 먼저 실행된다. 그래서 키에 들어간 `Authorization` 헤더는 검증되지 않은 값이고, 헤더를 매번 바꿔 보내면 사용자별 한도를 우회할 수 있다(`21a3c44`만 있을 때의 허점). 그래서 IP 기준 상한을 별도 규칙(`5e9bd8e`)으로 추가했다.

| 규칙 | 키 | 한도 | 역할 |
|---|---|---|---|
| global rule 1 | IP + Authorization | 분당 1200회 | 사용자별 한도 |
| global rule 2 | IP | 분당 12000회 | 헤더를 바꿔 우회하는 것 차단, 한 IP의 전체 상한 |

`limit-count`는 고정 창(60초) 방식이라 한도 안에서는 순간적인 몰림도 허용된다.

### QA 부하 발생기 예외
QA는 `10.0.1.99`(OCI qa-01) 한 대에서 게이트웨이로 직접 부하를 건다. 두 규칙 모두 이 IP를 건너뛰도록 플러그인 `_meta.filter`를 넣었다. 다른 IP는 그대로 제한된다.

```yaml
limit-count:
  _meta:
    filter:
    -
      - remote_addr
      - "!"
      - ipmatch
      -
        - 10.0.1.99/32
```

- 한 줄 배열 표기(`- [remote_addr, "!", ipmatch, [...]]`)는 standalone APISIX의 YAML 파서가 읽지 못해 **게이트웨이가 기동에 실패**한다. compose용 `apisix.yaml`에는 블록 표기만 쓴다. (ApisixRoute CRD나 Admin API는 일반 YAML/JSON이라 상관없음)
- CIDR 값이 잘못되면(예: `/32`만 남은 경우) 필터가 모든 요청을 통과시켜 **제한이 아예 꺼진다**. 값을 바꿀 때 주의한다.
- 예외 IP는 게이트웨이가 보는 `remote_addr` 기준이다. k8s에서 출발지 IP가 노드나 ingress IP로 바뀌면 예외가 적용되지 않는다 (아래 gitops 항목 참고).

### UTC를 JDBC URL이 아니라 Hibernate 설정으로 지정한 이유
compose와 k8s가 `SPRING_DATASOURCE_URL`을 환경변수로 통째로 덮어쓰기 때문에, yml의 JDBC URL에 붙인 옵션(`connectionTimeZone` 등)은 적용되지 않는다. `hibernate.jdbc.time_zone`은 URL과 무관하게 적용된다. JVM 시간대는 Jenkins가 Dockerfile로 이미지를 빌드하므로 Dockerfile에 넣었다(jib 설정은 사용되지 않음).

날짜만 받는 필드(`PromotionPostVm.startDate`/`endDate`, `yyyy-MM-dd`)는 UTC 자정, 즉 한국 시간 오전 9시로 저장된다. 쿠폰 오픈 시각을 한국 시간 자정에 맞추려면 시드 SQL처럼 DB에 직접 넣거나 API가 시각까지 받도록 바꿔야 한다.

### 시드 SQL
`deploy/seed/promotion-coupon-demo.sql`. Liquibase가 아닌 수동 실행 스크립트이며, slug 기준으로 다시 실행해도 중복되지 않는다. 시각은 실행 시점 기준 UTC이다.

| slug | 상태 | 발급 응답 |
|---|---|---|
| `demo-first-come-open` | 발급 중, 100장 | 201 |
| `demo-first-come-1000` | 발급 중, 1,000장 (QA 규모 테스트용) | 201 |
| `demo-first-come-sold-out` | 발급 중, 0장 | 409 `This coupon is sold out.` |
| `demo-first-come-upcoming` | 내일 시작, 50장 | 400 `This coupon is not open for claims.` |

```bash
# compose
docker compose exec -T mysql mysql -uroot -p"$MYSQL_ROOT_PASSWORD" promotionservice < deploy/seed/promotion-coupon-demo.sql
# k8s
kubectl exec -i <mysql-pod> -- mysql -uroot -p"$MYSQL_ROOT_PASSWORD" promotionservice < deploy/seed/promotion-coupon-demo.sql
```

발급 API의 `promotionId`는 slug가 아니라 `promotion.id`(숫자)다. 시드를 넣은 뒤 마지막 SELECT 결과의 `id`를 QA에 전달한다.

**회차별 초기화**: `deploy/seed/promotion-coupon-reset.sql`. 발급 이력을 지우고 `issued_count`를 0으로 되돌린다. 쿠폰 행은 남으므로 `promotionId`는 바뀌지 않는다. 가입한 테스트 사용자도 그대로 재사용할 수 있다.

```bash
kubectl exec -i <mysql-pod> -- mysql -uroot -p"$MYSQL_ROOT_PASSWORD" promotionservice < deploy/seed/promotion-coupon-reset.sql
```

시작·종료 시각까지 다시 맞추려면(시드 후 7일이 지나 종료된 경우 등) 쿠폰을 지우고 시드를 다시 실행한다. 이때는 `promotionId`가 바뀐다.

```sql
DELETE FROM promotion_issue WHERE promotion_id IN (SELECT id FROM promotion WHERE slug LIKE 'demo-first-come-%');
DELETE FROM promotion WHERE slug LIKE 'demo-first-come-%';
```

`usage_limit`은 엔티티에서 기본형 `int`이므로 NULL이면 매진·오픈 전 판정에서 500이 난다. 시드는 API와 같은 기본값 0을 넣는다.

## 검증

로컬 docker로 MySQL, Keycloak, APISIX와 서비스 4개를 띄우고 게이트웨이를 통해 확인했다.

| 완료 조건 | 결과 |
|---|---|
| admin backoffice 호출에 CORS 오류가 없음 | `localhost:3000`, `localhost:3001`, `admin.ecommerce.local` preflight에 `Access-Control-Allow-Origin` 응답 확인 |
| 쿠폰 생성·목록 조회와 발급이 게이트웨이를 통해 정상 응답 | 생성 201, 목록·상세 200, 발급 201. 토큰 없음 401, USER의 backoffice 접근 403 |
| 부하 시 rate limit이 아니라 발급 한도에 따라 성공/매진이 갈림 | 같은 IP에서 사용자 A가 1200회 후 429를 받아도 사용자 B와 비로그인 요청은 200. 헤더를 바꿔 우회하면 IP 상한에서 429 |
| `issued_count`가 `issue_limit`을 넘지 않고 발급 행 수와 일치 | 한도 5장 쿠폰에 사용자 20명이 동시에 요청하자 201 5건, 409 15건, `issued_count` 5, 발급 행 5 |
| 시작/종료 시각이 시간대 차이 없이 판정 | promotion-service를 KST JVM으로 띄워도 `issued_at`이 MySQL `UTC_TIMESTAMP()`와 일치. 오픈 예정 쿠폰은 400, 발급 중 쿠폰은 201 |

- 전체 모듈 `mvn clean verify` 통과, 테스트 38개 통과
- #37 통합 검증 33개도 다시 실행해 모두 통과 (회귀 없음)
- 동시성 확인은 정식 부하 테스트가 아니라 발급 한도 동작 확인용이다. 부하 시나리오는 QA의 k6로 진행한다.
- QA 예외(`cf7b7f5`): APISIX 3.9.1에 같은 규칙을 한도만 낮춰(3회, 5회) 띄워 확인했다. 예외 IP가 요청 IP와 다르면 4번째 요청부터 429, 요청 IP(또는 그 대역)와 같으면 8회 모두 200
- 1,000장 시드와 초기화 SQL: MySQL 8.0에 promotion-service DDL을 적용하고 확인했다. 시드를 두 번 실행해도 4건만 생성. 발급 이력을 넣은 뒤 초기화하면 `issued_count` 0, 발급 행 0, id 유지

## gitops 반영 항목

이 브랜치의 게이트웨이와 시간대 변경은 compose용 설정과 이미지에만 들어간다. k8s 배포에는 gitops에서 같은 내용을 반영해야 한다.

- **ApisixRoute** (`promotion-service`):
  - backoffice 경로에 `/promotion/backoffice/promotions`(끝 슬래시 없음) 추가
- **APISIX global rule**:
  - CORS `allow_origins`에 `http://admin.ecommerce.local` 추가
  - limit-count를 두 규칙으로 구성: IP + Authorization 기준 분당 1200회, IP 기준 분당 12000회
  - k8s 게이트웨이는 limit-count `policy: redis`를 쓰므로 두 규칙 모두 같은 Redis 설정을 사용
  - ingress가 앞에 있으면 `remote_addr`가 ingress Pod IP가 되어 모든 사용자가 한 IP로 잡힌다. APISIX `real-ip` 플러그인이나 ingress에서 클라이언트 IP를 전달하도록 설정 필요
  - 두 limit-count 규칙에 QA 예외 `_meta.filter`(`remote_addr`가 `10.0.1.99/32`가 아닐 때만 적용) 추가
  - QA는 게이트웨이를 내부망으로 직접 호출한다. 게이트웨이 Service가 `externalTrafficPolicy: Cluster`(기본값)이면 출발지 IP가 노드 IP로 바뀌어 예외가 적용되지 않으므로 `Local`로 설정하거나 `real-ip`로 원래 IP를 복원해야 한다. 적용 후 게이트웨이 access log에 `10.0.1.99`가 찍히는지 확인
  - QA에 게이트웨이 내부 주소(IP:포트) 전달 필요
- **시간대**:
  - 이미지에 `-Duser.timezone=UTC`가 들어가므로 별도 설정은 필요 없다
  - `JAVA_TOOL_OPTIONS`나 `TZ`로 다른 시간대를 지정하지 않아야 한다
- **시드**: 시연 환경 MySQL에 `deploy/seed/promotion-coupon-demo.sql`을 수동 실행. 회차별 초기화는 `promotion-coupon-reset.sql`

## 팀 확인이 필요한 것

- **앱 레포 `k8s/` 폴더 제거**: 위 gitops 항목이 반영된 것을 확인한 뒤 별도 커밋으로 진행
- **IP 상한 분당 12000회**: QA 부하 발생기는 예외 처리했으므로, 그 밖의 단일 IP(NAT 등) 트래픽 기준으로 적절한지 확인
- **QA 토큰 발급**: QA는 클러스터 밖에서 Keycloak 토큰을 받으므로 issuer가 서비스의 검증 값(`http://keycloak:8080/realms/ecommerce`)과 달라 401이 난다. Keycloak 공개 주소 고정(`KC_HOSTNAME`)과 검증 issuer 변경 여부 결정 필요
- **프론트 쿠폰 페이지와 응답 계약**: 이미 발급(`already.claimed`)과 매진(`sold.out`)이 둘 다 `code: ERR-0409`이고 `message`(다국어 문구)로만 구분된다. 프론트 목업은 메시지 키로 구분하도록 되어 있으므로, storefront 쿠폰 목록 API를 만들 때 구분 가능한 코드를 함께 정해야 한다
