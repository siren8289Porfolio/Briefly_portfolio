# Serving & Integration

> Evidence: **DESIGNED**

## Contract

- Input: `text`, max_length enforced, truncation policy documented
- Output: `label`, `confidence`, `model_version`, `uncertainty_note`
- Timeout + explicit error → fallback

## Fallback Chain

```text
DL classifier (if healthy)
  → TF-IDF baseline
  → keyword/rule baseline
  → explicit "model_unavailable" + human review flag
```

## Integration Points (Briefly)

- Brief ingest / admin review queue — **assistive only**
- RiskSignal: candidate generation evidence, **not** auto alert

## Failure Modes

- tokenizer/model version mismatch → reject + fallback
- input exceeds max_length → truncate with logged flag
- latency > SLA → degrade to L1/L0

코드: `briefly_dl.serve.fallback_router`
