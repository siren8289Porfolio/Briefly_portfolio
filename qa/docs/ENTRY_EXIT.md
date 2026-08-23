# Entry / Exit Criteria

> Evidence: **DESIGNED**

## Entry

- [ ] 승인된 PRD / SRS / SDD baseline
- [ ] `build_sha`, migration script, seed data, environment 정의
- [ ] Test data owner 지정
- [ ] QA branch/baseline tag 기록

## Exit (Release Gate)

- [ ] P0/P1 FR coverage 100% (positive + negative + boundary)
- [ ] Critical/High open defect = 0 (또는 승인 waiver + 만료일)
- [ ] 보안 스캔 (Session, CSRF, XSS, SQLi) 통과
- [ ] Rollback rehearsal 성공 증거
- [ ] Evidence pack 완성 (`qa/evidence/`)

체크리스트 실행본: [`../checklists/RELEASE_GATE.md`](../checklists/RELEASE_GATE.md)
