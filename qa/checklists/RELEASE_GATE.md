# Release Gate Checklist

> Fill on each release candidate. Evidence paths under `qa/evidence/<run_id>/`.

**build_sha:** ____________  
**run_id:** ____________  
**tester:** ____________  
**date:** ____________

## Coverage

- [ ] P0/P1 FR coverage 100% (positive + negative + boundary)
- [ ] BR-TC-001 ~ 015 executed or waived with ID

## Defects

- [ ] Critical/High open = 0 (또는 승인 waiver + 만료일)

## Security

- [ ] Session fixation / reissue (BR-TC-001)
- [ ] CSRF (BR-TC-002)
- [ ] AuthZ admin/IDOR (BR-TC-005, 006)
- [ ] XSS / SQLi (BR-TC-007, 008)
- [ ] OWASP ZAP / manual WSTG notes attached

## Ops

- [ ] Rollback drill 성공 증거
- [ ] Evidence pack (run_id, build_sha, log, screenshot)
- [ ] 외부 금융 데이터 freshness/fallback 확인 (확장, DE)

## AI Boundary

- [ ] BR-TC-011: 개인화 추천/자동 승인 없음

**Gate decision:** PASS / FAIL / CONDITIONAL  
**Sign-off:** ____________
