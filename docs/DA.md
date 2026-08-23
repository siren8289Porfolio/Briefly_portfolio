# Briefly Data Analytics (요약)

상세 설계·KPI·이벤트·스타 스키마·코드는 [`da/`](../da/) 모듈을 본다.

| 항목 | 위치 | Evidence |
| --- | --- | --- |
| KPI Catalog | `da/docs/KPI_CATALOG.md` | DESIGNED |
| Event Taxonomy | `da/docs/EVENT_TAXONOMY.md` | DESIGNED |
| Star Schema | `da/docs/STAR_SCHEMA.md` · `da/sql/` | DESIGNED |
| Analysis Design | `da/docs/ANALYSIS_DESIGN.md` | DESIGNED |
| KPI / Funnel Java | `da/src/main/java/com/briefly/da/` | DESIGNED (+ unit test) |
| BI Dashboard / GA4 | — | PLANNED / NOT TESTED |

원칙: 상관 분석만 (인과 단정 금지). user_id 마스킹. DE mart와 Context join. 분모 0 → KPI null.

DE 연계: [`docs/DE.md`](./DE.md)
