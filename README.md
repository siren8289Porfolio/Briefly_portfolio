# Briefly 

## 0. 저장소 구조 (3뎁스 개요)

전체 트리는 [`docs/STRUCTURE.md`](docs/STRUCTURE.md)를 본다.

```text
Briefly_portfolio/
├── docs/          # SRS · SDD · ERD · DE/DA/DB/DL/AI/QA 요약
├── back/          # Servlet/JSP 백엔드 (pom · src/main)
├── db/            # OLTP schema · seed
├── front/         # Vite/React (public · src)
├── de/            # Data Engineering (docs · sql · src)
├── da/            # Data Analytics (docs · sql · src)
├── dl/            # ML/DL NLP (docs · sql · briefly_dl · tests)
├── ai/            # Assistive AI MVP OFF (docs · sql · briefly_ai · tests)
└── qa/            # QA/QC (docs · checklists · evidence · src)
```

---

## 1. 핵심 한 줄

> **Briefly는 Servlet/JDBC 기반 백엔드에서 Service/Servlet의 책임을 분리하고, 자주 조회하는 DB 조건에 인덱스를 추가해 테스트 가능성·장애 추적성·조회 효율을 개선한 프로젝트입니다.**

---

## 2. 효율화 배경

Briefly는 Servlet, DAO, JSP 기반으로 구성된 Java 웹 프로젝트입니다.
초기 구조에서는 일부 클래스가 여러 책임을 함께 가지고 있었고, DB 조회도 자주 쓰는 조건에 인덱스가 없어 데이터가 많아질수록 전체 테이블을 훑을 가능성이 있었습니다.

이번 개선은 크게 두 가지 방향으로 진행했습니다.

```text
1. 코드 효율화
   → Service / Servlet을 테스트 가능한 구조로 변경
   → God Servlet 분리
   → 예외 로깅 추가

2. DB 효율화
   → 자주 쓰는 WHERE 조건에 인덱스 추가
   → SELECT * 제거
   → EXPLAIN으로 인덱스 적용 여부 확인
```

비전공자식으로 말하면,
기존에는 **한 사람이 주문, 요리, 계산, 재고관리를 전부 하고, 창고 물건도 처음부터 끝까지 뒤져 찾는 구조**였다면,
개선 후에는 **역할을 나누고, 자주 찾는 물건에는 위치표를 붙인 구조**입니다.

---

# Part 1. 백엔드 코드 효율화

## 3. Service에 생성자 주입 적용

### 기존 문제

예전 구조에서는 `FundService`가 내부에서 직접 `FundDao`를 만들었습니다.

```java
private final FundDao fundDao = new FundDao();
```

이렇게 하면 Service가 특정 DAO 구현에 강하게 묶입니다.
테스트할 때도 실제 DAO나 DB 연결이 따라오므로, DB 없이 Service 로직만 검증하기 어렵습니다.

비유하면, **요리사가 특정 냉장고 하나만 쓸 수 있게 고정된 상태**입니다.

---

### 개선 후

```java
public FundService() {
    this(new FundDao());
}

public FundService(FundDao fundDao) {
    this.fundDao = fundDao;
}
```

기본 생성자는 실제 Servlet 컨테이너 실행용이고, 두 번째 생성자는 테스트에서 `mockFundDao` 같은 가짜 객체를 넣기 위한 용도입니다.

Spring 공식문서에서도 Dependency Injection은 객체가 필요한 의존 객체를 생성자 인자, factory method 인자, property 등을 통해 외부에서 받는 방식이라고 설명합니다. Mockito는 Java 테스트에서 mock 객체를 만들고 검증할 수 있는 도구로, 공식 사이트에서 Maven/Gradle 설정과 문서를 제공합니다.

### 효율화 효과

```text
1. DB 없이 Service 로직 테스트 가능
2. DAO가 바뀌어도 Service 테스트 영향 감소
3. 객체 생성 책임 분리
4. 클래스 간 결합도 감소
```

쉽게 말하면,
**실제 냉장고를 열지 않고도 빈 냉장고 모형으로 요리 순서를 연습할 수 있게 만든 것**입니다.

---

## 4. Servlet도 테스트 가능한 구조로 변경

### 기존 문제

Servlet이 내부에서 직접 Service를 만들면, Servlet을 테스트할 때 실제 Service, DAO, DB까지 같이 따라옵니다.

즉, 단순히 “요청이 들어오면 어떤 화면으로 보내는지”만 보고 싶은데, 뒤에 DB까지 전부 준비해야 하는 상황이 됩니다.

---

### 개선 후

```java
public FundServlet() {
    this(new FundService(), new WatchlistService());
}

public FundServlet(FundService fundService, WatchlistService watchlistService) {
    this.fundService = fundService;
    this.watchlistService = watchlistService;
}
```

기본 생성자는 기존처럼 Servlet 컨테이너가 사용하고, 두 번째 생성자는 테스트에서 가짜 Service를 넣기 위해 사용합니다.

Jakarta Servlet 공식 튜토리얼은 Servlet이 `jakarta.servlet.Servlet` 인터페이스를 구현하는 Java 클래스이며, `init`, `service`, `destroy` 같은 생명주기 메서드를 가진다고 설명합니다. `@WebServlet` 같은 Servlet annotation은 Servlet, filter, listener 등을 annotation으로 선언하기 위한 패키지에 포함됩니다.

### 효율화 효과

```text
1. 기존 Servlet 컨테이너 실행 방식 유지
2. 테스트에서는 mock Service 주입 가능
3. Servlet 요청/응답 흐름만 따로 검증 가능
4. 실제 DB 준비 없이 컨트롤러 계층 테스트 가능
```

쉽게 말하면,
**실제 식당 전체를 열지 않고도 주문 접수대만 따로 테스트할 수 있게 만든 것**입니다.

---

## 5. 예외 삼킴 제거와 로깅 추가

### 기존 문제

예전에는 오류가 나도 에러 페이지로만 넘기고, 서버 로그에는 정확한 원인이 남지 않았습니다.

사용자 입장에서는 “오류가 났다”만 보이고, 개발자 입장에서도 “왜 오류가 났는지” 찾기 어려운 구조였습니다.

---

### 개선 후

```java
} catch (IllegalArgumentException e) {
    WebUtil.setError(req, e.getMessage());
    WebUtil.forward(req, resp, "error/not-found.jsp");
} catch (Exception e) {
    LOGGER.log(Level.SEVERE, "상품 조회 실패: " + req.getRequestURI(), e);
    WebUtil.setError(req, "상품 조회 중 오류가 발생했습니다.");
    WebUtil.forward(req, resp, "error/error.jsp");
}
```

예상 가능한 오류와 예상 못한 오류를 분리했습니다.

```text
IllegalArgumentException
→ 상품 없음, 잘못된 요청처럼 안내 가능한 오류

Exception
→ 예상 못한 시스템 오류
→ LOGGER.log(Level.SEVERE, ..., e)로 기록
```

Java `ResultSet`과 JDBC처럼 백엔드 코드가 DB 결과를 다루는 과정에서는 예외가 발생할 수 있으므로, 사용자 안내와 개발자용 로그를 분리하는 것이 중요합니다. Java Logging의 `Level`은 로그 출력 수준을 구분하는 기준으로 사용됩니다.

### 효율화 효과

```text
1. 사용자에게는 이해하기 쉬운 오류 화면 제공
2. 개발자에게는 서버 로그로 실제 원인 제공
3. 장애 발생 시 원인 추적 시간 감소
4. 예상 가능한 예외와 시스템 예외 분리
```

쉽게 말하면,
**손님에게는 “주문 처리 중 문제가 생겼습니다”라고 안내하고, 주방 기록지에는 “어떤 재료에서 문제가 났는지” 자세히 남긴 것**입니다.

---

## 6. God Servlet 분리

### 기존 문제

기존 `AdminServlet` 하나가 관리자 기능 여러 개를 모두 처리했습니다.

```text
/admin/funds
/admin/reports
/admin/alerts
/admin/applications/status
```

하나의 Servlet이 상품, 브리프, 알림, 신청 상태를 모두 관리하면 파일이 점점 커집니다.
이런 구조는 흔히 “God Object” 또는 “God Class”처럼, 한 객체가 너무 많은 책임을 가진 상태로 설명할 수 있습니다.

비유하면, **한 명의 직원이 상품 등록, 보고서 작성, 알림 발송, 신청 상태 변경을 전부 처리하는 상황**입니다.

---

### 개선 후

기능별로 Servlet을 나눴습니다.

```text
AdminFundServlet
AdminReportServlet
AdminAlertServlet
AdminApplicationServlet
```

예를 들어 `AdminFundServlet`은 관리자 상품 등록/수정만 담당합니다.

```java
@WebServlet("/admin/funds")
public class AdminFundServlet extends HttpServlet {
    private final FundService fundService;
}
```

### 효율화 효과

```text
1. 상품 기능 수정 → AdminFundServlet 중심으로 확인
2. 보고서 기능 수정 → AdminReportServlet 중심으로 확인
3. 알림 기능 수정 → AdminAlertServlet 중심으로 확인
4. 신청 상태 수정 → AdminApplicationServlet 중심으로 확인
5. 한 기능 수정이 다른 기능에 주는 영향 감소
```

쉽게 말하면,
**한 명이 모든 일을 하던 구조에서 업무 담당자를 나눈 것**입니다.

---

# Part 2. DB 효율화

## 7. 인덱스 추가

### 기존 문제

기존에는 `WHERE` 조건에서 자주 쓰는 컬럼에 인덱스가 없었습니다.

예를 들면 이런 조회입니다.

```sql
SELECT ...
FROM funds
WHERE status = 'ACTIVE';
```

```sql
SELECT ...
FROM fund_applications
WHERE user_id = ?
  AND fund_id = ?
  AND status = ?;
```

인덱스가 없으면 DB는 조건에 맞는 데이터를 찾기 위해 테이블을 처음부터 끝까지 확인해야 합니다.

비유하면, **전화번호부에서 이름 색인 없이 한 줄씩 전부 읽는 것**과 같습니다.

PostgreSQL 공식문서는 인덱스가 DB 서버가 특정 row를 더 빠르게 찾고 가져오도록 돕지만, DB 전체에는 overhead도 추가되므로 신중하게 사용해야 한다고 설명합니다.

---

### 개선 후

실제 조회 패턴에 맞춰 인덱스를 추가했습니다.

| 인덱스                                      | 쉽게 말하면             | 커버하는 조회              |
| ---------------------------------------- | ------------------ | -------------------- |
| `idx_funds_status`                       | 상품 상태별 색인          | 활성 상품 조회             |
| `idx_fund_applications_user_fund_status` | 사용자 + 상품 + 신청상태 색인 | 중복 신청 확인, 사용자별 신청 조회 |
| `idx_watchlists_user_fund`               | 사용자 + 상품 찜 목록 색인   | 특정 사용자의 관심 상품 조회     |
| `idx_risk_alerts_fund_id`                | 상품별 위험 알림 색인       | 여러 상품의 알림 조회         |
| `idx_fund_reports_fund_id`               | 상품별 리포트 색인         | 특정 상품 리포트 조회         |

PostgreSQL `CREATE INDEX` 공식문서는 인덱스 생성 문법을 제공하며, `CREATE INDEX IF NOT EXISTS`는 같은 이름의 인덱스가 이미 있을 때 중복 생성 오류를 피하는 데 사용할 수 있습니다.

### 효율화 효과

```text
Before
→ DB가 전체 테이블을 훑어 조건에 맞는 행을 찾음

After
→ 조건에 맞는 데이터 위치를 인덱스로 먼저 찾음
```

쉽게 말하면,
**창고 전체 박스를 다 열어보는 방식에서, 박스 위치표를 보고 바로 찾아가는 방식으로 바꾼 것**입니다.

---

## 8. 복합 인덱스 적용

### 기존 문제

일부 조회는 조건이 하나가 아니라 여러 개였습니다.

예를 들면 신청 중복 확인은 아래 조건을 같이 봅니다.

```sql
WHERE user_id = ?
  AND fund_id = ?
  AND status = ?;
```

이런 경우에는 `user_id`, `fund_id`, `status`를 따로따로 보는 것보다, 실제 조회 순서에 맞춰 묶은 인덱스를 두는 것이 더 적합합니다.

---

### 개선 후

```sql
idx_fund_applications_user_fund_status
(user_id, fund_id, status)
```

```sql
idx_watchlists_user_fund
(user_id, fund_id)
```

PostgreSQL 공식문서는 하나의 테이블에서 여러 컬럼을 묶은 multicolumn index를 정의할 수 있다고 설명합니다.

### 효율화 효과

```text
1. user_id + fund_id + status를 같이 보는 조회 최적화
2. user_id만 사용하는 조회에도 일부 활용 가능
3. 실제 DAO 조회 패턴과 인덱스 구조 일치
```

쉽게 말하면,
**“사용자별 → 상품별 → 상태별”로 정리된 파일함을 만든 것**입니다.

---

## 9. `SELECT *` 제거

### 기존 문제

기존 DAO들은 대부분 `SELECT *`를 사용했습니다.

```sql
SELECT *
FROM funds;
```

`SELECT *`는 테이블의 모든 컬럼을 가져옵니다.
그런데 실제 `mapRow()`에서는 필요한 컬럼만 읽고 있었습니다.

즉, 화면이나 로직에서 쓰지 않는 컬럼까지 DB에서 가져오는 구조였습니다.

비유하면, **이름과 전화번호만 필요하지만 주민등록등본 전체를 복사해 오는 것**과 같습니다.

---

### 개선 후

각 DAO에 `COLUMNS` 상수를 두고 필요한 컬럼을 명시했습니다.

```java
private static final String COLUMNS = """
    fund_id,
    title,
    status,
    expected_return,
    risk_level
""";
```

```sql
SELECT fund_id, title, status, expected_return, risk_level
FROM funds;
```

Oracle JDBC 문서는 `ResultSet`이 SQL 쿼리 결과를 담는 Java 객체이며, 현재 row의 각 column 값을 `get` 메서드로 읽을 수 있다고 설명합니다. 따라서 DAO에서 어떤 컬럼을 읽는지 SQL에 명시하면, `mapRow()`와 SQL의 관계가 더 분명해집니다.

### 효율화 효과

```text
1. 필요한 데이터만 조회
2. 테이블에 컬럼이 추가되어도 DAO 영향 감소
3. SQL에서 어떤 컬럼을 쓰는지 명확함
4. mapRow()와 SELECT 컬럼 관계가 분명해짐
```

쉽게 말하면,
**필요한 서류만 요청하는 방식으로 바꾼 것**입니다.

---

## 10. EXPLAIN으로 검증

### 기존 문제

DB 효율화는 “느낌상 빨라졌다”로 끝내면 안 됩니다.
실제로 DB가 어떤 방식으로 데이터를 찾는지 확인해야 합니다.

---

### 개선 후

H2 in-memory DB에서 `EXPLAIN`으로 인덱스 적용 전후를 비교했습니다.

```text
적용 전: tableScan
적용 후: IDX_FUND_APPLICATIONS_USER_FUND_STATUS 사용
```

PostgreSQL 공식문서도 `EXPLAIN`을 사용하면 planner가 어떤 query plan을 만드는지 확인할 수 있다고 설명합니다.

검증 결과 아래 4개 쿼리에서 table scan이 인덱스 스캔으로 바뀌는 것을 확인했습니다.

```text
fund_applications
watchlists
risk_alerts
funds
```

즉, DB가 더 이상 무조건 전체 테이블을 훑는 것이 아니라, 조건에 맞는 인덱스를 먼저 보고 데이터를 찾게 된 것입니다.

쉽게 말하면,
**진짜로 색인표를 보고 찾는지 확인한 것**입니다.

---

## 11. `mvn compile`로 빌드 검증

DB 쿼리와 초기화 코드를 수정한 뒤 `mvn compile`도 통과했습니다.

Maven 공식문서는 build lifecycle이 여러 phase로 구성되며, 각 phase가 lifecycle의 특정 단계를 나타낸다고 설명합니다. 즉, 이번 변경 이후 Java 코드가 정상적으로 컴파일되는지 확인한 것입니다.

쉽게 말하면,
**DB 성능 개선 후에도 프로젝트가 정상적으로 빌드되는지 확인한 것**입니다.

---

# Part 3. 최종 정리

## 12. 최종적으로 줄어든 비용

이번 코드·DB 효율화로 줄어든 비용은 아래와 같습니다.

```text
1. 테스트 준비 비용 감소
2. DB 의존성 감소
3. 기능 수정 범위 감소
4. 오류 추적 시간 감소
5. 코드 이해 비용 감소
6. 신규 기능 추가 시 사이드 이펙트 감소
7. 조회 시 전체 테이블을 훑는 비용 감소
8. 자주 쓰는 조건 검색 속도 개선
9. 불필요한 컬럼 조회 감소
10. SQL과 mapRow()의 컬럼 관계 명확화
11. EXPLAIN 기반 검증으로 성능 개선 근거 확보
```

---

## 13. 포트폴리오용 설명

Briefly 백엔드는 Servlet 기반 구조에서 Service와 DAO가 강하게 묶여 있어 단위 테스트가 어렵고, 관리자 기능이 하나의 Servlet에 집중되어 수정 범위가 넓은 문제가 있었습니다.

이를 개선하기 위해 Service와 Servlet에 생성자 주입 패턴을 적용해 테스트 시 mock 객체를 주입할 수 있도록 만들었습니다. 기본 생성자는 기존 Servlet 컨테이너 실행 흐름을 유지하고, 추가 생성자는 테스트에서 DB 없이 로직을 검증하기 위한 용도로 분리했습니다.

또한 예상 가능한 비즈니스 예외와 예상하지 못한 시스템 예외를 구분하고, 시스템 예외는 `LOGGER.log(Level.SEVERE, ..., e)`로 기록해 장애 추적성을 높였습니다. 관리자 기능은 기존 `AdminServlet` 하나에서 처리하던 구조를 `AdminFundServlet`, `AdminReportServlet`, `AdminAlertServlet`, `AdminApplicationServlet`로 나누어 리소스별 책임을 분리했습니다.

DB 측면에서는 `WHERE` 조건에서 자주 사용되는 컬럼에 인덱스가 없어 데이터가 많아질수록 조회 시 전체 테이블을 훑을 수 있는 구조를 개선했습니다. 실제 DAO 조회 패턴을 기준으로 `funds.status`, `fund_applications(user_id, fund_id, status)`, `watchlists(user_id, fund_id)`, `risk_alerts.fund_id`, `fund_reports.fund_id`에 인덱스를 추가했습니다.

또한 `FundDao`, `ApplicationDao`, `WatchlistDao`, `ReportDao`, `AlertDao`에서 사용하던 `SELECT *`를 제거하고, 각 DAO에 `COLUMNS` 상수를 두어 필요한 컬럼만 명시적으로 조회하도록 변경했습니다. 이를 통해 불필요한 컬럼 전송을 줄이고, SQL과 `mapRow()` 간의 매핑 관계를 더 명확하게 만들었습니다.

변경 후 H2 in-memory DB에서 `EXPLAIN`으로 인덱스 적용 전후를 비교했고, 주요 조회 쿼리가 `tableScan`에서 인덱스 스캔으로 전환되는 것을 확인했습니다. 마지막으로 `mvn compile`을 통과시켜 DB 초기화 코드와 DAO 변경이 Java 빌드에 영향을 주지 않는 것도 검증했습니다.

결과적으로 Briefly는 DB 없이 Service/Servlet 단위 테스트가 가능해졌고, 기능별 수정 범위가 줄었으며, 오류 발생 시 원인을 로그로 추적할 수 있게 되었습니다. 동시에 DB는 자주 쓰는 조회 조건에 맞춰 데이터를 더 빠르게 찾을 수 있고, DAO는 필요한 컬럼만 명확히 조회하는 유지보수 친화적인 구조로 개선되었습니다.

---

## 14. 한 줄 설명

> **Briefly는 Servlet/DAO 직접 생성 구조를 생성자 주입으로 바꿔 테스트 가능성을 높이고, God Servlet을 기능별 Servlet로 분리했으며, 자주 쓰는 조회 조건에 인덱스와 명시적 컬럼 조회를 적용해 코드·DB 유지보수 비용을 줄인 프로젝트입니다.**

---

## 15. README 카드용 문장

> **Service/Servlet에 생성자 주입을 적용해 mock 기반 단위 테스트가 가능하도록 개선하고, 관리자 God Servlet을 리소스별 Servlet로 분리했습니다. 또한 DAO 조회 패턴에 맞춰 FK·상태 컬럼 인덱스를 추가하고 `SELECT *`를 명시적 컬럼 조회로 바꿔 DB 조회 경로와 유지보수성을 개선했습니다.**

---

## 16. 공식문서 참고

| 주제                           | 공식문서                                    |
| ---------------------------- | --------------------------------------- |
| 생성자 주입 / DI                  | Spring Dependency Injection             |
| Servlet 기본 개념                | Jakarta Servlet Tutorial                |
| Servlet Annotation           | Jakarta Servlet API Annotation Overview |
| Mock 테스트                     | Mockito 공식 사이트                          |
| JUnit 테스트                    | JUnit 공식 사이트                            |
| PostgreSQL Index             | PostgreSQL Indexes                      |
| PostgreSQL CREATE INDEX      | PostgreSQL CREATE INDEX                 |
| PostgreSQL Multicolumn Index | PostgreSQL Multicolumn Indexes          |
| JDBC ResultSet               | Oracle JDBC ResultSet                   |
| Maven Build Lifecycle        | Apache Maven Build Lifecycle            |
