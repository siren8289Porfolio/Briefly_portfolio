-- Raw immutable layer (DESIGNED)
-- Retain raw OpenAPI payloads for audit / reprocess (min 1 year)

CREATE TABLE IF NOT EXISTS raw_api_snapshot (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_dataset  VARCHAR(64)  NOT NULL,
    source_name     VARCHAR(128) NOT NULL,
    source_url      VARCHAR(512) NOT NULL,
    base_date       DATE         NULL,
    fetched_at      DATETIME(3)  NOT NULL,
    pipeline_run_id VARCHAR(64)  NOT NULL,
    schema_version  VARCHAR(16)  NOT NULL,
    checksum        CHAR(64)     NOT NULL,
    payload_json    LONGTEXT     NOT NULL,
    http_status     INT          NULL,
    created_at      DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    UNIQUE KEY uk_raw_checksum (source_dataset, checksum, fetched_at),
    KEY idx_raw_dataset_fetched (source_dataset, fetched_at),
    KEY idx_raw_run (pipeline_run_id)
);
