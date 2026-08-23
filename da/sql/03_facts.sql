-- Analytics facts (DESIGNED)
-- External facts mirror DE mart; join keys documented in STAR_SCHEMA.md

CREATE TABLE IF NOT EXISTS fact_user_engagement (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id        VARCHAR(64) NOT NULL UNIQUE,
    date_key        INT NOT NULL,
    user_id_hash    CHAR(64) NOT NULL,
    product_id      BIGINT NULL,
    security_id     VARCHAR(32) NULL,
    event_name      VARCHAR(64) NOT NULL,
    has_market_context BOOLEAN NOT NULL DEFAULT FALSE,
    context_segment VARCHAR(32) NOT NULL DEFAULT 'context_unlinked',
    amount          DECIMAL(15, 2) NULL,
    event_ts        DATETIME(3) NOT NULL,
    KEY idx_engagement_date_product (date_key, product_id, event_name),
    KEY idx_engagement_user (user_id_hash, event_ts)
);

CREATE TABLE IF NOT EXISTS fact_market_snapshot (
    security_id     VARCHAR(32) NOT NULL,
    base_date       DATE NOT NULL,
    close_price     DECIMAL(18, 6) NOT NULL,
    change_rate     DECIMAL(12, 6) NULL,
    source_reference_date DATE NOT NULL,
    fetched_at      DATETIME(3) NOT NULL,
    source_url      VARCHAR(512) NOT NULL,
    PRIMARY KEY (security_id, base_date)
);

CREATE TABLE IF NOT EXISTS fact_disclosure_event (
    corp_code       VARCHAR(16) NOT NULL,
    rcept_no        VARCHAR(20) NOT NULL,
    reference_date  DATE NOT NULL,
    report_name     VARCHAR(300) NOT NULL,
    source_url      VARCHAR(512) NOT NULL,
    fetched_at      DATETIME(3) NOT NULL,
    PRIMARY KEY (corp_code, rcept_no)
);

CREATE TABLE IF NOT EXISTS fact_risk_signal (
    signal_id       BIGINT NOT NULL,
    date_key        INT NOT NULL,
    product_id      BIGINT NOT NULL,
    signal_status   VARCHAR(32) NOT NULL,
    reviewed        BOOLEAN NOT NULL DEFAULT FALSE,
    reviewed_at     DATETIME(3) NULL,
    PRIMARY KEY (signal_id, date_key)
);
