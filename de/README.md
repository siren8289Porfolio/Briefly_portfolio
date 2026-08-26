# Briefly — 조회 패턴 기반 DB 최적화 및 데이터 처리 구조 개선

> **Servlet/JDBC 기반 서비스의 실제 조회 패턴을 분석해 단일·복합 인덱스를 설계하고, `SELECT *`를 필요한 컬럼 조회로 변경했으며, `EXPLAIN`으로 Table Scan → Index Scan 전환을 검증한 DB 최적화 프로젝트**

* **프로젝트 구분:** 개인 프로젝트
* **핵심 역할:** SQL/DAO 분석, Index 설계, Query Optimization, 실행계획 검증, 데이터 조회 구조 개선
* **기술 스택:** Java, Servlet/JSP, JDBC, PostgreSQL, H2, Maven, SQL
* **주요 영역:** DBA / Query Optimization / Database Design / Data Engineering 기초

---

# 1. 문제 상황 및 요구사항

## 1-1. 프로젝트 배경

Briefly는 Servlet, DAO, JSP 기반의 Java 웹 프로젝트입니다.

서비스에서는 펀드 상태 조회, 사용자별 신청 내역, 관심상품, 위험 알림, 리포트 등 여러 데이터를 반복적으로 조회합니다.

대표적인 흐름은 다음과 같습니다.

```text
사용자
 ↓
Servlet
 ↓
Service
 ↓
DAO
 ↓
SQL
 ↓
Database
```

초기 DB 접근 코드를 분석했을 때 두 가지 주요 문제가 있었습니다.

첫째, 실제 `WHERE` 조건에서 반복적으로 사용하는 컬럼에 적절한 인덱스가 없어 데이터가 증가할 경우 전체 테이블을 탐색할 가능성이 있었습니다.

둘째, 여러 DAO가 `SELECT *`를 사용하고 있어 화면이나 서비스에서 필요하지 않은 컬럼까지 함께 조회하고 있었습니다. 이 구조에서는 조회 데이터가 증가할수록 I/O가 불필요하게 커지고, SQL과 `mapRow()`의 컬럼 관계도 명확하지 않았습니다.

---

## 1-2. 주요 조회 요구사항

DAO의 실제 조회 코드를 기준으로 다음 패턴을 식별했습니다.

```text
활성 상품 조회
WHERE status = 'ACTIVE'

사용자별 특정 상품 신청 상태 조회
WHERE user_id = ?
  AND fund_id = ?
  AND status = ?

사용자의 관심 상품 조회
WHERE user_id = ?
  AND fund_id = ?

상품별 위험 알림 조회
WHERE fund_id = ?

상품별 리포트 조회
WHERE fund_id = ?
```

따라서 DBA 관점의 해결 목표를 다음과 같이 설정했습니다.

* 실제 `WHERE` 조건을 기준으로 인덱스를 설계한다.
* 여러 컬럼을 동시에 사용하는 조회에는 복합 인덱스를 적용한다.
* 모든 컬럼에 무작정 인덱스를 생성하지 않는다.
* `SELECT *`를 제거해 필요한 컬럼만 조회한다.
* SQL 변경 전후 실행계획을 `EXPLAIN`으로 비교한다.
* DB 최적화 이후에도 Java 코드가 정상 빌드되는지 검증한다.

---

# 2. 원인 분석

## 2-1. 전체 테이블 탐색 가능성

예를 들어 활성 펀드 조회는 다음 조건을 사용합니다.

```sql
SELECT ...
FROM funds
WHERE status = 'ACTIVE';
```

`status`에 인덱스가 없으면 DB Planner는 데이터 크기와 통계에 따라 Table Scan을 선택할 수 있습니다.

데이터가 작을 때는 큰 차이가 없어 보일 수 있지만, 데이터가 증가하면 조건에 맞는 Row를 찾기 위해 읽어야 하는 데이터 범위도 커질 수 있습니다.

---

## 2-2. 여러 조건을 사용하는 중복 신청 조회

신청 데이터에서는 다음 세 컬럼을 동시에 사용합니다.

```sql
WHERE user_id = ?
  AND fund_id = ?
  AND status = ?
```

이 조회는 다음 의미를 가집니다.

```text
특정 사용자가
+
특정 상품에
+
특정 상태로 신청했는가?
```

따라서 각각 별도의 단일 인덱스만 생성하는 것보다 실제 조회 조건을 하나로 묶은 복합 인덱스 후보가 적합하다고 판단했습니다.

---

## 2-3. `SELECT *`로 인한 과다 조회

기존 DAO에서는 테이블의 모든 컬럼을 조회하는 쿼리가 존재했습니다.

```sql
SELECT *
FROM funds
...
```

하지만 목록이나 일부 기능에서 실제 필요한 값은 제한적입니다.

```text
id
name
status
...
```

필요하지 않은 컬럼까지 항상 반환하면 다음 문제가 있습니다.

```text
불필요한 DB I/O
+
JDBC ResultSet 크기 증가
+
DAO 매핑 관계 불명확
+
Schema 변경 시 영향 범위 증가
```

따라서 Query와 Java Mapping 사이의 경계를 더 명확하게 만들 필요가 있었습니다.

---

## 2-4. 성능 개선 여부를 코드만 보고 판단할 수 없음

인덱스를 SQL에 추가했다고 해서 실제 DB가 반드시 그 인덱스를 사용하는 것은 아닙니다.

Planner는 데이터량, 조건 선택도, 통계 등을 보고 Table Scan이나 Index Scan 중 실행계획을 선택합니다.

따라서:

```text
CREATE INDEX 작성
→ 성능 개선 완료
```

라고 판단하지 않고,

```text
CREATE INDEX
→ EXPLAIN
→ 실제 Plan 확인
```

과정이 필요했습니다.

---

# 3. 문제 해결 및 적용 과정

## Step 1. 실제 조회 패턴 기준 인덱스 설계

DAO의 Query를 먼저 확인하고 다음 인덱스를 추가했습니다.

| 인덱스                                      | 조회 목적            |
| ---------------------------------------- | ---------------- |
| `idx_funds_status`                       | 활성/상태별 상품 조회     |
| `idx_fund_applications_user_fund_status` | 사용자·상품·상태별 신청 조회 |
| `idx_watchlists_user_fund`               | 사용자별 특정 관심상품 조회  |
| `idx_risk_alerts_fund_id`                | 상품별 위험 알림 조회     |
| `idx_fund_reports_fund_id`               | 상품별 리포트 조회       |

설계 원칙은:

```text
모든 Column
→ Index
```

가 아니라:

```text
WHERE / JOIN에서 실제 반복 사용
→ Index 후보 선정
```

이었습니다.

---

## Step 2. 복합 인덱스 적용

가장 대표적인 복합 조회는 신청 데이터입니다.

```sql
WHERE user_id = ?
  AND fund_id = ?
  AND status = ?
```

이를 기준으로:

```text
idx_fund_applications_user_fund_status
(user_id, fund_id, status)
```

를 구성했습니다.

조회 의도는 다음과 같이 명확합니다.

```text
user_id
    ↓
fund_id
    ↓
status
    ↓
신청 데이터 탐색
```

따라서 중복 신청 확인과 사용자별 신청 상태 조회의 접근 경로를 하나의 복합 인덱스로 맞췄습니다.

---

## Step 3. 사용자 관심상품 조회용 복합 인덱스

관심상품 조회 역시:

```sql
WHERE user_id = ?
  AND fund_id = ?
```

형태입니다.

따라서:

```text
idx_watchlists_user_fund
(user_id, fund_id)
```

를 적용했습니다.

이 인덱스는:

```text
사용자의 전체 Watchlist
+
특정 Fund 포함 여부
```

와 같이 사용자와 펀드를 함께 조건으로 사용하는 조회에 대응합니다.

---

## Step 4. Foreign Key 기반 반복 조회 인덱스

위험 알림과 리포트는 특정 Fund에 종속됩니다.

```text
Fund
 ├─ Risk Alert
 └─ Report
```

따라서 다음 인덱스를 적용했습니다.

```text
idx_risk_alerts_fund_id
idx_fund_reports_fund_id
```

특정 Fund의 하위 데이터를 조회할 때 전체 Alert/Report 테이블을 검색하지 않고 `fund_id`를 기준으로 접근할 수 있는 구조로 변경했습니다.

---

# 4. Query 자체 최적화

## Step 5. `SELECT *` 제거

`FundDao`, `ApplicationDao`, `WatchlistDao`, `ReportDao`, `AlertDao`의 Query에서 `SELECT *`를 제거했습니다.

### Before

```sql
SELECT *
FROM funds
WHERE status = ?;
```

### After

```text
DAO가 실제 사용하는 Column만 명시
```

각 DAO에는 `COLUMNS` 상수를 두어 Query와 Mapping 대상 컬럼을 명시적으로 관리하도록 변경했습니다.

구조적으로는:

```text
SELECT *
→ 모든 Column
→ ResultSet
→ 필요한 값만 Mapping
```

에서:

```text
필요 Column 명시
→ ResultSet
→ 동일 Column Mapping
```

으로 변경한 것입니다.

---

## Step 6. SQL ↔ `mapRow()` 관계 명확화

JDBC 기반 DAO에서는 Query 결과를 `ResultSet`으로 받아 객체로 변환합니다.

따라서 SQL의 반환 컬럼과 `mapRow()`가 기대하는 컬럼이 서로 맞아야 합니다.

명시적인 Column List를 사용하면서:

```text
SQL Column
 ↕
ResultSet Column
 ↕
mapRow()
 ↕
Domain Object
```

관계를 확인하기 쉽게 만들었습니다.

이 변경은 단순 성능뿐 아니라 Schema 변경 시 영향을 추적하기 쉬운 구조라는 장점도 있습니다.

---

# 5. 실행계획 검증

## Step 7. `EXPLAIN`으로 Index 적용 여부 확인

DB 최적화 후 실제 Planner가 인덱스를 사용하는지 확인했습니다.

H2 In-Memory DB 환경에서 `EXPLAIN`을 사용해 변경 전후를 비교했습니다.

### Before

```text
tableScan
```

### After

```text
IDX_FUND_APPLICATIONS_USER_FUND_STATUS 사용
```

검증 결과 주요 네 쿼리에서 Table Scan이 Index 기반 조회로 변경되는 것을 확인했습니다.

```text
fund_applications
watchlists
risk_alerts
funds
```

즉:

```text
Index가 SQL 파일에 존재한다
```

수준에서 끝나지 않고,

```text
DB Planner가 실제 Index를 선택한다
```

는 것까지 확인했습니다.

---

# 6. 검증 및 안정성 확인

## Step 8. 빌드 회귀 검증

DAO Query와 DB 초기화 코드를 수정한 뒤 Maven Compile도 수행했습니다.

```bash
mvn compile
```

변경 이후 프로젝트가 정상적으로 컴파일되는 것을 확인해 DB 변경이 기존 Java Build를 깨뜨리지 않는지 검증했습니다.

---

# 7. 해결 결과 및 성과

Briefly는 현재 README상 실제 수백만 건의 PostgreSQL Benchmark나 P95/P99 Latency 측정값은 제공하지 않습니다.

따라서 없는 성능 수치를 만들어내지 않고 **실행계획의 구조적 개선 결과**를 중심으로 설명합니다.

| 측정/검증 항목               | 개선 전        | 개선 후             | 효과          |
| ---------------------- | ----------- | ---------------- | ----------- |
| `fund_applications` 조회 | Table Scan  | 복합 Index 사용      | 검색 경로 개선    |
| `watchlists` 조회        | Table Scan  | Index Scan       | 검색 경로 개선    |
| `risk_alerts` 조회       | Table Scan  | Index Scan       | FK 기준 조회 개선 |
| `funds` 상태 조회          | Table Scan  | Index Scan       | 상태별 조회 개선   |
| DAO 컬럼 조회              | `SELECT *`  | 필요 컬럼만 명시        | 불필요 조회 제거   |
| Java 빌드                | 변경 영향 확인 필요 | `mvn compile` 통과 | 회귀 여부 검증    |

---

# 8. DBA 관점 핵심 성과

## ① Query Pattern Analysis

```text
DAO SQL 확인
→ WHERE 조건 추출
→ 반복 조회 Pattern 분류
```

## ② Index Design

```text
단일 조건
→ Single Column Index

복합 조건
→ Multicolumn Index

FK 기반 하위 조회
→ FK Column Index
```

## ③ Query Optimization

```text
SELECT *
→ Explicit Column Selection
```

## ④ Execution Plan Verification

```text
Table Scan
→ Index 적용
→ EXPLAIN
→ Index Scan 확인
```

즉 Briefly의 DBA 핵심은:

> **“인덱스를 추가했다”가 아니라 실제 DAO Query를 분석해 인덱스를 설계하고 실행계획으로 확인했다**

는 데 있습니다.

---

# 9. DE 관점에서 가져갈 수 있는 부분

Briefly는 pivotSeoul이나 AlloHub처럼 Airflow/Spark 중심의 DE 프로젝트는 아닙니다.

현재 Repository 구조에는 별도 `de/` 영역이 있지만, 프로젝트의 메인 README에서 가장 강하게 확인되는 구현은 **운영 데이터 조회 효율화와 명시적인 데이터 접근 경계**입니다.

따라서 DE 포트폴리오에서는 과장해서 “대규모 데이터 파이프라인 구축”이라고 표현하기보다 다음처럼 가져가는 것이 안전합니다.

```text
Operational DB
      ↓
Explicit SQL
      ↓
JDBC ResultSet
      ↓
DAO Mapping
      ↓
Application
```

여기서 개선한 것은:

```text
불필요한 Column 조회 제거
Query/Data Mapping 명확화
Index 기반 조회 경로 구축
DB 변경 후 Build 검증
```

입니다.

즉 **Data Access Optimization / Relational Data Processing 기초 프로젝트**로 포지셔닝하는 것이 맞습니다.

---

# 10. 문제 해결 흐름 요약

```text
[Problem]

반복 조회 Column에 Index 없음
+
SELECT * 사용
+
실제 Index 사용 여부 검증 없음

        ↓

[Root Cause]

DAO WHERE 조건과 DB Index 구조가 맞지 않음
+
Query가 필요한 Data Grain보다 넓음

        ↓

[Action]

DAO Query Pattern 분석
→ 단일 / 복합 Index 설계
→ SELECT * 제거
→ Explicit Column 조회
→ EXPLAIN 실행
→ Maven Build 검증

        ↓

[Result]

주요 Query
Table Scan → Index Scan

DAO
전체 Column → 필요 Column

DB 변경
→ Build 정상 유지
```

---

# 11. 현재 프로젝트에서 주장하지 않는 것

현재 Repository가 근거를 제공하지 않는 아래 내용은 포트폴리오 성과로 적지 않습니다.

* PostgreSQL 1,000만 건 Benchmark
* P95 / P99 Latency 몇 % 개선
* TPS 향상 수치
* CPU 사용률 개선
* Disk I/O 감소량 측정
* `EXPLAIN (ANALYZE, BUFFERS)` 기반 Buffer 측정
* PostgreSQL 운영 환경에서의 실측 성능
* `work_mem` 튜닝
* Lock / Deadlock 분석
* Isolation Level 튜닝
* Partitioning
* BRIN / Partial Index
* Materialized View
* HA / Failover
* RTO / RPO
* Backup / Restore 자동화
* PgBouncer
* AWS RDS / Aurora

특히 현재 실행계획 검증은 README 기준 **H2 In-Memory DB의 `EXPLAIN` 결과**이므로, PostgreSQL 운영환경에서 동일 성능이 측정되었다고 표현해서는 안 됩니다.

---

# 12. 회고 및 배운 점

첫째, Index는 DB에 많이 생성하는 것이 중요한 것이 아니라 **실제 Query 조건에서 어떤 컬럼 조합을 반복적으로 사용하는지 확인한 뒤 설계해야 한다는 점**을 배웠습니다.

특히:

```text
WHERE user_id
AND fund_id
AND status
```

처럼 여러 조건을 동시에 사용하는 Query에서는 단일 인덱스 여러 개보다 실제 접근 패턴을 반영한 복합 인덱스를 검토하는 것이 중요했습니다.

둘째, Query 성능 개선은 SQL을 수정하는 것만으로 끝낼 수 없다는 점을 확인했습니다. `EXPLAIN`을 통해 실제 Planner가 Table Scan 대신 Index를 선택하는지 검증해야 개선 근거를 만들 수 있었습니다.

셋째, `SELECT *`는 편리하지만 DAO가 실제 어떤 데이터를 필요로 하는지 불명확하게 만들 수 있다는 점을 배웠습니다. 필요한 Column을 명시하면서 DB I/O 범위를 줄이는 동시에 Query와 `mapRow()`의 계약도 명확하게 할 수 있었습니다.

넷째, DB 변경도 애플리케이션의 일부이기 때문에 Query나 초기화 SQL을 수정한 후에는 Java Build와 기존 기능에 영향을 주지 않는지 함께 확인해야 한다는 점을 경험했습니다.

---

# 13. 포트폴리오용 최종 설명

> **Briefly의 Servlet/JDBC 기반 백엔드에서 실제 DAO 조회 패턴을 분석해 PostgreSQL 인덱스 구조와 SQL을 개선했습니다. `funds.status`, `fund_applications(user_id, fund_id, status)`, `watchlists(user_id, fund_id)`, `risk_alerts.fund_id`, `fund_reports.fund_id` 등 실제 WHERE 조건을 기준으로 단일·복합 인덱스를 설계했습니다. 또한 `FundDao`, `ApplicationDao`, `WatchlistDao`, `ReportDao`, `AlertDao`의 `SELECT *`를 제거하고 필요한 컬럼을 명시해 불필요한 데이터 조회를 줄이고 SQL과 JDBC `mapRow()`의 매핑 관계를 명확히 했습니다. 변경 후 H2 In-Memory 환경에서 `EXPLAIN`을 이용해 주요 네 조회가 `tableScan`에서 Index 기반 접근으로 전환되는 것을 확인했고, `mvn compile`을 통과시켜 DB 및 DAO 변경 이후에도 기존 애플리케이션 Build가 정상적으로 유지되는 것을 검증했습니다.**

## 한 줄 성과

> **DAO Query Pattern을 기준으로 단일·복합 인덱스를 재설계하고 `SELECT *`를 제거했으며, `EXPLAIN`에서 주요 조회의 Table Scan → Index Scan 전환을 확인해 DB 조회 경로를 최적화했습니다.**
