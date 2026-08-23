# AI Monitoring

> Evidence: **DESIGNED**

| Signal | Action |
| --- | --- |
| Input drift (공시/브리프 분포) | alert + optional flag off |
| Output distribution shift | review sample set |
| Refusal / fallback rate ↑ | check upstream DE / model health |
| Safety blocks | compliance review |
| Latency / cost SLO breach | degrade to non-AI |

## Retraining / Re-eval Trigger

- Label / review disagreement spike
- Groundedness drop on fixed eval set
- Policy change (prompt / retrieval corpus)

No continuous auto-deploy of generative models without HITL sample audit.
