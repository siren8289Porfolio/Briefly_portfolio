# Briefly Data Engineering

> **상태:** DESIGNED / PLANNED / NOT TESTED (실 API 호출·DQ 리포트·lineage sample 증거 전)
>
> 내부 OLTP(관심·모의가입·Brief)와 외부 금융 Context(시세·종목·공시)를 **분리된 레이어**로 수집·정규화·서빙한다.

## 원칙

| 원칙 | 내용 |
| --- | --- |
| Context only | 외부 데이터는 Brief·Risk **보조 Context/Evidence**. 자동 투자판단·위험 확정 label 금지 (금융소비자보호법) |
| Provenance | 모든 external record에 `source_name`, `source_url`, `base_date`/`reference_date`, `fetched_at`, `checksum` 보존 |
| Precision | 가격·금액은 `BigDecimal`(scale 명시). float 금지 |
| Idempotency | 시세는 `(security_id, base_date)` snapshot upsert |
| Graceful degradation | 외부 API 장애 시 내부 트랜잭션 중단 금지. 마지막 정상 Snapshot + freshness 표시 |

## 아키텍처

```text
Internal Write Path
User Session → API → Interest / MockJoin / Brief (상태 머신)
       ↓
   Audit / ReviewLog

External Path
금융위 OpenAPI / OPEN DART
  → Raw Snapshot (immutable)
  → Schema Validation → Quarantine(실패)
  → Normalize (ISIN, corp_code, base_date) → Dedup
  → DQ Gate → Serving Mart
  → Market/Disclosure Context API (Brief·Risk 보조)
```

## 디렉터리

```text
de/
├── README.md                 ← 본 문서
├── docs/
│   ├── DATA_CATALOG.md       ← 공공 데이터 카탈로그
│   ├── PIPELINE.md           ← 스테이지 정의
│   ├── DQ_GATES.md           ← 품질 게이트
│   └── LINEAGE.md            ← 계보·출처
├── sql/
│   ├── 01_raw.sql
│   ├── 02_quarantine.sql
│   ├── 03_staging.sql
│   ├── 04_mart.sql
│   └── 05_lineage.sql
└── src/
    ├── main/java/com/briefly/de/...
    └── test/java/com/briefly/de/...
```

## Evidence 기준

| 등급 | 조건 |
| --- | --- |
| **IMPLEMENTED** | pipeline 코드 + DQ 리포트 + lineage sample + 실제 API 호출 로그 |
| **DESIGNED** | 스키마·인터페이스·오케스트레이션 설계 완료 (현재) |
| **PLANNED** | 스케줄러(Airflow 등)·운영 알람 연동 |
| **NOT TESTED** | 실키 기반 수집·E2E DQ |

## 빌드

```bash
cd de && mvn test
```

## 공식 근거

- AWS Data Architecture / CAF
- Apache Airflow Best Practices
- dbt Data Tests
- 금융위원회 공공데이터 이용 안내
- OPEN DART 개발 가이드
- 금융소비자보호법 (투자권유·자동판단 제한)
