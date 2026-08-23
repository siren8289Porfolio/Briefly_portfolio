# Briefly Assistive AI (요약)

상세 계약·정책·스캐폴딩은 [`ai/`](../ai/) 모듈을 본다.

| 항목 | 위치 | Evidence |
| --- | --- | --- |
| MVP AI 기능 | 없음 (추천·자동위험·LLM발행 금지) | by design |
| Scope / Requirements | `ai/docs/` | DESIGNED |
| Feature flags (default OFF) | `ai/briefly_ai/policy/` | DESIGNED (+ test) |
| Safety filter (투자 권유 0 tolerance) | `ai/briefly_ai/safety/` | DESIGNED (+ test) |
| HITL publish gate | `ai/briefly_ai/hitl/` | DESIGNED (+ test) |
| Adapter stubs + Non-AI fallback | `ai/briefly_ai/adapter/` | DESIGNED / NOT IMPLEMENTED |
| LLM / RAG / 추천 실서비스 | — | NOT IMPLEMENTED |

원칙: AI는 초안·후보만. 공시 ≠ 자동 RiskAlert. 출처·모델버전 기록. 장애 시 비-AI 경로.

연계: [`DE.md`](./DE.md) · [`DL.md`](./DL.md) · [`DA.md`](./DA.md)
