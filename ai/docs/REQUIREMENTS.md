# AI Requirements (if introduced)

> Evidence: **DESIGNED**

| ID | Requirement |
| --- | --- |
| AI-R1 | AI 출력은 “초안/후보”로만 표시, 최종 발행은 관리자 |
| AI-R2 | 모델·prompt·retrieval source·timestamp 기록 |
| AI-R3 | 개인 투자 성향 데이터 학습 시 명시적 동의 |
| AI-R4 | Latency / cost budget 정의 |
| AI-R5 | Safety: 투자 권유·확정 표현 차단, groundedness |
| AI-R6 | FastAPI assistive explain은 원문을 대체하지 않고 보조 설명만 제공 |
| AI-R7 | FastAPI 장애 시 Servlet 핵심 조회는 정상 동작 |

## Non-Functional Budgets (proposed)

| Metric | Proposed default | Owner |
| --- | --- | --- |
| p95 latency (draft summarize) | ≤ 5s | Platform |
| Cost / 1k briefs | TBD before enable | Ops |
| Safety violation rate | **0** (block + log) | Compliance |
| Fallback success | 100% non-AI path | Product |

## Feature Flags

- `ai.assistive_explain.enabled` — template FastAPI (default **true**)
- `ai.brief_draft.enabled`
- `ai.risk_candidate.enabled`
- `ai.nl_search.enabled`
- `ai.recommendation.enabled`

Default: generative flags **false**; assistive explain **true** in v1.
