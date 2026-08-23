# Model Design

> Evidence: **DESIGNED** — baseline 코드 only. Neural/Transformer: NOT IMPLEMENTED.

## Tier Ladder

| Tier | Model | When |
| --- | --- | --- |
| L0 | Rule / keyword | Always available fallback |
| L1 | TF-IDF + linear (LogisticRegression) | Default ML baseline |
| L2 | Neural text classifier | L1 대비 val F1 ↑, latency OK |
| L3 | Transformer encoder | 충분한 labeled data + cost/latency 승인 |

## Version Contract

```text
artifact_manifest:
  model_version
  tokenizer_version
  preprocess_version
  label_schema_version
  training_dataset_hash
```

Serving must load **matching** tokenizer + preprocess + model triple.

## Generative (Separate)

- Summarization model artifact namespace: `generative/` (predictive와 분리)
- Eval metrics: correctness, relevance, grounding — **not** classification F1
