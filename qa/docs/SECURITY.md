# Security Test (OWASP WSTG Mapping)

> Evidence: **DESIGNED** · ZAP/manual run: **NOT TESTED**

| WSTG | Focus | BR-TC |
| --- | --- | --- |
| WSTG-ATHN | Session fixation, logout 후 재사용 불가, 세션 재발급 | BR-TC-001 |
| WSTG-ATHZ | Admin 경로 403, IDOR (타인 application/watchlist) | BR-TC-005, 006 |
| WSTG-INPV | XSS (브리프 HTML), SQLi (PreparedStatement only) | BR-TC-007, 008 |
| WSTG-BUSL | 상태 전이 불법 경로, CSRF on state-changing POST | BR-TC-002, 004, 014 |
| WSTG-ERRH | 에러 메시지에 스택/민감정보 미노출 | Component/E2E |

## ASVS Alignment (summary)

- Session management (V3)
- Access control (V4)
- Input validation (V5)
- Error handling & logging (V7)
