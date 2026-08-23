# Briefly Analytics Star Schema

> Evidence: **DESIGNED** — DDL in `da/sql/`. Context 조인 검증: NOT TESTED.

## Dimensions

| Table | Keys / Notes |
| --- | --- |
| `dim_date` | date_key, year, month, week, is_business_day |
| `dim_product` | product_id, name, risk_level, status |
| `dim_security` | security_id, isin, srtn_cd (DE dim_security join) |
| `dim_company` | corp_code, corp_name (DE dim_company join) |
| `dim_user` | user_id_hash (masked), segment |
| `dim_status` | application / signal status codes |

## Facts

| Table | Grain | Source |
| --- | --- | --- |
| `fact_user_engagement` | event × user × day | raw_event validated |
| `fact_market_snapshot` | security_id × base_date | DE mart_market_snapshot |
| `fact_disclosure_event` | corp_code × rcept_no | DE mart_disclosure |
| `fact_risk_signal` | signal_id × day | ReviewLog + risk_signal_candidates |

## Join Rules

- Brief Context 분석: `fact_user_engagement` LEFT JOIN `fact_market_snapshot` ON security_id + base_date
- **인과 단정 금지**: join은 segment 비교용 only
- 모든 external fact row에 `source_reference_date`, `fetched_at`, `source_url`
