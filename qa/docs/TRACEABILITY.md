# Requirement Traceability

> Evidence: **DESIGNED** · 실행 매핑 run_id: **NOT TESTED**

| Baseline | Test Scope | Evidence |
| --- | --- | --- |
| PRD 목표·범위 | 핵심 journey / Out-of-scope 경계 | E2E video/log |
| SRS FR-001~009 | positive/negative/boundary/authz/state | case/run IDs |
| SRS NFR | security/reliability/session | scan/drill |
| SDD component | contract/integration/migration | build/schema |
| DE/DA | DQ/metric/external data freshness | dataset/reconciliation |
| POL-001~007 | Session 필수, 상태 전이, risk grade | state machine test |
| AI scope | 추천/자동 위험 없음 (BR-TC-011) | boundary assertion |

## FR → BR-TC (요약)

| SRS | BR-TC |
| --- | --- |
| FR-001 Auth / Session | BR-TC-001 |
| FR-002 Fund | BR-TC-015, BR-TC-013 |
| FR-003 Watchlist | BR-TC-002, BR-TC-003 |
| FR-004 Application | BR-TC-004, BR-TC-005, BR-TC-009, BR-TC-014 |
| FR-005~006 Brief/Alert | BR-TC-007, BR-TC-010 |
| FR-007~009 Admin | BR-TC-006, BR-TC-013 |
| Security NFR | BR-TC-001,002,006,007,008 |
| AI non-scope | BR-TC-011 |
| DA event idempotency | BR-TC-012 |

기계 가독 카탈로그: `com.briefly.qa.catalog.BrTcCatalog`
