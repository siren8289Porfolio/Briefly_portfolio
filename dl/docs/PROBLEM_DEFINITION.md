# Problem Definition

> Evidence: **DESIGNED**

## Predictive Tasks

1. **Brief topic / importance classification** — 운용 브리프·공시 evidence 텍스트 라벨링 보조
2. **User reaction candidate** — 관심/해제/조회 후속 행동 **후보** (proxy label, 인과 단정 금지)

## Representation

- Sequence or pooled embedding for downstream DA segment / retrieval
- Versioned with `tokenizer_version` + `model_version`

## Generative Summary (Out of Predictive Boundary)

- Correctness / relevance / grounding 평가는 **별도 AI evaluation spec**
- 생성 요약은 **원문 사실을 확정적으로 대체하지 않음**
- Human review 시나리오: RiskSignal candidate, admin-facing brief digest

## Adoption Gate

DL 채택 전 필수:

1. Keyword baseline
2. TF-IDF + linear classifier baseline
3. Neural classifier — baseline 대비 **validation F1 개선** + latency budget 충족
4. Transformer — 데이터 규모·비용·latency 근거 문서화
