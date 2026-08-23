# Briefly Data Analytics

> **상태:** DESIGNED / PLANNED / NOT TESTED (대시보드·실데이터 샘플 리포트 증거 전)
>
> 모의가입·관심상품·Brief 소비·위험 알림 흐름을 **재현 가능한 KPI·분석 계약**으로 정의한다.

## Business Questions

1. 관심상품 등록 → 모의가입 신청 전환율은?
2. Brief 조회 후 관심해제/상세 재방문 비율은?
3. 위험 알림 발생 후 사용자 행동 변화는?
4. 외부 시세/공시 Context가 연결된 Brief의 체류·전환은 다른가?
5. 시장 변동과 관심 변화의 상관관계는? (인과 단정 금지)

## 원칙

| 원칙 | 내용 |
| --- | --- |
| Correlation only | 시장 변동 vs 관심 해제 등 **상관 분석만**. 인과 단정 금지 |
| Context segment | Context 없는 경우 `미연결` 세그먼트로 명시 |
| Reproducibility | metric formula versioning, 동일 입력 → 동일 KPI |
| Privacy | user_id 해시/마스킹. 금융 Context는 투자 권유·자동 판단 금지 |
| DE link | 외부 시세/공시는 DE mart → fact_market_snapshot / fact_disclosure |

## 디렉터리

```text
da/
├── README.md
├── docs/
│   ├── KPI_CATALOG.md
│   ├── EVENT_TAXONOMY.md
│   ├── STAR_SCHEMA.md
│   └── ANALYSIS_DESIGN.md
├── sql/
│   ├── 01_raw_event.sql
│   ├── 02_dimensions.sql
│   ├── 03_facts.sql
│   └── 04_kpi_views.sql
└── src/
    ├── main/java/com/briefly/da/...
    └── test/java/com/briefly/da/...
```

## Evidence 기준

| 등급 | 조건 |
| --- | --- |
| **IMPLEMENTED** | KPI 대시보드 + 쿼리 + 샘플 리포트 + Context 조인 검증 |
| **DESIGNED** | KPI·이벤트·스타 스키마·계산 로직 (현재) |
| **PLANNED** | BI 대시보드, GA4 Measurement Protocol 연동 |
| **NOT TESTED** | 프로덕션 이벤트 스트림 E2E |

## 빌드

```bash
cd da && mvn test
```

## 공식 근거

- GA4 Measurement Protocol
- Power BI Star Schema Guidance
- 금융위원회 공공데이터 / OPEN DART 가이드
- 금융소비자보호법
