# QA Strategy & Scope

> Evidence: **DESIGNED**

## Objectives

1. 일반 투자자 MVP journey가 SRS FR를 충족하는지 검증
2. Session·권한·입력 검증(보안) 회귀 방지
3. 상태 전이·UNIQUE·BigDecimal·risk grade 데이터 무결성
4. PRD → SRS → SDD → Test Case → Evidence 추적성

## Risk Priorities (High)

| Risk | Related BR-TC |
| --- | --- |
| Session fixation | BR-TC-001 |
| CSRF on state-changing POST | BR-TC-002 |
| AuthZ bypass / IDOR | BR-TC-005, BR-TC-006 |
| Illegal application transition | BR-TC-004, BR-TC-014 |
| Amount precision (float) | BR-TC-009 |
| XSS / SQLi | BR-TC-007, BR-TC-008 |

## Evidence Rule

| Status | Meaning |
| --- | --- |
| DESIGNED | 케이스·오라클·체크리스트 정의 |
| PLANNED | 자동화·환경 준비 중 |
| NOT TESTED | 실행 전 |
| PASSED | run_id + build_sha + artifact 존재 |
