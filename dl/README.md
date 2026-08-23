# Briefly ML/DL (Deep Learning / NLP)

> **상태:** DESIGNED / NOT IMPLEMENTED
>
> 텍스트 분류·표현 학습(predictive DL)과 생성형 요약(generative)을 **분리**하고,
> 학습·평가·서빙·MLOps 요구사항을 재현 가능한 계약으로 정의한다.

## Problem Scope

| Task | Boundary |
| --- | --- |
| Predictive | Brief/공시 텍스트 주제·중요도 분류, 사용자 반응 예측 **후보** |
| Representation | text embedding / sequence representation |
| Generative summary | **별도 AI evaluation 경계** — predictive metric과 분리 |
| Adoption rule | DL은 단순 NLP baseline 대비 **개선 근거** 있을 때만 채택 |

## 디렉터리

```text
dl/
├── README.md
├── docs/              ← 명세 (Problem, Data, Model, Train, Eval, Serving, RAI)
├── sql/               ← experiment registry DDL
├── pyproject.toml
├── briefly_dl/        ← Python scaffolding (DESIGNED)
└── tests/
```

## Evidence 기준

| 등급 | 조건 |
| --- | --- |
| **IMPLEMENTED** | training dataset + experiment log + baseline comparison + checkpoint + serving test + monitoring |
| **DESIGNED** | 스키마·계약·baseline·평가 로직 (현재) |
| **NOT IMPLEMENTED** | 실제 neural/Transformer 학습·배포 |

## 실행

```bash
cd dl && python -m pip install -e ".[dev]" && pytest
```

## 공식 근거

- Google ML Development Phases / Experiments
- Google Cloud MLOps CI/CD/CT
- PyTorch NLP Tutorials
- NIST AI Risk Management Framework
