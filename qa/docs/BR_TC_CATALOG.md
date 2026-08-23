# Core Test Cases (BR-TC)

> Evidence: **DESIGNED** · Automation: partial unit oracles only

| ID | Scenario | Precondition | Action | Expected | Priority |
| --- | --- | --- | --- | --- | --- |
| BR-TC-001 | 세션 고정 방지 | 로그인 전 세션 ID | 로그인 | 인증 후 새 세션 ID | P0 |
| BR-TC-002 | CSRF 차단 | token 없음 | POST /watchlist/toggle | 403, 데이터 불변 | P0 |
| BR-TC-003 | 관심상품 더블클릭 | 동일 user/fund 동시 | toggle | UNIQUE 1건, 상태 결정적 | P0 |
| BR-TC-004 | 비정상 신청 전이 | status=REJECTED | approve | 거부, audit 실패 사유 | P0 |
| BR-TC-005 | 타인 신청 취소 | 다른 user application | cancel | 403, 상태 불변 | P0 |
| BR-TC-006 | 관리자 URL 우회 | role=USER | /admin/funds POST | 403 | P0 |
| BR-TC-007 | XSS 브리프 | script 포함 입력 | 발행·조회 | 저장/출력 무해화 | P0 |
| BR-TC-008 | SQL injection | id=' OR 1=1 | 상세 조회 | 400/404, 추가 행 노출 없음 | P0 |
| BR-TC-009 | 금액 정밀도 | 소수·경계 금액 | 저장·표시 | BigDecimal 규칙, float 비교 금지 | P0 |
| BR-TC-010 | 미발행 브리프 | DRAFT version | 사용자 조회 | 목록 제외 | P1 |
| BR-TC-011 | AI 추천 경계 | 사용자 프로필 | 조회 | 개인화 추천/자동 승인 없음 | P0 |
| BR-TC-012 | 이벤트 재전송 | 동일 event_id | 수집 | 집계 1회 | P1 |
| BR-TC-013 | 위험등급 범위 | riskGrade=0 or 6 | 저장 | 422 | P0 |
| BR-TC-014 | Application PENDING 고정 | 생성 시 | POST /applications | status=PENDING only | P0 |
| BR-TC-015 | Fund INACTIVE 목록 제외 | status=INACTIVE | GET /funds | ACTIVE만 반환 | P0 |

## Domain Oracle Coverage (unit)

| Rule | Class | BR-TC |
| --- | --- | --- |
| Application transitions | `ApplicationStatusMachine` | 004, 014 |
| Risk grade 1~5 | `RiskGradeRules` | 013 |
| Money BigDecimal | `MoneyPrecisionRules` | 009 |
| Fund list ACTIVE only | `FundVisibilityRules` | 015 |
| AI boundary | `AiBoundaryRules` | 011 |
