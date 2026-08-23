# AI Scope — MVP vs Future

> Evidence: **DESIGNED** · MVP AI features: **NOT IMPLEMENTED (by design)**

## MVP (Current Product)

| Feature | Status |
| --- | --- |
| AI product recommendation | **Out of scope** |
| Auto risk grade / RiskAlert from disclosure | **Forbidden** |
| LLM-generated FundReport publish | **Forbidden** |
| External market/disclosure | DE Context only |

## Future (v2.0+) — Assistive Only

| Use Case | AI Role | Human Gate |
| --- | --- | --- |
| Brief draft summary | LLM draft | Admin approve before publish |
| Risk Signal candidate | Classifier + rule | Admin review → RiskAlert |
| NL product search | Embedding retrieval | Ranking explanation shown |
| Interest-based recommend | CF / ranking | Explanation-only; no “buy” language |

## Lifecycle Framework

```text
Problem → Requirements → Data/Knowledge → System
  → RAG/Agent (optional) → Evaluation → QA/Deploy → Monitoring
```

AI는 **feature-flagged optional adapter**. Synchronous non-AI path always available.
