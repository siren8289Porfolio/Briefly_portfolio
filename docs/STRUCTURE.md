# Briefly Repository Structure

> Overview depth ≤ 3. Module internals (Java packages, etc.) live under each leaf.

```text
Briefly_portfolio/
├── .gitignore
├── README.md
├── docs/                              # 공통 설계·모듈 요약
│   ├── SRS.md
│   ├── SDD.md
│   ├── ERD_DB_SCHEMA.md
│   ├── ENGINEERING_GUIDE.md
│   ├── DATA_EFFICIENCY_GUIDE.md
│   ├── DE.md
│   ├── DA.md
│   ├── DL.md
│   ├── AI.md
│   ├── QA.md
│   └── STRUCTURE.md
├── back/                              # Servlet/JSP 백엔드 (MVP)
│   ├── pom.xml
│   ├── dB/
│   │   ├── schema.sql
│   │   └── seed.sql
│   └── src/
│       └── main/                      # java · resources · webapp
├── front/                             # Vite/React 프론트
│   ├── package.json
│   ├── vite.config.js
│   ├── index.html
│   ├── public/
│   │   ├── favicon.svg
│   │   └── icons.svg
│   └── src/
│       ├── main.jsx
│       ├── App.jsx
│       ├── App.css
│       ├── index.css
│       ├── api/
│       ├── pages/
│       └── assets/
├── de/                                # Data Engineering
│   ├── README.md
│   ├── pom.xml
│   ├── docs/                          # DATA_CATALOG · PIPELINE · DQ_GATES · LINEAGE
│   ├── sql/                           # 01_raw … 05_lineage
│   └── src/                           # main|test/java
├── da/                                # Data Analytics
│   ├── README.md
│   ├── pom.xml
│   ├── docs/                          # KPI · EVENT · STAR_SCHEMA · ANALYSIS
│   ├── sql/                           # 01_raw_event … 04_kpi_views
│   └── src/                           # main|test/java
├── dl/                                # ML/DL NLP
│   ├── README.md
│   ├── pyproject.toml
│   ├── docs/                          # PROBLEM · DATA · MODEL · TRAIN · EVAL · SERVING · RAI
│   ├── sql/
│   │   └── 01_experiment_registry.sql
│   ├── briefly_dl/                    # data · preprocess · models · eval · serve · train · versioning
│   └── tests/
├── ai/                                # Assistive AI (MVP OFF)
│   ├── README.md
│   ├── pyproject.toml
│   ├── docs/                          # SCOPE · REQUIREMENTS · DATA · SYSTEM · EVAL · MONITORING
│   ├── sql/
│   │   └── 01_ai_audit.sql
│   ├── briefly_ai/                    # policy · safety · hitl · adapter · audit
│   └── tests/
└── qa/                                # QA/QC
    ├── README.md
    ├── pom.xml
    ├── docs/                          # STRATEGY · LEVELS · ENTRY_EXIT · TRACEABILITY · BR_TC · SECURITY · DQ · DEFECT
    ├── checklists/
    │   └── RELEASE_GATE.md
    ├── evidence/
    │   └── README.md
    └── src/                           # main|test/java
```

## Module map

| Path | Role |
| --- | --- |
| `back/` | Servlet/JSP MVP |
| `front/` | React (Vite) |
| `de/` | External financial data pipeline |
| `da/` | KPI / events / star schema |
| `dl/` | NLP baselines & DL contracts |
| `ai/` | Future assistive AI (flags OFF) |
| `qa/` | BR-TC, release gate, oracles |
| `docs/` | SRS/SDD + module summaries |
