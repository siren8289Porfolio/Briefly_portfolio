# Briefly Generative / Assistive AI

> **상태:** Assistive explain (FastAPI) **IMPLEMENTED** · LLM draft/risk-candidate **NOT IMPLEMENTED**
>
> FastAPI는 상품/브리프/위험 **쉬운 설명**만 보조한다.  
> AI 추천·실제 투자판단 자동화·자동 위험 확정은 포함하지 않는다.

## Hard Constraints (Non-Negotiable)

| Constraint | Meaning |
| --- | --- |
| No auto risk finalization | 공시 발생 ≠ RiskAlert. Admin review 필수 |
| Draft/candidate only | LLM 출력은 초안·후보. 최종 발행은 관리자 |
| Provenance required | model/prompt/source/timestamp 기록 |
| Fallback always | FastAPI 장애 시 Servlet은 원본 상품/브리프/알림만 표시 |
| No investment advice | 금융소비자보호법 — 권유·확정 표현 금지 |

## v1 FastAPI (Servlet 연동)

```text
Browser → Servlet → Service / AiAssistService → AiClient (HTTP)
                                              ↓
                                    FastAPI AI Service
                                      /v1/explain/fund
                                      /v1/explain/brief
                                      /v1/explain/risk
```

- FastAPI는 제품 DB를 수정하지 않는다.
- `ai.assistive_explain.enabled` 기본 ON (template 설명).
- `ai.brief_draft` / `ai.risk_candidate` / 검색·추천 플래그는 기본 OFF.

## 디렉터리

```text
ai/
├── README.md
├── docs/           ← Scope, Requirements, Lifecycle, Eval, Monitoring
├── sql/            ← AI run / review audit
├── briefly_ai/     ← policy · safety · hitl · adapter · api (FastAPI)
└── tests/
```

## 실행

```bash
cd ai
python3 -m pip install -e ".[dev]"
PYTHONPATH=. uvicorn briefly_ai.api.app:app --host 127.0.0.1 --port 8000
PYTHONPATH=. python3 -m pytest -q
```

백엔드 `ai.properties`:

```text
ai.enabled=true
ai.baseUrl=http://127.0.0.1:8000
ai.timeoutMs=1500
```

## 공식 근거

- NIST AI Risk Management Framework
- Microsoft Azure Well-Architected AI
- AWS Generative AI Lens
- 금융소비자 보호에 관한 법률

연계: [`docs/DE.md`](../docs/DE.md) · [`docs/DL.md`](../docs/DL.md) · [`docs/DA.md`](../docs/DA.md) · [`docs/AI.md`](../docs/AI.md)
