-- Raw analytics events (DESIGNED)
-- GA4-style; idempotent by event_id

CREATE TABLE IF NOT EXISTS raw_event (
    event_id        VARCHAR(64)  PRIMARY KEY,
    event_name      VARCHAR(64)  NOT NULL,
    event_ts        DATETIME(3)  NOT NULL,
    user_id_hash    CHAR(64)     NOT NULL,
    session_id      VARCHAR(64)  NULL,
    product_id      BIGINT       NULL,
    security_id     VARCHAR(32)  NULL,
    payload_json    JSON         NULL,
    source_page     VARCHAR(256) NULL,
    ingested_at     DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    KEY idx_raw_event_ts (event_ts),
    KEY idx_raw_event_name (event_name, event_ts),
    KEY idx_raw_event_user (user_id_hash, event_ts)
);

CREATE TABLE IF NOT EXISTS validated_event (
    event_id        VARCHAR(64)  PRIMARY KEY,
    event_name      VARCHAR(64)  NOT NULL,
    event_date      DATE         NOT NULL,
    event_ts        DATETIME(3)  NOT NULL,
    user_id_hash    CHAR(64)     NOT NULL,
    product_id      BIGINT       NULL,
    security_id     VARCHAR(32)  NULL,
    has_market_context BOOLEAN   NOT NULL DEFAULT FALSE,
    context_segment VARCHAR(32)  NOT NULL DEFAULT 'context_unlinked',
    amount          DECIMAL(15, 2) NULL,
    validated_at    DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    KEY idx_validated_date (event_date, event_name)
);
