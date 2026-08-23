# Briefly KPI Catalog

> Evidence: **DESIGNED** — formula·grain·owner 문서화. 대시보드·실데이터 리포트 없음 → NOT TESTED.

| KPI | Formula | Grain | Source | Segment | Owner |
| --- | --- | --- | --- | --- | --- |
| Interest → MockJoin Conversion | `mock_joins / interests` | day / product | Event + Transaction | product, risk_level | Product |
| Brief View Rate | `brief_views / active_sessions` | day | Event | brief_type | Product |
| Alert Conversion | `post_alert_actions / alerts_sent` | day | Event | signal_type | Ops / Product |
| Market Data Coverage | `products_with_price / active_products` | day | DE Mart | — | DE |
| Security Master Match Rate | `matched / mapping_targets` | day | DE | — | DE |
| Disclosure Coverage | `companies_with_disclosure / tracked` | week | DE | — | DE |
| Source Freshness | `analysis_ts - source_reference_date` | day | DE | — | DE |
| Brief Evidence Coverage | `briefs_with_external / total_briefs` | week | Brief + DE | — | Product |
| Risk Signal Review Rate | `reviewed / generated` | week | ReviewLog | — | Ops |

## Formula Versioning

| Field | Description |
| --- | --- |
| `metric_code` | KPI 식별자 (예: `interest_mockjoin_cvr`) |
| `formula_version` | semver (예: `1.0.0`) |
| `formula_text` | 사람이 읽는 수식 |
| `effective_from` | 적용 시작일 |

코드: `com.briefly.da.kpi.KpiDefinition`

## Null / Unknown

- Context 미연결 Brief → segment `context_unlinked`
- security_id 없음 → `security_unknown`
- 분모 0 → KPI `null` (0으로 대체하지 않음, 리포트에 `N/A` 표시)
