# Briefly Engineering Guide

Java 백엔드·DB·운영 전반을 정리한 학습/면접 참고 문서입니다.  
Briefly MVP(Servlet/JSP/JDBC)와 Spring Boot 확장 시나리오를 함께 대조합니다.

---

## 1. Java 코드 스타일 / Clean Code

### 이름 짓기

| 대상 | 규칙 | 예시 (Briefly) |
| --- | --- | --- |
| 클래스 | 명사, 역할이 드러나게 | `FundService`, `ApplicationDao` |
| 메서드 | 동사+목적어 | `findByUserId`, `updateStatus` |
| 변수 | 의미 있는 이름, 약어 지양 | `fundId` (O), `fid` (X) |
| 상수 | `UPPER_SNAKE_CASE` | `SESSION_USER` |
| 불리언 | `is/has/can` 접두사 | `isWatched`, `existsPending` |

### 클래스와 메서드 책임 분리 (SRP)

- **Servlet/Controller**: HTTP 요청 파라미터 수신, 응답(JSP forward/redirect)만
- **Service**: 비즈니스 규칙 (금액 검증, 중복 신청 차단)
- **DAO/Repository**: SQL 실행, ResultSet 매핑
- **DTO**: 화면·API 전달용 데이터
- **Model/Entity**: DB 행과 1:1 대응하는 도메인 객체

한 메서드는 한 가지 일만. 20~30줄 넘으면 분리 검토.

### 중복 제거

- 반복 SQL → DAO 메서드로 추출
- 반복 검증 → Service private 메서드 또는 Validator
- 반복 JSP 레이아웃 → include/header/footer 분리
- 매직 넘버/문자열 → enum 또는 상수 (`FundApplication.Status.PENDING`)

### 예외 처리

```
잘못된 입력 (IllegalArgumentException) → 사용자 메시지 (400)
인증 실패 → 로그인 redirect
권한 없음 → forbidden.jsp
DB 오류 (SQLException) → 로그 남기고 공통 error.jsp
```

- catch 후 아무것도 안 하지 않기
- 예외를 삼키지 않고 로그 + 적절한 응답
- Service는 비즈니스 예외, DAO는 SQLException 전파 또는 래핑

### DTO / Entity / Service 역할

| 계층 | 역할 | Briefly 예시 |
| --- | --- | --- |
| Entity (Model) | DB 테이블 구조 반영 | `Fund`, `FundApplication` |
| DTO | 외부(화면/API)에 노출할 필드만 | `FundDto` (password 제외) |
| Service | 규칙 + 트랜잭션 조율 | `ApplicationService.apply()` |

---

## 2. Spring DI / 계층 분리

### 계층 역할

| 계층 | Spring | Servlet MVP |
| --- | --- | --- |
| Controller | `@RestController` / `@Controller` | `AuthServlet`, `FundServlet` |
| Service | `@Service` | `FundService` |
| Repository | `@Repository` + JPA | `FundDao` (JDBC) |

### 의존성 주입 (DI)

```java
// 직접 생성 (현재 Briefly)
private final FundDao fundDao = new FundDao();

// Spring DI
@Service
public class FundService {
    private final FundRepository fundRepository;
    public FundService(FundRepository fundRepository) {
        this.fundRepository = fundRepository;
    }
}
```

### 객체를 직접 만들지 않고 주입받는 이유

1. **테스트**: Mock Repository로 Service 단위 테스트 가능
2. **교체 용이**: 구현체 변경 시 생성 코드 수정 불필요
3. **생명주기 관리**: Spring이 싱글톤/스코프 관리
4. **결합도 감소**: Service가 구체 DAO 클래스에 의존하지 않음

### 계층 분리가 유지보수에 좋은 이유

- UI(JSP/React) 변경이 DB 접근에 영향 없음
- 비즈니스 규칙 변경 시 Service만 수정
- DB 스키마 변경 시 DAO/Repository만 수정
- 팀별 병렬 작업 가능 (화면 / API / DB)

---

## 3. Clean Architecture / Dependency Rule

### 의존성 방향

```
[Framework] Servlet, JSP, JDBC
      ↑
[Interface Adapters] Controller, DAO, DTO Mapper
      ↑
[Use Cases] Service (비즈니스 규칙)
      ↑
[Entities] Fund, User, FundApplication
```

**규칙**: 안쪽(도메인)은 바깥(프레임워크)을 모른다.  
Service는 `HttpServletRequest`를 모르고, Servlet은 Service만 호출.

### 비즈니스 로직과 외부 기술 분리

- `amount > 0` 검증 → Service (비즈니스)
- `PreparedStatement` 실행 → DAO (인프라)
- Session 저장 → Servlet/Filter (프레임워크)

### 테스트 가능한 구조

- Service + Mock DAO → DB 없이 단위 테스트
- Integration Test → 실제 DB(Testcontainers) + 전체 흐름

### 변경 영향 범위 줄이기

| 변경 | 영향 범위 |
| --- | --- |
| MySQL → PostgreSQL | DAO/Repository, db.properties |
| JSP → React API | Controller만 JSON 응답으로 변경 |
| PENDING 외 상태 추가 | enum + Service 검증 |

---

## 4. Entity / DTO / Value Object

### Entity란?

- **JPA Entity**: `@Entity`로 매핑, 영속성 컨텍스트 관리
- **Plain Model** (Briefly): JDBC `ResultSet` → 객체 매핑, DB 행 표현

### Entity와 Table의 관계

- 일반적으로 **테이블 1개 ↔ Entity 1개**
- `@Table(name = "funds")`, `@Column(name = "risk_grade")`
- Briefly: `Fund` ↔ `funds` 테이블

### @Entity, @Id

```java
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
```

### DTO와 Entity 분리 이유

| 이유 | 설명 |
| --- | --- |
| 보안 | `password_hash`를 화면에 노출하지 않음 |
| API 안정성 | DB 컬럼 변경이 API 스펙에 직접 영향 없음 |
| 순환 참조 방지 | JPA 연관관계 양방향 시 JSON 직렬화 문제 |
| 화면 최적화 | 목록용 DTO는 id, name만 포함 |

### Value Object (VO)

- **식별자 없이 값으로만 구분**되는 불변 객체
- 예: `Money(amount, currency)`, `RiskGrade(1~5)`, `Email`
- equals/hashCode를 값 기준으로 구현
- Entity의 필드를 원시 타입 대신 VO로 감싸면 검증·의미가 한곳에 모임

---

## 5. Repository / JPA

### Repository 역할

- 도메인 객체의 **영속성 추상화**
- JDBC DAO와 동일한 위치, 인터페이스로 CRUD 선언

```java
public interface FundRepository extends JpaRepository<Fund, Long> {
    List<Fund> findByStatus(FundStatus status);
}
```

### CRUD

| 연산 | JPA | JDBC (Briefly) |
| --- | --- | --- |
| Create | `save()` | `INSERT` |
| Read | `findById()` | `SELECT` |
| Update | `save()` (변경 감지) | `UPDATE` |
| Delete | `delete()` | `DELETE` |

### Query Method

- 메서드 이름으로 쿼리 자동 생성: `findByEmailAndRole(String email, Role role)`

### Custom Query

- `@Query("SELECT f FROM Fund f WHERE f.status = :status")`
- 복잡한 JOIN, 집계는 JPQL 또는 Native Query

### Service → Repository 흐름

```
Controller → Service.apply(userId, fundId, amount)
              → validate amount
              → repository.existsPending(...)
              → repository.save(application)
              → return ApplicationDto
```

---

## 6. Java Collections

| 자료구조 | 특징 | 사용 시점 |
| --- | --- | --- |
| **List** | 순서 O, 중복 O | 상품 목록, 신청 내역 |
| **Set** | 순서 X(LinkedHashSet 제외), 중복 X | 관심상품 fundId 집합, 중복 제거 |
| **Map** | Key-Value | id → Fund 캐시, 세션 속성 |
| **Queue** | FIFO | 작업 대기열, BFS |
| **PriorityQueue** | 우선순위 정렬 | 위험등급 높은 알림 우선 처리 |
| **Deque** | 양끝 삽입/삭제 | 슬라이딩 윈도우, undo |
| **TreeMap/TreeSet** | 정렬 유지 (Red-Black) | 등급순 정렬, 범위 조회 |

### Briefly 예시

```java
// 관심상품 fundId 목록 → 알림 조회
List<Long> fundIds = watchlists.stream().map(Watchlist::getFundId).toList();

// 중복 관심상품 방지 → DB UK + Set으로 메모리 중복 체크(배치 시)
Set<Long> watchedFundIds = new HashSet<>();
```

---

## 7. 알고리즘 / 시간복잡도

### 기본 표기

| 표기 | 의미 | 예 |
| --- | --- | --- |
| O(1) | 상수 | HashMap get |
| O(log n) | 로그 | Binary Search, B-tree Index |
| O(n) | 선형 | 리스트 전체 순회 |
| O(n log n) | 정렬 | `Collections.sort` |
| O(n²) | 이중 반복 | naive 중복 비교 |

### 공간복잡도

- 추가 배열/Map 사용량. in-place 정렬은 O(1) 추가 공간.

### 반복문 최적화

- 중첩 루프 제거: `Map`으로 O(n²) → O(n)
- DB에서 필터링 (WHERE) vs 애플리케이션에서 필터링

### 정렬 / 탐색

- **정렬**: DB `ORDER BY report_date DESC` (인덱스 활용)
- **선형 탐색**: O(n), 소량 데이터
- **Binary Search**: 정렬된 배열에서 O(log n)
- **Hash 조회**: `HashMap.get()` O(1) 평균

### 중복 제거

- `Set` / `DISTINCT` / DB `UNIQUE KEY` (watchlists UK)

---

## 8. DB 쿼리 / 인덱스 / 실행계획

### 기본 절

```sql
-- WHERE: ACTIVE 상품만 (FR-005)
SELECT * FROM funds WHERE status = 'ACTIVE';

-- JOIN: 관심상품 기준 알림 (FR-013)
SELECT ra.* FROM risk_alerts ra
INNER JOIN watchlists w ON w.fund_id = ra.fund_id
WHERE w.user_id = ?;

-- ORDER BY + LIMIT: 페이징
SELECT * FROM fund_applications
WHERE user_id = ?
ORDER BY created_at DESC
LIMIT 10 OFFSET 0;
```

### Index

- **B-tree Index**: `=`, `<`, `>`, `BETWEEN`, `ORDER BY`에 유리
- Briefly: `idx_funds_status`, `idx_applications_user`, UK `uk_watchlist_user_fund`

### EXPLAIN

```sql
EXPLAIN SELECT * FROM funds WHERE status = 'ACTIVE';
```

| type | 의미 |
| --- | --- |
| ALL | Full Table Scan (인덱스 미사용) |
| ref/range | Index Scan |
| const | PK/UK 단건 조회 |

### N+1 문제

```
// 나쁜 예: 신청 N건 → 상품 N번 조회
for (Application app : applications) {
    fundDao.findById(app.getFundId()); // N번
}

// 좋은 예: IN 절 또는 JOIN 한 번
SELECT a.*, f.name FROM fund_applications a JOIN funds f ON ...
```

### 페이징

- `LIMIT/OFFSET`: 단순, OFFSET 커지면 느려짐
- **Keyset pagination**: `WHERE id < ? ORDER BY id DESC LIMIT 10` (대량 데이터)

---

## 9. Spring Boot Test

| 종류 | 범위 | 도구 |
| --- | --- | --- |
| **Unit Test** | Service 단독, Mock | JUnit 5, Mockito |
| **Integration Test** | Service + DB | `@SpringBootTest`, Testcontainers |
| **Controller Test** | HTTP 레이어 | `@WebMvcTest`, MockMvc |
| **Repository Test** | DB 쿼리 | `@DataJpaTest` |
| **예외 케이스** | 잘못된 금액, 중복 신청 | `assertThrows` |
| **회귀 테스트** | CI에서 매 PR 실행 | GitHub Actions |

### Briefly (Servlet) 대응

```java
@Test
void apply_rejectsZeroAmount() {
    ApplicationService service = new ApplicationService();
    assertThrows(IllegalArgumentException.class,
        () -> service.apply(1L, 1L, BigDecimal.ZERO));
}
```

---

## 10. 유지보수성

| 원칙 | 설명 |
| --- | --- |
| **모듈성** | Auth / Fund / Application 모듈 분리 |
| **재사용성** | `WebUtil.forward`, `PasswordUtil.hash` |
| **분석 용이성** | SRS → SDD → 코드 추적 가능 |
| **수정 용이성** | 계층 분리로 변경 범위 국소화 |
| **테스트 용이성** | Service 단위 테스트 |
| **변경 영향 최소화** | DTO로 API/화면과 DB 분리 |

---

## 11. Spring Boot External Config

### 환경 분리

```
application.yml          # 공통
application-local.yml    # 로컬
application-dev.yml      # 개발
application-prod.yml     # 운영
```

### Profile

```yaml
spring:
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:local}
```

```bash
java -jar briefly.jar --spring.profiles.active=prod
```

### 환경 변수 / 외부 설정

```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

Briefly JDBC 대응: `db.properties` + 환경별 파일 또는 시스템 프로퍼티 `-Ddb.url=...`

---

## 12. Docker / 배포 기초

### Dockerfile (Spring Boot 예시)

```dockerfile
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/briefly.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Servlet WAR

```dockerfile
FROM tomcat:10-jdk17
COPY target/briefly.war /usr/local/tomcat/webapps/briefly.war
EXPOSE 8080
```

### 흐름

```
소스 → mvn package → WAR/JAR → docker build → image → docker run
```

### 환경 변수 주입

```bash
docker run -e DB_URL=jdbc:mysql://db:3306/briefly briefly:latest
```

---

## 13. GitHub Actions CI/CD

```yaml
name: CI
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      - run: cd back && mvn -B verify
      - run: cd front && npm ci && npm run build
```

### Deploy 흐름

```
push → CI(build + test) → image build → registry push → K8s/서버 deploy
```

---

## 14. Flyway DB Migration

### 개념

- `V1__create_users.sql`, `V2__add_amount_to_applications.sql`
- **versioned migration**: 순서 보장, 이력 테이블 `flyway_schema_history`

### Seed vs Migration

| 구분 | 용도 |
| --- | --- |
| Migration | 스키마 변경 (DDL) |
| Seed | 초기/테스트 데이터 (선택) |

### 여러 환경에 같은 순서로 적용하는 이유

- dev/staging/prod 스키마 **일치** 보장
- 배포 실패 시 어느 버전까지 적용됐는지 추적
- 수동 SQL 실행 실수 방지

Briefly 현재: `back/dB/schema.sql` + `seed.sql` → Flyway 도입 시 `db/migration/V1__...`로 전환

---

## 15. Spring Boot Actuator

| Endpoint | 용도 |
| --- | --- |
| `/actuator/health` | 앱 생존 + DB 연결 상태 |
| `/actuator/metrics` | JVM, HTTP, DB 풀 지표 |
| `/actuator/info` | 버전, 빌드 정보 |

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics
```

Servlet/Tomcat: 별도 health servlet 또는 load balancer TCP check

---

## 16. Kubernetes Probe

| Probe | 질문 | 실패 시 |
| --- | --- | --- |
| **Liveness** | 프로세스 살아있나? | 컨테이너 재시작 |
| **Readiness** | 트래픽 받을 준비됐나? | Service에서 제외 |
| **Startup** | 기동 완료됐나? | (느린 기동 시) Liveness 보호 |

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
```

---

## 17. Logging / Metrics / Tracing

### 로그

- **레벨**: ERROR(장애) > WARN(이상) > INFO(흐름) > DEBUG(개발)
- 구조화 로그(JSON): 검색·집계 용이
- 민감정보(비밀번호, 세션) 로깅 금지

### 메트릭

- RPS, 응답 시간 p95, 에러율, DB 커넥션 풀
- 알림 기준 예: 에러율 5% 초과 5분 지속 → PagerDuty

### 트레이싱

- 분산 요청에 `traceId` 전파 (Gateway → Service → DB)
- 장애 시 어느 구간에서 지연/실패했는지 추적

---

## 18. Rollback / Graceful Shutdown

### 배포 실패 시 롤백

- **Blue-Green / Rolling**: 이전 버전 Pod/컨테이너 유지
- **DB**: Flyway는 되돌리기 어려움 → backward-compatible migration 설계
- **K8s**: `kubectl rollout undo deployment/briefly`

### Graceful Shutdown

```yaml
server:
  shutdown: graceful
spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s
```

1. 새 요청 거부
2. 진행 중 요청 완료 대기
3. DB 커넥션·스레드 풀 정리 후 종료

### 운영 장애 대응 흐름

```
알림 수신 → 메트릭/로그 확인 → Health Check
→ 최근 배포 여부 → 롤백 또는 스케일 아웃
→ 원인 분석 → 핫픽스 → CI/CD 재배포
```

---

## Briefly 스택 대응표

| 개념 | 현재 MVP | Spring 확장 시 |
| --- | --- | --- |
| Controller | `com.briefly.controller.*` | `@RestController` |
| Service | `*Service` | `@Service` + DI |
| Repository | `*Dao` (JDBC) | `JpaRepository` |
| Entity | `com.briefly.model.*` | `@Entity` |
| DTO | `com.briefly.dto.*` | record / class |
| Config | `db.properties` | `application.yml` + profile |
| Migration | `dB/schema.sql` | Flyway |
| Test | JUnit + Mockito | `@SpringBootTest` |
| Deploy | Tomcat WAR | Docker + K8s |

---

관련 문서: [SRS](./SRS.md) · [ERD/DB Schema](./ERD_DB_SCHEMA.md) · [SDD](./SDD.md)
