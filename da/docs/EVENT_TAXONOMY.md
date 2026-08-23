# Briefly Event Taxonomy (GA4-style)

> Evidence: **DESIGNED**. GA4 Measurement Protocol 연동: PLANNED / NOT TESTED.

| Event | Parameters | Trigger |
| --- | --- | --- |
| `product_interest_add` | product_id, security_id, source | 관심 등록 |
| `mock_join_submit` | product_id, amount, risk_ack | 모의가입 신청 |
| `brief_view` | brief_id, product_id, has_market_context | Brief 조회 |
| `risk_alert_view` | signal_id, product_id | 위험 알림 확인 |
| `interest_remove` | product_id, reason | 관심 해제 |
| `market_context_view` | security_id, base_date | 시세/공시 Context 노출 |

## Idempotency

- `event_id` UNIQUE (재전송 시 dedup)
- `event_ts` + `user_id_hash` + `event_name` + payload hash로 보조 검증 가능

## Pipeline

```text
Client / Servlet hook → raw_event → validated_event → fact_user_engagement
```

코드: `com.briefly.da.event.AnalyticsEventType`, `EventValidator`
