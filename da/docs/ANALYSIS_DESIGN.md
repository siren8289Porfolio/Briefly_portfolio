# Briefly Analysis Design

> Evidence: **DESIGNED**. 샘플 리포트·대시보드: NOT TESTED.

## 1. Funnel

```text
탐색 (product list/detail view)
  → 관심 (product_interest_add)
  → 모의가입 (mock_join_submit)
  → Brief 소비 (brief_view)
```

코드: `com.briefly.da.funnel.FunnelAnalyzer`

## 2. Context Effect (상관만)

- Segment A: `brief_view.has_market_context = true`
- Segment B: `context_unlinked`
- Compare: 체류 시간, interest_remove rate, detail revisit
- **면책**: "Context 연결 Brief가 전환율이 높다" ≠ Context가 원인

## 3. Market Volatility vs Interest Remove

- `fact_market_snapshot.change_rate` bucket vs `interest_remove` rate
- Spearman/Pearson 상관 (배치 job, PLANNED)
- 리포트 문구: "관련성이 관찰됨" / "인과 관계 아님"

## 4. Post-Alert Behavior

- Window: alert_view 후 7일
- Metrics: detail revisit, interest_remove, mock_join_submit
- Segment by signal_type

## 5. DE Coverage Monitoring

- Market Data Coverage, Source Freshness from DE DQ metrics
- Alert when freshness > policy threshold

## Privacy & Compliance

- user_id → SHA-256 + salt (`UserIdMasker`)
- 금융 데이터: 출처·면책 문구 필수, 투자 권유 금지
