-- AI assistive run & human review audit (DESIGNED)
-- MVP: tables unused until feature flags enabled

CREATE TABLE IF NOT EXISTS ai_feature_flag (
    flag_key        VARCHAR(64) PRIMARY KEY,
    enabled         BOOLEAN NOT NULL DEFAULT FALSE,
    updated_at      DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_by      BIGINT NULL
);

CREATE TABLE IF NOT EXISTS ai_run (
    run_id          VARCHAR(64) PRIMARY KEY,
    feature_key     VARCHAR(64) NOT NULL,
    model_name      VARCHAR(128) NOT NULL,
    model_version   VARCHAR(64) NOT NULL,
    prompt_version  VARCHAR(64) NULL,
    retrieval_sources JSON NULL,
    input_ref       VARCHAR(256) NULL,
    output_status   ENUM('DRAFT', 'CANDIDATE', 'BLOCKED', 'FAILED', 'FALLBACK') NOT NULL,
    confidence      DECIMAL(8, 6) NULL,
    latency_ms      INT NULL,
    cost_units      DECIMAL(12, 6) NULL,
    created_at      DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    KEY idx_ai_run_feature (feature_key, created_at)
);

CREATE TABLE IF NOT EXISTS ai_review_decision (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    run_id          VARCHAR(64) NOT NULL,
    reviewer_id     BIGINT NOT NULL,
    decision        ENUM('APPROVE', 'REJECT', 'EDIT_THEN_APPROVE') NOT NULL,
    decided_at      DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    notes           TEXT NULL,
    KEY idx_ai_review_run (run_id)
);

INSERT INTO ai_feature_flag (flag_key, enabled) VALUES
('ai.brief_draft.enabled', FALSE),
('ai.risk_candidate.enabled', FALSE),
('ai.nl_search.enabled', FALSE),
('ai.recommendation.enabled', FALSE)
ON DUPLICATE KEY UPDATE flag_key = flag_key;
