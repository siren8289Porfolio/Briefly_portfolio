# Test Levels (Pyramid)

> Evidence: **DESIGNED** · E2E/ZAP 실행: **NOT TESTED**

| Level | 대상 | 도구/방식 | 현재 |
| --- | --- | --- | --- |
| Unit | Domain rule, status transition, BigDecimal | JUnit (`qa` module) | DESIGNED (+ tests) |
| Component | DAO, Service, Session filter | Mockito / Servlet Test | PLANNED |
| Contract | URL mapping, error message | Manual + assertion | DESIGNED (catalog) |
| Integration | DB transaction, Session lifecycle | Testcontainers / H2 | PLANNED |
| E2E | 로그인→상세→관심→모의가입→브리프 | Selenium / Playwright | PLANNED |
| Non-functional | OWASP WSTG, Performance, Recovery | OWASP ZAP, restore drill | PLANNED |

## Smoke Suite (minimum before merge)

- BR-TC-001, 002, 006, 009, 014, 015
- AI 경계 BR-TC-011 (추천/자동승인 없음)
