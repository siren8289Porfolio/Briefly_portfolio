# Responsible AI & Risk

> Evidence: **DESIGNED**

| Risk | Mitigation |
| --- | --- |
| Misclassification | confidence + uncertainty note in UI |
| PII in text | preprocess filter, training exclusion |
| Generative hallucination | never replace source; grounding eval separate |
| Investment advice | no auto risk grade / buy-sell label |
| Human review | admin RiskSignal, sensitive brief categories |

## NIST AI RMF Mapping (summary)

- **Map**: predictive vs generative boundary documented
- **Measure**: classification metrics + generative eval separation
- **Manage**: fallback, rollback, monitoring triggers
- **Govern**: label policy, PII, financial compliance

Human-in-the-loop required when: low confidence, generative summary, risk-related categories.
