# #37 백엔드 오류 수정 작업 개요

- 브랜치: `fix/37-backend-fixes` (main `e648d91` 기준)
- 범위: order-service, promotion-service, auth-service, product-service, common-lib
- 프론트엔드(`frontend/`)는 수정하지 않음

## 목적

inventory-service 없이 mock 결제가 동작하게 하고, promotion-service가 기동되게 하며, 로그인·상품 조회·주문 생성·쿠폰 조회의 백엔드 결함을 고친다.

## 변경 내용

### order-service

| 커밋 | 내용 |
|---|---|
| `897aed2` | 결제 품절 판단을 inventory 조회 대신 상품 상세의 `quantity <= 0`으로 변경. `CallAPI.receiverInventory`와 `InventoryStockDto` 삭제 |
| `177f8f6` | `OrderMappingHelper`의 cart 매핑을 저장·조회 양방향 모두 null 허용으로 변경. cartId가 없으면 cart를 연결하지 않음 |
| `0cf3a66` | `CallAPI`가 공용 `RestClient.Builder`에 호출마다 `baseUrl`을 바꾸던 구조를, 생성자에서 `clone()`으로 서비스별 클라이언트(auth, product, promotion)를 한 번만 만드는 구조로 변경 |
| `b71d1de` | `receiverProductDto`가 `ApiResponse<ProductDto>`를 풀어서 읽도록 변경. 인자 1개 오버로드는 `/product` context-path가 빠져 항상 404였으므로 인자 2개 메서드로 위임 |

### promotion-service

| 커밋 | 내용 |
|---|---|
| `1c07c2d` | `ProductService`가 주입받는 `RestClient` 빈 등록 (`RestClientConfig`). 이전에는 빈이 없어 기동 실패 |
| `8d84ca2` | 쿠폰 목록·상세 조회(`toPromotionDetail`)에서 product-service에 없는 `/backoffice/{products,categories,brands}/by-ids` 호출 제거 |

### auth-service

| 커밋 | 내용 |
|---|---|
| `945e902` | 가입 요청의 `roles`를 무시하고 항상 `USER`로 생성 |
| `9050978` | `/users/me`를 username 대신 JWT `sub`(keycloakUserId)로 조회 |
| `ad5b56b` | `/users/me` 조회 시 로컬 사용자 행이 없으면 JWT 정보(`preferred_username`, `email`, `name`, `realm_access.roles`)로 생성 (JIT). 동시 요청으로 unique 충돌이 나면 다시 조회 |

JIT 생성 규칙:
- 역할은 JWT realm role 중 `RoleName`(USER, PM, ADMIN)에 있는 값만 사용하고, 없으면 USER
- `users.email`, `users.gender`가 NOT NULL이므로 email이 없으면 `<sub>@local.invalid`, gender는 `UNKNOWN`으로 채움

### product-service

| 커밋 | 내용 |
|---|---|
| `13d0f00` | 상품 상세를 `ApiResponse.ok(dto)`로 변경. `GET /api/categories`가 `page`, `size`(기본 50, 최대 100)를 받아 `ApiResponse.ok(Page)`를 반환 |
| `a28a811` | `KeycloakRealmRoleConverter`를 연결하고 상품·카테고리 쓰기(POST/PUT/DELETE)에 `ADMIN` 권한 적용 |
| `4760ae0` | `Category.products`의 `CascadeType.ALL` 제거. 상품이 남은 카테고리 삭제 시 409 (`product.category.has.products`) |
| `58667de` | 없는 상품은 404 (`product.not.found`). 상품 ID 경로 변수를 `Integer`로 받아 잘못된 형식은 400 |
| `9593f28` | 카테고리가 null인 상품의 조회·저장 시 NPE 방지 (`ProductMappingHelper`) |

### 검증 중 발견해서 추가로 수정한 것 (이슈 체크리스트 외)

| 커밋 | 내용 |
|---|---|
| `aa43738` | 상위 카테고리 없이 카테고리를 생성하면 500. `CategoryMappingHelper`가 parent가 없을 때도 빈 `Category`를 연결해 Hibernate가 저장을 거부하던 문제 |
| `e3d75eb` | **common-lib** `JpaAutoConfiguration`에 `after = HibernateJpaAutoConfiguration.class` 추가. `@ConditionalOnBean(EntityManagerFactory)`가 Hibernate보다 먼저 평가되어 JPA auditing이 모든 서비스에서 꺼져 있었고, `AuditingHandler`가 필요한 promotion의 쿠폰 생성이 500으로 실패했음 |
| `f59f9d0` | 오류 메시지의 ID가 `99,999,999`처럼 천 단위 구분 기호로 표시되던 문제 |

`e3d75eb`의 영향: 이제 모든 서비스에서 auditing이 켜져 `created_by`, `last_modified_by` 등에 JWT `sub`(없으면 `system`)가 채워진다. 원래 의도된 동작(`JpaAutoConfiguration` Javadoc)이며, 전체 모듈 빌드와 4개 서비스 기동은 확인했다.

## 검증

### 빌드·단위 테스트
- `mvn verify` (`frontend/`를 제외한 13개 모듈 전체) 통과, 테스트 38개 통과
- auth-service에 테스트 추가: 가입 시 roles 무시, `/users/me` JWT 조회, JIT 생성(기존 사용자 재사용, realm role 매핑)

### 통합 검증 (로컬 docker, 33개 항목 모두 통과)
MySQL, Keycloak(realm import 포함)과 4개 서비스를 띄우고 curl로 완료 조건을 확인했다. APISIX는 띄우지 않았으므로 게이트웨이 경로는 검증하지 않았다(#38 범위).

| 완료 조건 | 결과 |
|---|---|
| inventory-service 없이 mock 결제가 동작 | 결제 COMPLETED, 쿠폰 USED 처리 확인. `quantity=0` 상품 결제는 409 `INV-3002`로 거절되고 NOT_STARTED로 저장 |
| promotion-service 정상 기동, backoffice 쿠폰 목록·상세 200 | 확인. 쿠폰 생성 201, 쿠폰 발급 201 |
| 상품 상세와 카테고리가 화면 형식으로 응답 | 상세 `data.productId`, 카테고리 `data.content` 확인 (브라우저 화면 확인은 하지 않음) |
| cart 없이 주문 생성 시 500 없음, 이후 목록·상세 조회 정상 | 확인. 주문 상세에 product-service 상품 정보가 채워짐 |
| roles=ADMIN으로 가입해도 USER | 가입 사용자의 JWT realm role에 ADMIN 없음 확인 |
| `/users/me`가 가입 사용자와 realm 시드 계정 모두 200 | testuser, adminuser, 가입 사용자 모두 200. 두 번째 호출은 기존 행 재사용 |
| 없는 상품 404, 잘못된 ID 400 | 확인 |
| 일반 사용자 쓰기 403, 비로그인 401, ADMIN 정상 | 확인 |
| 상품이 있는 카테고리 삭제 시 409, 상품 유지 | 확인 |

## 배포 시 주의

- 상품 응답 형식 변경(`13d0f00`)과 order `CallAPI` 수정(`b71d1de`)은 같이 배포되어야 한다. 이 브랜치 하나로 함께 나가므로 PR을 나누지 않는다.
- inventory-service는 이 브랜치가 배포된 뒤에 내릴 수 있다.

## gitops 반영 항목

- 이 브랜치 배포 후 inventory-service Deployment와 라우트 제거 가능
- 그 밖의 설정 변경은 없음 (환경변수·ConfigMap 추가 없음)

## 남은 것 (이 브랜치에서 하지 않음)

- 카테고리 경로 변수는 여전히 `String`을 `parseInt`하므로 `/api/categories/abc`는 500 (이슈 범위가 상품 ID였음)
- `/users/me` 응답(`UserResponse`)은 `fullname` 필드명이고 `roles`가 없다. 프론트 타입은 `fullName`, `roles`를 기대하므로 프론트 담당자와 응답 계약 확정 필요
- 가입 API의 `@LogPerformance(logInput = true)`가 요청 본문(비밀번호 포함)을 로그에 남김
- `CallAPI.receiverUserDto`는 존재하지 않는 경로(`/api/manager/user/{id}`)를 호출하고 예외가 무시되어 항상 비어 있음
- promotion-service의 `ProductService`는 이제 호출하는 곳이 없음. product-service에 by-ids API가 생기기 전까지는 삭제해도 됨
