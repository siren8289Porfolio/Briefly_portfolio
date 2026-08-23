# Briefly QA / QC (요약)

상세 전략·BR-TC·릴리스 게이트·도메인 오라클은 [`qa/`](../qa/) 모듈을 본다.

| 항목 | 위치 | Evidence |
| --- | --- | --- |
| Strategy / Levels / Entry-Exit | `qa/docs/` | DESIGNED |
| BR-TC-001~015 | `qa/docs/BR_TC_CATALOG.md` | DESIGNED |
| Traceability PRD→SRS→TC | `qa/docs/TRACEABILITY.md` | DESIGNED |
| OWASP WSTG 매핑 | `qa/docs/SECURITY.md` | DESIGNED |
| Release gate checklist | `qa/checklists/RELEASE_GATE.md` | DESIGNED |
| Domain oracles (status/money/risk/AI) | `qa/src/main/java/.../domain/` | DESIGNED (+ unit test) |
| Component / E2E / ZAP | — | PLANNED / NOT TESTED |

원칙: Risk-based. PASS는 run_id + artifact 필수. Critical/High open = 0 (또는 waiver).

연계: [`SRS.md`](./SRS.md) · [`SDD.md`](./SDD.md) · [`AI.md`](./AI.md) · [`DE.md`](./DE.md)
