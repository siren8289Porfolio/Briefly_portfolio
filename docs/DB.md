# Briefly Database (요약)

상세 ERD·DDL은 [`db/`](../db/) 및 [`ERD_DB_SCHEMA.md`](./ERD_DB_SCHEMA.md)를 본다.

| 항목 | 위치 | Evidence |
| --- | --- | --- |
| DDL | `db/schema.sql` | DESIGNED |
| Seed | `db/seed.sql` | DESIGNED |
| ERD / 컬럼 계약 | `docs/ERD_DB_SCHEMA.md` | DESIGNED |
| Flyway migration | — | PLANNED |

원칙: 내부 OLTP만 `db/`에 둔다. 외부 Context는 `de/`와 분리.
