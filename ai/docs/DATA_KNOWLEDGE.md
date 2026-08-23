# Data / Knowledge for Future AI

> Evidence: **DESIGNED**

## Allowed Sources

| Source | Use |
| --- | --- |
| FundReport (발행분) | Summary grounding / retrieval corpus |
| RiskAlert (발행분) | Historical pattern (not auto labels) |
| DE mart_disclosure / market_snapshot | Context + evidence with provenance |
| DA events (masked) | Recommendation features **only with consent** (AI-R3) |

## Forbidden

- 미검토 공시를 자동 risk **label**로 사용
- 실계좌·실주문 데이터
- Unmasked PII in prompts/logs

## Knowledge Contract

Every retrieval chunk must carry:

- `source_url` / `source_dataset`
- `reference_date` / `fetched_at`
- `document_id` / checksum when available
