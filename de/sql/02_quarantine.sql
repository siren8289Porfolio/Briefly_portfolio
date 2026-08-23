-- Quarantine layer (DESIGNED)
-- Invalid / schema-fail rows isolated with reason codes

CREATE TABLE IF NOT EXISTS quarantine_record (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_dataset  VARCHAR(64)  NOT NULL,
    pipeline_run_id VARCHAR(64)  NOT NULL,
    raw_snapshot_id BIGINT       NULL,
    reason_code     VARCHAR(64)  NOT NULL,
    reason_detail   VARCHAR(512) NULL,
    payload_json    LONGTEXT     NULL,
    quarantined_at  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    KEY idx_quarantine_dataset (source_dataset, quarantined_at),
    KEY idx_quarantine_reason (reason_code)
);
