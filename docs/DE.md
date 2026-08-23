# Briefly Data Engineering (요약)

상세 설계·카탈로그·SQL·코드는 [`de/`](../de/) 모듈을 본다.

| 항목 | 위치 | Evidence |
| --- | --- | --- |
| 공공 데이터 카탈로그 | `de/docs/DATA_CATALOG.md` | DESIGNED |
| Pipeline stages | `de/docs/PIPELINE.md` | DESIGNED |
| DQ Gates | `de/docs/DQ_GATES.md` | DESIGNED (+ unit test) |
| Lineage | `de/docs/LINEAGE.md` · `de/sql/05_lineage.sql` | DESIGNED |
| Medallion SQL | `de/sql/01_raw.sql` … `04_mart.sql` | DESIGNED |
| Java pipeline | `de/src/main/java/com/briefly/de/` | DESIGNED (stub ingest) |
| Live OpenAPI / Airflow | — | PLANNED / NOT TESTED |

원칙: 외부 데이터는 Context/Evidence only. 가격은 BigDecimal. 내부 모의가입·관심상품 트랜잭션과 분리.
