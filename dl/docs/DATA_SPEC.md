# Data Specification

> Evidence: **DESIGNED**

## Fields

| Field | Required | Notes |
| --- | --- | --- |
| `document_id` | Y | content canonical ID |
| `text` | Y | raw input |
| `text_version` | Y | content revision |
| `timestamp` | Y | collection time |
| `label` | train only | approved or proxy label |
| `label_source` | train | `approved` / `proxy` / `rule` |
| `split` | Y | train / validation / test |
| `tokenizer_version` | after preprocess | reproducibility |
| `preprocess_version` | after preprocess | parity tracking |

## Leakage Prevention

- 동일 `document_id` 또는 파생 문서(parent_id)가 train/test에 동시 존재 금지
- 시간 기반 split 옵션: `timestamp` cutoff
- 코드: `briefly_dl.data.split_validator`

## Privacy

- PII 패턴 마스킹 (`email`, `phone`, 주민등록번호 형식)
- 민감 텍스트는 학습 제외 또는 별도 bucket
- 코드: `briefly_dl.data.pii_filter`

## Label Policy

- Risk / investment **확정 label** 자동 생성 금지 (금융소비자보호법)
- Proxy label은 `label_source=proxy` 명시
