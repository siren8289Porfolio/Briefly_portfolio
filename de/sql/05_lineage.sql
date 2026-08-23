-- Lineage & pipeline run metadata (DESIGNED)

CREATE TABLE IF NOT EXISTS pipeline_runs (
    pipeline_run_id VARCHAR(64) PRIMARY KEY,
    pipeline_name   VARCHAR(64)  NOT NULL,
    source_dataset  VARCHAR(64)  NOT NULL,
    status          ENUM('RUNNING', 'SUCCESS', 'FAILED', 'PARTIAL') NOT NULL,
    started_at      DATETIME(3)  NOT NULL,
    finished_at     DATETIME(3)  NULL,
    rows_in         BIGINT       NULL,
    rows_out        BIGINT       NULL,
    rows_quarantine BIGINT       NULL,
    error_summary   VARCHAR(1024) NULL
);

CREATE TABLE IF NOT EXISTS lineage_edges (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    pipeline_run_id VARCHAR(64)  NOT NULL,
    upstream_object VARCHAR(128) NOT NULL,
    downstream_object VARCHAR(128) NOT NULL,
    transform_name  VARCHAR(64)  NOT NULL,
    created_at      DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    KEY idx_lineage_run (pipeline_run_id)
);

CREATE TABLE IF NOT EXISTS dq_run_metrics (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    pipeline_run_id VARCHAR(64)  NOT NULL,
    metric_name     VARCHAR(64)  NOT NULL,
    metric_value    DECIMAL(18, 6) NOT NULL,
    threshold_value DECIMAL(18, 6) NOT NULL,
    passed          BOOLEAN      NOT NULL,
    evaluated_at    DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    KEY idx_dq_run (pipeline_run_id)
);
