# Defect Severity & Regression

| Severity | 정의 | 예 |
| --- | --- | --- |
| Critical | 권한 우회, 개인정보 노출, data corruption, 핵심 기능 전면 장애 | Admin 우회, Session fixation |
| High | 핵심 journey 실패, 상태 전이 오류, 금액 정밀도 오류 | 모의가입 상태 오류 |
| Medium | 비핵심 기능, workaround 존재 | 정렬 오류 |
| Low | UI/문구 | 문구 오타 |

## Regression Policy

결함 수정 후 **관련 BR-TC + smoke suite** 재실행 필수.  
Evidence pack에 `regression_of` defect id 기록.
