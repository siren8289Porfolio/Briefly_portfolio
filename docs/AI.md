# Briefly Assistive AI (요약)

상세 계약·정책·FastAPI는 [`ai/`](../ai/) 모듈을 본다.

| 항목 | 위치 | Evidence |
| --- | --- | --- |
| Assistive explain (FastAPI) | `ai/briefly_ai/api/` | IMPLEMENTED |
| Servlet AIClient | `back/.../ai/` | IMPLEMENTED |
| Feature flags | `ai/briefly_ai/policy/` | DESIGNED (+ test) |
| Safety filter (투자 권유 0 tolerance) | `ai/briefly_ai/safety/` | DESIGNED (+ test) |
| HITL publish gate | `ai/briefly_ai/hitl/` | DESIGNED (+ test) |
| LLM draft / risk candidate / 추천 | — | NOT IMPLEMENTED |

원칙: FastAPI 장애 시 원본 조회 유지. AI 문구는 이해 보조만. 공시 ≠ 자동 RiskAlert.

연계: [`DE.md`](./DE.md) · [`DL.md`](./DL.md) · [`DA.md`](./DA.md)
