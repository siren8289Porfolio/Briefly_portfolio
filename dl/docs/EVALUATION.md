# Evaluation Specification

> Evidence: **DESIGNED** (+ unit tests for metrics)

## Classification (Predictive)

- Precision, Recall, F1 (macro / per-class)
- Confusion matrix
- Segment/category error analysis
- **Baseline 대비 repeatable improvement** required for promotion

## Generative Summary (Separate Boundary)

| Metric | Purpose |
| --- | --- |
| Correctness | factual alignment with source |
| Relevance | answers user/info need |
| Grounding | cites source spans |

Predictive F1 **must not** be used as generative quality proxy.

## Regression Set

- Fixed `evaluation_set_id` + version
- CI: preprocessing parity + metric regression on golden set (PLANNED)
