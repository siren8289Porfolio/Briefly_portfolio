# Briefly Generative / Assistive AI

> **상태:** DESIGNED / PROPOSED / NOT IMPLEMENTED
>
> **MVP에는 AI 추천, 자동 위험 판정, LLM 브리프 생성이 없다.**  
> 외부 공시/시세는 DE Context/Evidence이며 자동 투자판단이 아니다.

## Hard Constraints (Non-Negotiable)

| Constraint | Meaning |
| --- | --- |
| No auto risk finalization | 공시 발생 ≠ RiskAlert. Admin review 필수 |
| Draft/candidate only | AI 출력은 초안·후보. 최종 발행은 관리자 |
| Provenance required | model/prompt/source/timestamp/confidence 기록 |
| Fallback always | AI 장애 시 규칙 기반 목록·수동 브리프 |
| No investment advice | 금융소비자보호법 — 권유·확정 표현 금지 |

## Future Use Cases (v2.0+)

1. 운용 브리프 초안 요약 (LLM, 관리자 검토 후 발행)
2. 공시 텍스트 Risk Signal **candidate** (classifier + rule)
3. 상품 설명 자연어 검색 (embedding retrieval)
4. 관심 패턴 추천 (explanation-only, 협업 필터링)

## 디렉터리

```text
ai/
├── README.md
├── docs/           ← Scope, Requirements, Lifecycle, Eval, Monitoring
├── sql/            ← AI run / review audit
├── briefly_ai/     ← Feature flag, safety, HITL, adapter contracts
└── tests/
```

## Evidence 기준

| 등급 | 조건 |
| --- | --- |
| **IMPLEMENTED** | 모델 학습/배포 + eval + HITL UI + monitoring 증거 |
| **DESIGNED** | 계약·정책·스캐폴딩 (현재) |
| **NOT IMPLEMENTED** | LLM/RAG/추천 실서비스 |

## 실행

```bash
cd ai && PYTHONPATH=. python3 -m pytest -q
```

## 공식 근거

- NIST AI Risk Management Framework
- Microsoft Azure Well-Architected AI
- AWS Generative AI Lens
- 금융소비자 보호에 관한 법률

연계: [`docs/DE.md`](../docs/DE.md) · [`docs/DL.md`](../docs/DL.md) · [`docs/DA.md`](../docs/DA.md)
