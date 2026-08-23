# Data Quality & Consistency Checks

> Evidence: **DESIGNED**

| Rule | Expected |
| --- | --- |
| Application status | PENDING → APPROVED \| REJECTED \| CANCELED only |
| Fund status | ACTIVE / INACTIVE; list = ACTIVE only |
| Watchlist | UNIQUE(user_id, fund_id) |
| Amount / return | BigDecimal; no float/double equality |
| Risk grade | integer 1~5 |
| Audit | status change → actor + timestamp + before/after |

## DE / DA Link

- External freshness / fallback: DE DQ gates
- Event idempotency (`event_id`): DA BR-TC-012
