# AI Evaluation Metrics (planned)

> Evidence: **DESIGNED** · Live eval harness: **NOT IMPLEMENTED**

| Metric | Purpose | Tolerance |
| --- | --- | --- |
| Groundedness / Faithfulness | 공시·브리프 원문 근거 일치 | policy threshold |
| Task success | 요약 유용성, signal precision | vs human gold set |
| Latency | p50/p95 | AI-R4 budget |
| Cost | token / call | budget |
| Safety violation rate | 투자 권유·확정 표현 | **0** |

## Separation from DL predictive metrics

- Classification F1 (dl/) ≠ generative groundedness
- Recommendation CTR alone ≠ safe product advice

## Promotion Gate

AI feature flag may turn on only if:

1. HITL UI exists
2. Safety filter tests pass
3. Eval report attached to experiment/run id
4. Fallback path verified
