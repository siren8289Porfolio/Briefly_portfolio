# Briefly QA / QC

> **상태:** DESIGNED / PLANNED / NOT TESTED  
> PASS 증거(run_id + artifact) 전에는 **IMPLEMENTED/PASSED로 표기하지 않음**.

Servlet/JSP MVP(Auth · Fund · Watchlist · Application · Brief · RiskAlert · Admin)의  
요구사항 충족·보안·상태 전이·금액 정밀도·운영 복구를 검증한다.

## In / Out of Scope

| In Scope | Out of Scope (MVP) |
| --- | --- |
| FR Auth/Fund/Watchlist/Application/Report/Alert/Admin | 실주문·계좌·실명·결제 |
| Session / CSRF / XSS / SQLi / BigDecimal | AI 추천·복잡 차트 |
| Application 상태머신, UNIQUE watchlist, risk 1~5 | — |
| PRD→SRS→SDD→TC→Evidence 추적성 | — |

## 전략 (IEEE 29119 / ISTQB)

- **Risk-based**: Session fixation, CSRF, 권한 우회, 금액 정밀도, 불법 상태 전이 = High
- Traceability matrix 유지 (`docs/TRACEABILITY.md`)
- Evidence-required: PASS = `run_id` + artifact 필수

## 디렉터리

```text
qa/
├── README.md
├── docs/              ← Strategy, Levels, EntryExit, Traceability, Security, DQ
├── checklists/        ← Release gate
├── evidence/          ← Pack template (empty until runs)
├── pom.xml
└── src/               ← Domain oracles + BR-TC catalog + unit tests
```

## 실행

```bash
cd qa && mvn -q test
```

## 공식 근거

- IEEE 29119, ISTQB, OWASP WSTG/ASVS, Jakarta Servlet, NIST SSDF, 금융소비자보호법
