# Briefly SDD

## 1. 문서 정보

| 항목 | 내용 |
| --- | --- |
| 문서명 | Briefly SDD |
| 버전 | v0.1 |
| 작성일 | 2026-07-03 |
| 제품명 | Briefly |
| 대상 | MVP |
| 기술 | Java Servlet / JSP / JDBC / MySQL 또는 MariaDB |

---

## 2. 설계 목적

Briefly SDD는 SRS의 요구사항을 실제 Servlet/JSP/JDBC 개발 구조로 변환하기 위한 설계 문서다.

핵심 설계 목표는 다음과 같다.

```
Servlet은 요청/응답 처리
Service는 비즈니스 로직 처리
DAO는 DB 접근 처리
DTO는 데이터 전달
Filter는 인증/권한 처리
JSP는 화면 렌더링
```

---

## 3. 설계 범위

| 기능 ID | 기능명 | 설명 | 관련 SRS |
| --- | --- | --- | --- |
| F-001 | 인증 | 회원가입, 로그인, 로그아웃 | FR-001~FR-004 |
| F-002 | 투자상품 탐색 | 상품 목록/상세 조회 | FR-005~FR-006 |
| F-003 | 관심상품/모의가입 | 관심 등록, 모의가입 신청, 신청 내역 조회 | FR-007~FR-011 |
| F-004 | 브리프/알림 | 운용 브리프, 위험 알림 조회 | FR-012~FR-013 |
| F-005 | 관리자 기능 | 상품/브리프/알림/신청 상태 관리 | FR-014~FR-016 |

---

## 4. 전체 구조

```
Browser
 ↓
Servlet Controller
 ↓
Service
 ↓
DAO
 ↓
Database

Servlet Controller
 ↓
JSP
```

| 계층 | 책임 |
| --- | --- |
| Servlet | 요청 파라미터 수신, Service 호출, JSP 이동 |
| Service | 비즈니스 규칙 처리 |
| DAO | JDBC 기반 SQL 실행 |
| DTO | Servlet-Service-DAO 간 데이터 전달 |
| Filter | 로그인/관리자 권한 검사 |
| JSP | 화면 출력 |

---

## 5. 패키지 구조 (도메인별 + 레이어별)

```
src/main/java/com/briefly
 ├── auth/
 │   ├── controller/   AuthServlet.java
 │   ├── service/      AuthService.java
 │   ├── dao/          UserDao.java
 │   ├── dto/          UserDto.java
 │   └── entity/       User.java
 │
 ├── fund/
 │   ├── controller/   FundServlet.java
 │   ├── service/      FundService.java
 │   ├── dao/          FundDao.java
 │   ├── dto/          FundDto.java
 │   └── entity/       Fund.java
 │
 ├── watchlist/
 │   ├── controller/   WatchlistServlet.java
 │   ├── service/      WatchlistService.java
 │   ├── dao/          WatchlistDao.java
 │   ├── dto/          WatchlistDto.java
 │   └── entity/       Watchlist.java
 │
 ├── application/
 │   ├── controller/   ApplicationServlet.java
 │   ├── service/      ApplicationService.java
 │   ├── dao/          ApplicationDao.java
 │   ├── dto/          ApplicationDto.java
 │   └── entity/       FundApplication.java
 │
 ├── report/
 │   ├── controller/   ReportServlet.java
 │   ├── service/      ReportService.java
 │   ├── dao/          ReportDao.java
 │   ├── dto/          ReportDto.java
 │   └── entity/       FundReport.java
 │
 ├── alert/
 │   ├── controller/   AlertServlet.java
 │   ├── service/      AlertService.java
 │   ├── dao/          AlertDao.java
 │   ├── dto/          AlertDto.java
 │   └── entity/       RiskAlert.java
 │
 ├── admin/
 │   └── controller/   AdminServlet.java
 │
 └── common/
     ├── filter/       LoginCheckFilter, AdminCheckFilter
     └── util/         DBConnectionUtil, WebUtil, SessionUtil, PasswordUtil
```

도메인 패키지 안에 `controller → service → dao → entity/dto` 레이어를 둔다.  
`common`은 전 도메인 공통 인프라만 담는다.

---

## 6. 모듈 설계

| 모듈 | 책임 | 관련 기능 |
| --- | --- | --- |
| Auth Module | 회원가입, 로그인, 로그아웃, 세션 관리 | FR-001~FR-004 |
| Fund Module | 투자상품 목록/상세 조회 | FR-005~FR-006 |
| Watchlist Module | 관심상품 등록/해제 | FR-007~FR-008 |
| Application Module | 모의가입 신청/상태 조회 | FR-009~FR-011 |
| Brief Module | 운용 브리프 조회 | FR-012 |
| Alert Module | 위험 알림 조회 | FR-013 |
| Admin Module | 상품, 브리프, 알림, 신청 상태 관리 | FR-014~FR-016 |

---

## 7. 데이터 설계

### 7.1 Entity / Table

| 테이블 | 설명 | 주요 필드 |
| --- | --- | --- |
| users | 사용자/관리자 | user_id, email, password, name, role |
| funds | 투자상품 | fund_id, fund_name, company_name, risk_level, status |
| watchlist | 관심상품 | watchlist_id, user_id, fund_id |
| fund_applications | 모의가입 신청 | application_id, user_id, fund_id, amount, status |
| fund_reports | 운용 브리프 | report_id, fund_id, title, report_month, summary |
| risk_alerts | 위험 알림 | alert_id, fund_id, previous_risk_level, current_risk_level, message |

> 실제 DDL·컬럼명은 [ERD / DB Schema](./ERD_DB_SCHEMA.md) 및 `back/dB/schema.sql`을 기준으로 한다.

### 7.2 주요 관계

```
users 1:N watchlist
funds 1:N watchlist

users 1:N fund_applications
funds 1:N fund_applications

funds 1:N fund_reports
funds 1:N risk_alerts
```

---

## 8. Servlet URL 설계

| ID | Method | URL | Servlet | 설명 |
| --- | --- | --- | --- | --- |
| API-001 | GET/POST | `/signup` | AuthServlet | 회원가입 |
| API-002 | GET/POST | `/login` | AuthServlet | 로그인 |
| API-003 | POST | `/logout` | AuthServlet | 로그아웃 |
| API-004 | GET | `/funds` | FundServlet | 상품 목록 |
| API-005 | GET | `/funds/detail` | FundServlet | 상품 상세 |
| API-006 | POST | `/watchlist/toggle` | WatchlistServlet | 관심상품 등록/해제 |
| API-007 | POST | `/applications` | ApplicationServlet | 모의가입 신청 |
| API-008 | GET | `/applications` | ApplicationServlet | 내 신청 내역 |
| API-009 | GET | `/reports` | ReportServlet | 운용 브리프 |
| API-010 | GET | `/alerts` | AlertServlet | 위험 알림 |
| API-011 | GET/POST | `/admin/funds` | AdminServlet | 상품 관리 |
| API-012 | POST | `/admin/reports` | AdminServlet | 브리프 등록 |
| API-013 | POST | `/admin/alerts` | AdminServlet | 알림 등록 |
| API-014 | POST | `/admin/applications/status` | AdminServlet | 신청 상태 변경 |

---

## 9. 클래스 설계

| 구분 | 클래스 | 책임 |
| --- | --- | --- |
| Servlet | AuthServlet | 회원가입/로그인/로그아웃 요청 처리 |
| Servlet | FundServlet | 상품 목록/상세 요청 처리 |
| Servlet | WatchlistServlet | 관심상품 등록/해제 요청 처리 |
| Servlet | ApplicationServlet | 모의가입 신청/조회 요청 처리 |
| Servlet | ReportServlet | 운용 브리프 조회 요청 처리 |
| Servlet | AlertServlet | 위험 알림 조회 요청 처리 |
| Servlet | AdminServlet | 관리자 요청 처리 |
| Service | AuthService | 계정 생성, 로그인 검증 |
| Service | FundService | 상품 조회 로직 |
| Service | WatchlistService | 관심상품 중복 확인/토글 |
| Service | ApplicationService | 신청 생성/상태 조회 |
| Service | ReportService | 브리프 조회 |
| Service | AlertService | 관심상품 기준 알림 조회 |
| DAO | UserDao | users SQL 처리 |
| DAO | FundDao | funds SQL 처리 |
| DAO | WatchlistDao | watchlist SQL 처리 |
| DAO | ApplicationDao | fund_applications SQL 처리 |
| DAO | ReportDao | fund_reports SQL 처리 |
| DAO | AlertDao | risk_alerts SQL 처리 |

---

## 10. 주요 로직

### 10.1 로그인

```
입력:
- email
- password

처리:
1. email로 사용자 조회
2. 비밀번호 검증
3. 일치하면 Session에 loginUser 저장
4. role에 따라 사용자/관리자 화면으로 이동

예외:
- 사용자 없음
- 비밀번호 불일치
```

### 10.2 관심상품 등록/해제

```
입력:
- userId
- fundId

처리:
1. 로그인 여부 확인
2. 기존 관심상품 존재 여부 조회
3. 존재하면 삭제
4. 없으면 등록
5. 상품 상세 또는 관심상품 화면으로 이동
```

### 10.3 모의가입 신청

```
입력:
- userId
- fundId
- amount

처리:
1. 로그인 여부 확인
2. 신청 금액이 0보다 큰지 검증
3. 동일 상품의 진행 중 신청이 있는지 확인
4. 없으면 PENDING 상태로 신청 저장
5. 신청 내역 화면으로 이동
```

### 10.4 위험 알림 조회

```
입력:
- userId

처리:
1. 로그인 여부 확인
2. 사용자의 관심상품 목록 조회
3. 관심상품 fundId 기준 risk_alerts 조회
4. 최신순으로 JSP에 전달
```

---

## 11. 상태 설계

### 11.1 User Role

| 상태 | 설명 |
| --- | --- |
| USER | 일반 사용자 |
| ADMIN | 관리자 |

### 11.2 Fund Status

| 상태 | 설명 |
| --- | --- |
| ACTIVE | 사용자 화면 노출 |
| INACTIVE | 사용자 화면 미노출 |

### 11.3 Application Status

| 상태 | 설명 |
| --- | --- |
| PENDING | 신청 접수 |
| APPROVED | 승인 |
| REJECTED | 반려 |
| CANCELED | 취소 |

```
PENDING → APPROVED
PENDING → REJECTED
PENDING → CANCELED
```

### 11.4 Risk Level

| 등급 | 설명 |
| --- | --- |
| 1 | 낮은 위험 |
| 2 | 다소 낮은 위험 |
| 3 | 보통 위험 |
| 4 | 높은 위험 |
| 5 | 매우 높은 위험 |

---

## 12. 트랜잭션 설계

| 유스케이스 | 트랜잭션 범위 | 실패 시 처리 |
| --- | --- | --- |
| 회원가입 | users 저장 | 저장 실패 시 Rollback |
| 관심상품 등록 | watchlist 저장 | 저장 실패 시 Rollback |
| 관심상품 해제 | watchlist 삭제 | 삭제 실패 시 Rollback |
| 모의가입 신청 | fund_applications 저장 | 저장 실패 시 Rollback |
| 상품 등록 | funds 저장 | 저장 실패 시 Rollback |
| 브리프 등록 | fund_reports 저장 | 저장 실패 시 Rollback |
| 위험 알림 등록 | risk_alerts 저장 | 저장 실패 시 Rollback |
| 신청 상태 변경 | fund_applications.status 수정 | 수정 실패 시 Rollback |

---

## 13. 예외 처리 설계

| Error Code | 상황 | 처리 |
| --- | --- | --- |
| INVALID_REQUEST | 필수값 누락, 잘못된 금액 | JSP 오류 메시지 표시 |
| UNAUTHORIZED | 로그인 필요 | `/login`으로 redirect |
| FORBIDDEN | 관리자 권한 없음 | 접근 불가 페이지 표시 |
| NOT_FOUND | 데이터 없음 | 조회 결과 없음 메시지 표시 |
| DUPLICATED_DATA | 중복 관심상품/중복 신청 | 기존 상태 유지 또는 오류 메시지 표시 |
| INTERNAL_SERVER_ERROR | 서버 오류 | 공통 오류 페이지 표시 |

---

## 14. 보안 설계

| 항목 | 설계 |
| --- | --- |
| 인증 | Session 기반 로그인 |
| 인가 | USER / ADMIN Role 기반 |
| 로그인 체크 | LoginCheckFilter |
| 관리자 체크 | AdminCheckFilter |
| 비밀번호 | 해시 저장 |
| 민감정보 | JSP에 password 노출 금지 |
| 접근 제한 | `/admin/*`은 ADMIN만 접근 |

---

## 15. 테스트 설계

| 테스트 ID | 대상 | 검증 내용 | 관련 요구사항 |
| --- | --- | --- | --- |
| TC-001 | AuthService | 정상 로그인 시 Session 생성 | FR-002 |
| TC-002 | FundService | ACTIVE 상품만 조회 | FR-005 |
| TC-003 | WatchlistService | 관심상품 등록/해제 | FR-007 |
| TC-004 | WatchlistService | 중복 관심상품 방지 | FR-008 |
| TC-005 | ApplicationService | 모의가입 신청 PENDING 생성 | FR-009, FR-010 |
| TC-006 | AlertService | 관심상품 기준 알림 조회 | FR-013 |
| TC-007 | AdminServlet | 일반 사용자 관리자 접근 차단 | FR-014 |
| TC-008 | AdminServlet | 신청 상태 변경 | FR-016 |

---

## 16. 개발로 넘길 항목

| SDD 항목 | 개발 산출물 |
| --- | --- |
| 패키지 구조 | Java 디렉터리 생성 |
| 데이터 설계 | SQL DDL 작성 |
| URL 설계 | web.xml 또는 @WebServlet 매핑 |
| Service 설계 | Service 클래스 구현 |
| DAO 설계 | JDBC SQL 구현 |
| 상태 설계 | enum 또는 상수 클래스 |
| 보안 설계 | Filter 구현 |
| 예외 처리 | JSP 오류 메시지 / error.jsp |
| 테스트 설계 | 기능별 테스트케이스 |

---

## 17. 체크리스트

| 체크 항목 | 확인 |
| --- | --- |
| SRS 요구사항과 설계가 연결되는가? | □ |
| Servlet / Service / DAO 책임이 분리되었는가? | □ |
| DTO와 DB 테이블이 구분되었는가? | □ |
| URL 매핑이 정의되었는가? | □ |
| DB 테이블과 관계가 정의되었는가? | □ |
| 주요 로직이 정리되었는가? | □ |
| 예외 처리가 정의되었는가? | □ |
| 트랜잭션 범위가 정의되었는가? | □ |
| 테스트케이스로 검증 가능한가? | □ |

---

관련 문서: [SRS v0.1](./SRS.md) · [ERD / DB Schema](./ERD_DB_SCHEMA.md)
