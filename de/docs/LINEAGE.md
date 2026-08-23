# Briefly DE Lineage & Provenance

> Evidence: **DESIGNED** — 컬럼·테이블 정의. lineage sample / 운영 run 로그 없음 → NOT TESTED.

## External mart row (필수)

| Column | Description |
| --- | --- |
| `source_dataset` | 카탈로그 코드 |
| `source_url` | 수집 URL |
| `base_date` / `reference_date` | 출처 기준일 |
| `fetched_at` | 수집 시각 |
| `pipeline_run_id` | 파이프라인 실행 ID |
| `checksum` | raw payload hash |
| `schema_version` | raw/staging 스키마 버전 |
| `master_mapping_version` | ISIN/corp 매핑 사전 버전 |

## Internal audit (OLTP, 분리)

| Column | Description |
| --- | --- |
| `session_id` | 세션 (마스킹 가능) |
| `user_id` | 사용자 (PII 분리·마스킹) |
| `status` | 트랜잭션 상태 |
| `audit_log_id` | ReviewLog / 상태전이 감사 |

## Retention & Sensitivity

| Data | Policy |
| --- | --- |
| Raw 시세/공시 | 최소 1년 (감사·재처리) |
| 가격 | BigDecimal 정밀도 유지, float 변환 금지 |
| PII | 세션/사용자 데이터 분리, 마스킹 |
| 금융 Context | 출처 표기, 투자 권유 문구 금지 |

테이블: `de/sql/05_lineage.sql` → `pipeline_runs`, `lineage_edges`
