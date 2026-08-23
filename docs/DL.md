# Briefly ML/DL (요약)

상세 명세·baseline·실험 registry·Python 스캐폴딩은 [`dl/`](../dl/) 모듈을 본다.

| 항목 | 위치 | Evidence |
| --- | --- | --- |
| Problem / Data / Model | `dl/docs/` | DESIGNED |
| Experiment registry SQL | `dl/sql/01_experiment_registry.sql` | DESIGNED |
| L0 Keyword / L1 TF-IDF baseline | `dl/briefly_dl/models/` | DESIGNED (+ unit test) |
| Eval metrics (classification) | `dl/briefly_dl/eval/` | DESIGNED (+ unit test) |
| Serving fallback chain | `dl/briefly_dl/serve/` | DESIGNED (+ unit test) |
| Neural / Transformer training | — | NOT IMPLEMENTED |
| Generative summary eval | separate boundary | PLANNED |

원칙: predictive classification과 generative summary **분리**. DL은 baseline 대비 개선 근거 있을 때만 채택. PII 마스킹, split leakage 차단, version contract.

DE/DA 연계: Brief/공시 텍스트는 DE Context, 행동 라벨은 DA events (proxy label 명시).
