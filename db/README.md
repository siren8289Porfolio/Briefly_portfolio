# Briefly Database (OLTP)

> MVP MySQL 스키마·시드. 상세 ERD·컬럼 설명은 [`docs/ERD_DB_SCHEMA.md`](../docs/ERD_DB_SCHEMA.md).

## 디렉터리

```text
db/
├── README.md      ← 본 문서
├── schema.sql     ← DDL (users · funds · watchlists · applications · briefs · alerts)
└── seed.sql       ← 로컬/데모 시드
```

## 역할

| 경로 | 역할 |
| --- | --- |
| `schema.sql` | 내부 OLTP DDL·인덱스 |
| `seed.sql` | 개발·데모용 초기 데이터 |
| `docs/ERD_DB_SCHEMA.md` | ERD·테이블 계약 (공통 docs) |

## 원칙

- 내부 트랜잭션(관심·모의가입·Brief·알림)만 이 스키마에 둔다.
- 외부 시세·공시 Context는 `de/` 파이프라인·mart와 분리한다.
- Flyway 도입 시 `db/migration/V1__...`로 이관하는 것을 권장한다 (`docs/ENGINEERING_GUIDE.md`).
