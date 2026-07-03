# Briefly Data Efficiency Guide

데이터·DB·파이프라인 효율화를 정리한 학습/실무 참고 문서입니다.  
Briefly MVP 스키마(`back/dB/schema.sql`)를 기준으로 적용 예시를 포함합니다.

관련 문서: [ERD/DB Schema](./ERD_DB_SCHEMA.md) · [Engineering Guide](./ENGINEERING_GUIDE.md)

---

## 1. 효율화 지표 잡기

측정하지 않으면 개선 여부를 알 수 없다. **기준선(baseline)** 을 먼저 잡고, 변경 후 비교한다.

| 지표 | 의미 | Briefly 예시 |
| --- | --- | --- |
| **응답 시간** | API/화면 end-to-end 지연 | `/funds` 목록 p95 < 200ms |
| **쿼리 실행 시간** | DB 단일 쿼리 소요 | `EXPLAIN ANALYZE`로 ms 확인 |
| **읽은 데이터 양** | rows × row size | `SELECT *` vs 필요 컬럼만 |
| **CPU / 메모리** | 앱·DB 서버 리소스 | Tomcat heap, MySQL buffer pool |
| **스토리지 비용** | 테이블·인덱스·로그 용량 | TEXT `content` 보관 정책 |
| **배치 처리 시간** | 일/월 집계 job 소요 | 월별 신청 건수 summary 갱신 |
| **데이터 품질 오류 수** | 검증 실패 row 건수 | 중복 watchlist, amount ≤ 0 |

### 측정 방법 (MVP → 확장)

```
MVP:     로그에 query + elapsed ms 기록
확장:    Actuator metrics, DB slow query log, 대시보드
```

---

## 2. 데이터 모델 정리

### 테이블 책임 분리

| 테이블 | 단일 책임 |
| --- | --- |
| `users` | 인증·권한 |
| `funds` | 상품 마스터 |
| `watchlists` | 관심 관계만 |
| `fund_applications` | 신청 트랜잭션 |
| `fund_reports` | 운용 브리프 |
| `risk_alerts` | 위험 이벤트 |

**안 좋은 예**: `funds`에 신청 건수·최근 알림 문구까지 넣기 → 갱신 시 이상·락 증가.

### 중복 데이터 제거

- 사용자 이름을 `fund_applications`에 복사하지 않음 → `user_id` FK로 조회
- 상품명 중복 저장 대신 `fund_id`만 보관

### PK / FK 명확화

- PK: `BIGINT AUTO_INCREMENT` (Briefly 전 테이블)
- FK: `ON DELETE CASCADE`로 고아 row 방지
- UK: `watchlists(user_id, fund_id)` — 비즈니스 중복 방지

### 반복 컬럼 제거

- `created_at` / `updated_at`은 **변경 이력이 필요한 테이블**에만 (`funds`, `fund_applications`)
- 정적 마스터에 불필요한 `updated_at` 남발 지양

### 업무 단위별 Entity 분리

```
운영(OLTP): users, funds, applications  → 트랜잭션·무결성
분석(OLAP): fact_applications, dim_fund → 집계·조회 (후술 Star Schema)
```

---

## 3. 운영 DB 정규화

### 원칙

1. **한 테이블에 모든 데이터를 넣지 않기** — 관심·신청·리포트 분리 (Briefly 현재 구조 양호)
2. **중복 최소화** — 3NF 기준, 의도적 비정규화는 성능·리포트용으로만
3. **데이터 무결성** — FK, CHECK(`risk_grade` 1~5), ENUM status
4. **수정/삭제 이상 방지** — 상품은 `DELETE` 대신 `INACTIVE` (BR-008)

### 정규화 vs 성능

| 상황 | 선택 |
| --- | --- |
| 목록에 상품명 필요 | JOIN (정규화 유지) |
| 대시보드 초고속 조회 | Summary Table (의도적 비정규화) |

---

## 4. 분석 DB Star Schema

### 구조

```
         dim_user
              \
               → fact_applications ← dim_fund
              /                    \
         dim_date                  dim_status
```

| 유형 | 역할 | 예 |
| --- | --- | --- |
| **Fact Table** | 측정값(건수, 금액) | `fact_applications(amount, status, applied_at)` |
| **Dimension Table** | 분석 축 | `dim_fund`, `dim_date`, `dim_user` |

### 운영 DB와 분석 DB를 다르게 설계하는 이유

| 운영 DB (OLTP) | 분석 DB (OLAP) |
| --- | --- |
| INSERT/UPDATE 빈번 | SELECT·집계 위주 |
| 정규화·무결성 | 비정규화·Wide Table |
| 실시간 일관성 | 배치 지연 허용 |
| 짧은 트랜잭션 | 대량 스캔·집계 |

Briefly: 운영은 MySQL `briefly`, 분석은 별도 스키마 또는 BigQuery/Snowflake 데이터마트.

### 데이터마트 설계 (조회 성능 중심)

```sql
-- 예: 일별 신청 집계 마트
CREATE TABLE mart_daily_applications (
    report_date   DATE NOT NULL,
    fund_id       BIGINT NOT NULL,
    pending_cnt   INT NOT NULL,
    approved_cnt  INT NOT NULL,
    total_amount  DECIMAL(18,2) NOT NULL,
    PRIMARY KEY (report_date, fund_id)
);
```

---

## 5. 실행계획 확인

### EXPLAIN / EXPLAIN ANALYZE

```sql
-- MySQL 8.0.18+
EXPLAIN ANALYZE
SELECT id, name, risk_grade, expected_return
FROM funds
WHERE status = 'ACTIVE'
ORDER BY id DESC;
```

### 주요 항목 해석

| 항목 | 의미 |
| --- | --- |
| **Seq Scan (type=ALL)** | Full Table Scan — 인덱스 미사용 |
| **Index Scan (type=ref/range)** | 인덱스 활용 |
| **Join 순서** | 작은 결과 집합 먼저 조인되는지 |
| **Sort 비용** | `filesort` 발생 여부 |
| **rows** | 예상 읽기 row 수 |

### Briefly 점검 대상 쿼리

```sql
-- FR-005: ACTIVE 목록
EXPLAIN SELECT id, name, risk_grade, expected_return FROM funds WHERE status = 'ACTIVE';

-- FR-013: 관심상품 기준 알림
EXPLAIN
SELECT ra.* FROM risk_alerts ra
INNER JOIN watchlists w ON w.fund_id = ra.fund_id
WHERE w.user_id = 1
ORDER BY ra.created_at DESC;

-- FR-011: 내 신청 내역
EXPLAIN SELECT id, fund_id, amount, status, created_at
FROM fund_applications WHERE user_id = 1 ORDER BY created_at DESC;
```

---

## 6. SQL 최적화

| 기법 | Before | After |
| --- | --- | --- |
| SELECT * 제거 | `SELECT * FROM funds` | 필요 컬럼만 (목록: id, name, risk_grade) |
| WHERE 명확화 | 앱에서 필터 | `WHERE status = 'ACTIVE'` DB에서 |
| 불필요 JOIN 제거 | 3-way JOIN | 2-table로 충분한지 검토 |
| 중복 서브쿼리 | correlated subquery N번 | JOIN 또는 IN 한 번 |
| ORDER BY 최소화 | 불필요 정렬 | 인덱스와 정렬 순서 일치 |
| Pagination | 전체 로드 | `LIMIT ? OFFSET ?` 또는 keyset |

### Briefly: 상품 상세

```sql
-- description(TEXT)는 상세 화면에서만
SELECT id, name, description, risk_grade, expected_return
FROM funds WHERE id = ? AND status = 'ACTIVE';
```

### Briefly: 관심 토글 후

```
나쁜 예: toggle 후 전체 funds 목록 재조회
좋은 예: redirect to detail, 해당 fund만 갱신
```

---

## 7. 인덱스 설계

### 인덱스 대상 컬럼

| 패턴 | Briefly 컬럼 |
| --- | --- |
| WHERE 필터 | `funds.status`, `fund_applications.user_id` |
| JOIN | `watchlists.fund_id`, `risk_alerts.fund_id` |
| ORDER BY | `fund_reports.report_date DESC`, `risk_alerts.created_at DESC` |
| 날짜 범위 | `fund_reports.report_date`, `created_at` |
| 상태 필터 | `fund_applications.status` |

### 현재 인덱스 (`schema.sql`)

```sql
idx_funds_status (status)
idx_applications_user (user_id)
idx_applications_status (status)
idx_reports_fund (fund_id)
idx_alerts_fund (fund_id)
uk_watchlist_user_fund (user_id, fund_id)  -- Unique Index
```

### 추가 검토 (트래픽 증가 시)

```sql
-- 사용자별 최신 신청 목록
CREATE INDEX idx_applications_user_created
ON fund_applications(user_id, created_at DESC);

-- 상품별 브리프 최신순
CREATE INDEX idx_reports_fund_date
ON fund_reports(fund_id, report_date DESC);

-- 관심상품 알림: watchlists.user_id
CREATE INDEX idx_watchlists_user ON watchlists(user_id);
```

---

## 8. Partial Index / Composite Index

### Partial Index (MySQL 8.0.13+ Functional/조건부는 제한적)

PostgreSQL 예:

```sql
CREATE INDEX idx_applications_pending
ON fund_applications(user_id, fund_id)
WHERE status = 'PENDING';
```

MySQL 대안: **생성 컬럼 + 인덱스** 또는 composite에 `status` 포함.

### Composite Index

```sql
-- WHERE user_id = ? AND status = 'PENDING' 에 유리
CREATE INDEX idx_app_user_status ON fund_applications(user_id, status);
```

**왼쪽 prefix 규칙**: `(user_id, status)` 인덱스는 `user_id` 단독 조회에도 사용 가능.

### 트레이드오프

| 이점 | 비용 |
| --- | --- |
| 조회 가속 | INSERT/UPDATE 시 인덱스 갱신 |
| Sort 제거 | 디스크·메모리 추가 사용 |
| UK로 중복 방지 | 쓰기 경합 가능 |

**원칙**: 실제 EXPLAIN으로 확인한 느린 쿼리에만 추가. 인덱스 남발 금지.

---

## 9. 파티셔닝

대용량 테이블에서 **스캔 범위를 물리적으로 줄인다**.

```sql
-- 예: risk_alerts 월별 RANGE 파티션 (연간 수백만 건 이상 시)
CREATE TABLE risk_alerts (
    ...
    created_at DATETIME NOT NULL
) PARTITION BY RANGE (TO_DAYS(created_at)) (
    PARTITION p202601 VALUES LESS THAN (TO_DAYS('2026-02-01')),
    PARTITION p202602 VALUES LESS THAN (TO_DAYS('2026-03-01'))
);
```

| 단위 | 적합한 데이터 |
| --- | --- |
| 일별 | 초고빈도 이벤트 로그 |
| 월별 | 알림, 접속 로그 |
| 분기/연도 | 아카이브 리포트 |

Briefly MVP 규모에서는 **파티셔닝 불필요**. `fund_reports`, `risk_alerts` 증가 시 검토.

---

## 10. 클러스터링

- **파티션 안에서** 비슷한 값을 물리적으로 인접 저장
- BigQuery: **Partition** (날짜) + **Cluster** (fund_id, user_id)
- 효과: 조회 시 읽는 블록 수 감소 → **비용·시간 절감**

```
Partition: report_date = '2026-03'
Cluster:   fund_id
→ "3월 + 특정 펀드" 브리프 집계 시 스캔 최소화
```

운영 MySQL(InnoDB)는 PK 클러스터링 인덱스가 기본. 분석 DB에서 클러스터링 개념이 더 두드러짐.

---

## 11. Materialized View / Summary Table

### 언제 쓰는가

- **매번 같은 집계** (대시보드, 관리자 통계)
- JOIN·GROUP BY 비용이 큰 리포트

### Briefly 예시

```sql
CREATE TABLE summary_fund_stats (
    fund_id           BIGINT PRIMARY KEY,
    watchlist_count   INT NOT NULL DEFAULT 0,
    application_count INT NOT NULL DEFAULT 0,
    pending_count     INT NOT NULL DEFAULT 0,
    updated_at        DATETIME NOT NULL
);
```

| 갱신 방식 | 설명 |
| --- | --- |
| 배치 (일 1회) | Airflow job으로 전체 재계산 |
| 이벤트 (신청 시) | `application_count++` (트랜잭션 내) |
| 증분 | `updated_at > last_run` 만 반영 |

**Materialized View**: DB가 갱신 시점/방식 관리 (PostgreSQL, Oracle). MySQL은 Summary Table + job이 일반적.

---

## 12. 증분 처리 (Incremental Load)

| 방식 | 설명 | 비용 |
| --- | --- | --- |
| **Full Load** | 전체 테이블 덮어쓰기 | 단순, 데이터 커지면 비쌈 |
| **Incremental** | 변경분만 | `updated_at > :watermark` |

### Briefly → 분석 마트 동기화

```sql
-- 마지막 동기화 이후 변경된 신청만
SELECT * FROM fund_applications
WHERE updated_at > '2026-07-02 00:00:00';
```

- watermark 테이블: `etl_watermark(job_name, last_synced_at)`
- 전체 재처리 비용 ↓, 실패 시 watermark 롤백으로 재실행

---

## 13. CDC (Change Data Capture)

### 개념

DB의 **insert / update / delete 이벤트**를 캡처해 downstream(마트, 검색, 캐시)에 반영.

| 방식 | 설명 | 지연 |
| --- | --- | --- |
| **Polling** | `updated_at` 주기 조회 | 분~시간 |
| **CDC (binlog)** | MySQL binlog, Debezium | 초~분 (준실시간) |

```
MySQL binlog → Debezium → Kafka → Spark/Flink → mart_daily_applications
```

Briefly MVP: polling + `updated_at`으로 충분. 실시간 대시보드 필요 시 CDC 검토.

---

## 14. Spark / 분산 처리 튜닝

대용량 ETL·로그 분석 시 적용 (Briefly MVP 범위 밖, 확장 참고).

| 기법 | 목적 |
| --- | --- |
| **cache** | 반복 사용 DataFrame 메모리 저장 |
| **partition 조정** | `repartition` / `coalesce` — shuffle 균형 |
| **shuffle 최소화** | broadcast join (작은 dim 테이블) |
| **serialization** | Kryo 등 |
| **memory tuning** | executor memory, spill 방지 |
| **작은 파일 문제** | `coalesce` / `OPTIMIZE` (Delta/Iceberg) |

---

## 15. 데이터 품질 테스트

잘못된 데이터가 파이프라인·API·대시보드로 **빠르게 퍼지는 것**을 막는다.

| 검증 | Briefly 적용 |
| --- | --- |
| **unique** | `users.email`, `watchlists(user_id,fund_id)` |
| **not_null** | `fund_applications.amount` |
| **accepted_values** | `status IN (PENDING,...)` , `risk_grade 1~5` |
| **relationships** | `fund_id` → `funds.id` FK |
| **날짜 역전** | `report_date <= CURRENT_DATE` |
| **row count 급감/급증** | 일별 신청 건수 전일 대비 ±50% 알림 |

### 도구

- dbt tests, Great Expectations, Airflow task 내 SQL assert
- Service 레이어 검증 + DB 제약 **이중 방어**

---

## 16. Airflow DAG 오케스트레이션

```
extract (MySQL) → load (staging) → transform (mart) → test (품질) → publish (BI) → alert (실패 시)
```

### Briefly 예시 DAG (개념)

```python
# 의사 코드
extract_applications >> load_staging >> transform_daily_mart
transform_daily_mart >> test_row_counts >> publish_to_bi
[extract_applications, transform_daily_mart] >> alert_on_failure
```

| Task | 역할 |
| --- | --- |
| extract | 운영 DB에서 증분 추출 |
| load | staging 테이블 적재 |
| transform | summary / star schema 변환 |
| test | unique, not_null, row count |
| publish | Metabase/Superset 연동 |
| alert | Slack/PagerDuty |

---

## 17. 모니터링 / 비용 관리

### Job 메타데이터 (ETL 테이블 예시)

```sql
CREATE TABLE etl_job_runs (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_name            VARCHAR(100) NOT NULL,
    job_start_time      DATETIME NOT NULL,
    job_end_time        DATETIME,
    processed_row_count INT,
    failed_row_count    INT DEFAULT 0,
    query_runtime_ms    BIGINT,
    data_freshness      DATETIME,  -- 마트 기준 최신 데이터 시각
    error_message       TEXT,
    status              ENUM('RUNNING','SUCCESS','FAILED') NOT NULL
);
```

### API 레벨 (운영)

| 메트릭 | 용도 |
| --- | --- |
| `query_runtime` | 느린 쿼리 탐지 |
| `data_freshness` | 마트 지연 알림 |
| `failed_row_count` | 품질·적재 실패 |
| 요청당 쿼리 수 | N+1 회귀 방지 |

---

## 18. Briefly 프로젝트 적용 순서

SDD·ERD 이후 **성능·효율**을 단계적으로 적용하는 권장 순서입니다.

| 단계 | 작업 | 상태 |
| --- | --- | --- |
| 1 | DB 테이블 책임 분리 | ✅ users/funds/applications 등 분리 완료 |
| 2 | PK / FK / status / 날짜 컬럼 정리 | ✅ schema.sql 기준 |
| 3 | list API(화면) 쿼리 **EXPLAIN** 확인 | 🔲 `/funds`, `/applications`, `/alerts` |
| 4 | WHERE 조건에 맞는 **인덱스** 추가 | 🔲 composite 검토 (§7) |
| 5 | 저장 후 **전체 list 재조회** 제거 | 🔲 toggle/신청 후 redirect만 |
| 6 | 변경된 row만 **patch** (부분 갱신) | 🔲 SPA/API 확장 시 |
| 7 | 목록 조회 **pagination** | 🔲 `LIMIT/OFFSET` 또는 keyset |
| 8 | 이력 / **로그 테이블** 분리 | 🔲 `application_status_history` |
| 9 | 보고·통계 → **summary table** | 🔲 `summary_fund_stats` |
| 10 | 테스트로 **API 호출 수·쿼리 수** 검증 | 🔲 JUnit + 쿼리 카운터 |

### 단계 3 실행 예시

```sql
USE briefly;
EXPLAIN ANALYZE SELECT id, name, risk_grade, expected_return
FROM funds WHERE status = 'ACTIVE' ORDER BY id DESC;

EXPLAIN ANALYZE
SELECT id, fund_id, amount, status, created_at
FROM fund_applications WHERE user_id = 1 ORDER BY created_at DESC;
```

### 단계 8 이력 테이블 예시

```sql
CREATE TABLE application_status_history (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id  BIGINT NOT NULL,
    previous_status VARCHAR(20),
    new_status      VARCHAR(20) NOT NULL,
    changed_by      BIGINT,
    changed_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_history_application
        FOREIGN KEY (application_id) REFERENCES fund_applications(id)
);
```

운영 테이블은 현재 상태만, 이력은 append-only — 수정 이력·감사·분석에 유리.

### 단계 10 테스트 예시

```java
// 의사 코드: 요청 1회당 쿼리 1회 이하 목표
@Test
void listFunds_executesSingleQuery() {
    QueryCountHolder.clear();
    fundService.getActiveFunds();
    assertThat(QueryCountHolder.get()).isLessThanOrEqualTo(1);
}
```

---

## 체크리스트 요약

| 영역 | 핵심 질문 |
| --- | --- |
| 모델 | 테이블 책임이 하나인가? 중복 컬럼이 있는가? |
| 정규화 | FK·UK로 무결성을 DB가 지키는가? |
| 쿼리 | SELECT * 를 쓰지 않는가? N+1은 없는가? |
| 인덱스 | EXPLAIN에서 ALL(풀스캔)이 나오지 않는가? |
| 분석 | OLTP와 OLAP을 분리했는가? |
| 파이프라인 | 증분·CDC·품질 테스트가 있는가? |
| 운영 | job 시간·row 수·freshness를 기록하는가? |

---

관련 문서: [SRS](./SRS.md) · [SDD](./SDD.md) · [Engineering Guide](./ENGINEERING_GUIDE.md)
