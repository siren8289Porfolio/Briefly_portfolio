# Briefly

투자 운용사 실무 흐름을 일반 투자자용 B2C 서비스로 단순화한 **Servlet/JSP MVP** 포트폴리오 프로젝트입니다.

## 구조

```
Briefly_portfolio/
├── front/     # React + Vite (선택 UI)
├── back/      # Servlet + JSP (MVP 메인)
│   └── src/main/java/com/briefly/
│       ├── auth/          # controller, service, dao, dto, entity
│       ├── fund/
│       ├── watchlist/
│       ├── application/
│       ├── report/
│       ├── alert/
│       ├── admin/
│       └── common/        # filter, util
│   └── dB/    # MySQL schema & seed
└── docs/      # SRS · ERD · SDD
```

## 빠른 시작

### 1. 데이터베이스

```bash
mysql -u root -p < back/dB/schema.sql
mysql -u root -p < back/dB/seed.sql
```

`back/src/main/resources/db.properties`에서 DB 접속 정보를 수정하세요.

### 2. 백엔드

```bash
cd back
mvn package
# target/briefly.war 를 Tomcat webapps에 배포
# http://localhost:8080/briefly/funds
```

### 3. 프론트엔드 (선택)

```bash
cd front
npm install
npm run dev
```

## 주요 기능

- 투자상품 목록/상세 탐색
- 관심상품 등록/해제
- 모의가입 신청 및 상태 조회
- 운용 브리프 / 위험 알림 조회
- 관리자: 상품·브리프·알림·신청 상태 관리

자세한 요구사항·API·정책은 아래 문서를 참고하세요.

- [docs/SRS.md](docs/SRS.md) — 기능/비기능 요구사항, URL, 비즈니스 규칙
- [docs/ERD_DB_SCHEMA.md](docs/ERD_DB_SCHEMA.md) — ERD, 테이블 정의, 조회 쿼리
- [docs/SDD.md](docs/SDD.md) — Servlet/Service/DAO/JSP 설계
- [docs/ENGINEERING_GUIDE.md](docs/ENGINEERING_GUIDE.md) — Clean Code, Spring, DB, 운영 학습 정리
- [docs/DATA_EFFICIENCY_GUIDE.md](docs/DATA_EFFICIENCY_GUIDE.md) — DB 최적화, 인덱스, ETL, 데이터 품질
