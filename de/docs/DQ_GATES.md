# Briefly DE Data Quality Gates

> Evidence: **DESIGNED** — 메트릭·임계값·게이트 코드. 운영 DQ 리포트 없음 → NOT TESTED.

## Metrics

| Metric | Definition | Threshold |
| --- | --- | --- |
| Market Snapshot Completeness | 활성 상품 중 시세 연결 비율 | ≥ 95% |
| Security Master Match Rate | KRX 식별 성공 / 매핑 대상 | ≥ 98% |
| Disclosure Deduplication | `rcept_no` 중복 0 | 100% |
| Source Freshness | 분석시점 − `source_reference_date` | 정책 (영업일 이내) |
| Schema Pass Rate | 검증 통과 레코드 | ≥ 99% |
| Raw ↔ Normalized Reconciliation | count 차이 | ≤ 0.1% |

## Gate behavior

```text
DQGate.evaluate(metrics) → PASS | FAIL
FAIL → mart promote 금지, alert, last-good mart 유지
PASS → staging → mart swap / upsert
```

구현: `com.briefly.de.dq.DataQualityGate`

## Degradation

외부 데이터 장애·게이트 실패 시:

1. 모의가입 / 관심상품 / Brief **내부 트랜잭션은 중단하지 않음**
2. Serving API는 마지막 정상 Snapshot + freshness status 반환
3. Risk/Brief UI는 Context optional (null-safe)

## dbt-style tests (PLANNED)

| Test | Target |
| --- | --- |
| `unique` | `mart_market_snapshot(security_id, base_date)` |
| `not_null` | `price`, `base_date`, `fetched_at`, `source_dataset` |
| `accepted_values` | `currency` ∈ {KRW, USD, ...} |
| `relationships` | `security_id` → `dim_security` |
