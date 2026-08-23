# Briefly DE Pipeline Stages

> Evidence: **DESIGNED** — 스테이지·멱등 키·실패 정책 정의. 스케줄러 운영은 PLANNED.

## Stages

| # | Stage | Input | Output | Failure |
| --- | --- | --- | --- | --- |
| 1 | **Ingestion** | OpenAPI (pagination, auth key) | HTTP body + meta (retry/backoff, timeout, rate-limit) | retry then alert; 내부 OLTP 영향 없음 |
| 2 | **Raw Immutable** | response body | `raw_*` JSON + checksum + fetched_at + source_url | 저장 실패 시 run abort |
| 3 | **Validate** | raw JSON | pass rows / fail rows | fail → Quarantine + reason_code |
| 4 | **Quarantine** | invalid rows | `quarantine_*` | alert on schema drift spike |
| 5 | **Normalize** | valid rows | typed rows: security_id, base_date, BigDecimal | mapping miss → Context null |
| 6 | **Dedup** | normalized | upsert by business key | overwrite or skip (idempotent) |
| 7 | **DQ Gate** | staging | pass → mart / fail → block promote | threshold 미달 시 배포 차단 |
| 8 | **Serving** | mart | Market/Disclosure Context API | last-good snapshot + freshness banner |

## Idempotency

| Entity | Key | Strategy |
| --- | --- | --- |
| Market snapshot | `(security_id, base_date)` | upsert overwrite |
| Disclosure | `(corp_code, rcept_no)` | upsert skip-if-same-checksum |
| Security master | `isin` (fallback `srtn_cd`) | upsert |

## Orchestration (PLANNED)

```text
Daily (T+1 afternoon window)
  1. ingest FSC_KRX_LISTED
  2. ingest FSC_STOCK_PRICE
  3. ingest FSC_COMPANY_INFO (optional delta)
  4. validate → normalize → dq → mart

Near-real-time (OPENDART)
  poll list.json → raw → validate → mart fact_disclosure
  RiskSignal candidate 생성은 Admin Review 필수 (자동 Alert 금지)
```

코드 진입점: `com.briefly.de.pipeline.PipelineOrchestrator` (DESIGNED stub).

## Fallback

| Failure | Behavior |
| --- | --- |
| API outage | 마지막 정상 mart Snapshot 유지 + UI에 `시세 기준일` / `fetched_at` 표시 |
| Schema drift | quarantine + alert, mart promote 차단 |
| Master map miss | Context null, Interest/MockJoin 트랜잭션 정상 유지 |
