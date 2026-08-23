-- Staging / conformed layer (DESIGNED)
-- Typed, normalized, not yet DQ-promoted

CREATE TABLE IF NOT EXISTS stg_security_master (
    security_id     VARCHAR(32)  NOT NULL,
    isin            VARCHAR(12)  NULL,
    srtn_cd         VARCHAR(12)  NULL,
    name            VARCHAR(200) NOT NULL,
    market_category VARCHAR(40)  NULL,
    corp_name       VARCHAR(200) NULL,
    corp_regn_no    VARCHAR(20)  NULL,
    base_date       DATE         NOT NULL,
    source_dataset  VARCHAR(64)  NOT NULL,
    fetched_at      DATETIME(3)  NOT NULL,
    pipeline_run_id VARCHAR(64)  NOT NULL,
    PRIMARY KEY (security_id, base_date)
);

CREATE TABLE IF NOT EXISTS stg_market_snapshot (
    security_id     VARCHAR(32)  NOT NULL,
    base_date       DATE         NOT NULL,
    close_price     DECIMAL(18, 6) NOT NULL,
    open_price      DECIMAL(18, 6) NULL,
    high_price      DECIMAL(18, 6) NULL,
    low_price       DECIMAL(18, 6) NULL,
    change_price    DECIMAL(18, 6) NULL,
    change_rate     DECIMAL(12, 6) NULL,
    volume          BIGINT       NULL,
    trade_amount    DECIMAL(20, 2) NULL,
    currency        CHAR(3)      NOT NULL DEFAULT 'KRW',
    source_dataset  VARCHAR(64)  NOT NULL,
    source_url      VARCHAR(512) NOT NULL,
    fetched_at      DATETIME(3)  NOT NULL,
    pipeline_run_id VARCHAR(64)  NOT NULL,
    checksum        CHAR(64)     NOT NULL,
    PRIMARY KEY (security_id, base_date)
);

CREATE TABLE IF NOT EXISTS stg_disclosure (
    corp_code       VARCHAR(16)  NOT NULL,
    rcept_no        VARCHAR(20)  NOT NULL,
    corp_name       VARCHAR(200) NULL,
    stock_code      VARCHAR(12)  NULL,
    report_name     VARCHAR(300) NOT NULL,
    reference_date  DATE         NOT NULL,
    notice_type     VARCHAR(40)  NULL,
    source_dataset  VARCHAR(64)  NOT NULL,
    source_url      VARCHAR(512) NOT NULL,
    fetched_at      DATETIME(3)  NOT NULL,
    pipeline_run_id VARCHAR(64)  NOT NULL,
    checksum        CHAR(64)     NOT NULL,
    PRIMARY KEY (corp_code, rcept_no)
);
