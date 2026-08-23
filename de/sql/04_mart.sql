-- Serving mart (DESIGNED)
-- Context only — never auto investment / risk labels

CREATE TABLE IF NOT EXISTS dim_security (
    security_id     VARCHAR(32) PRIMARY KEY,
    isin            VARCHAR(12)  NULL,
    srtn_cd         VARCHAR(12)  NULL,
    name            VARCHAR(200) NOT NULL,
    market_category VARCHAR(40)  NULL,
    corp_name       VARCHAR(200) NULL,
    corp_regn_no    VARCHAR(20)  NULL,
    source_dataset  VARCHAR(64)  NOT NULL,
    source_url      VARCHAR(512) NOT NULL,
    base_date       DATE         NOT NULL,
    fetched_at      DATETIME(3)  NOT NULL,
    pipeline_run_id VARCHAR(64)  NOT NULL,
    schema_version  VARCHAR(16)  NOT NULL,
    master_mapping_version VARCHAR(16) NOT NULL,
    UNIQUE KEY uk_dim_isin (isin),
    KEY idx_dim_srtn (srtn_cd)
);

CREATE TABLE IF NOT EXISTS dim_company (
    corp_code       VARCHAR(16) PRIMARY KEY,
    corp_name       VARCHAR(200) NOT NULL,
    corp_regn_no    VARCHAR(20)  NULL,
    stock_code      VARCHAR(12)  NULL,
    source_dataset  VARCHAR(64)  NOT NULL,
    source_url      VARCHAR(512) NOT NULL,
    reference_date  DATE         NULL,
    fetched_at      DATETIME(3)  NOT NULL,
    pipeline_run_id VARCHAR(64)  NOT NULL
);

CREATE TABLE IF NOT EXISTS mart_market_snapshot (
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
    source_name     VARCHAR(128) NOT NULL,
    source_url      VARCHAR(512) NOT NULL,
    fetched_at      DATETIME(3)  NOT NULL,
    pipeline_run_id VARCHAR(64)  NOT NULL,
    checksum        CHAR(64)     NOT NULL,
    schema_version  VARCHAR(16)  NOT NULL,
    master_mapping_version VARCHAR(16) NOT NULL,
    PRIMARY KEY (security_id, base_date),
    CONSTRAINT fk_mart_mkt_security FOREIGN KEY (security_id) REFERENCES dim_security(security_id)
);

CREATE TABLE IF NOT EXISTS mart_disclosure (
    corp_code       VARCHAR(16)  NOT NULL,
    rcept_no        VARCHAR(20)  NOT NULL,
    corp_name       VARCHAR(200) NULL,
    stock_code      VARCHAR(12)  NULL,
    report_name     VARCHAR(300) NOT NULL,
    reference_date  DATE         NOT NULL,
    notice_type     VARCHAR(40)  NULL,
    source_dataset  VARCHAR(64)  NOT NULL,
    source_name     VARCHAR(128) NOT NULL,
    source_url      VARCHAR(512) NOT NULL,
    fetched_at      DATETIME(3)  NOT NULL,
    pipeline_run_id VARCHAR(64)  NOT NULL,
    checksum        CHAR(64)     NOT NULL,
    PRIMARY KEY (corp_code, rcept_no)
);

-- Optional product ↔ security bridge (OLTP funds.id → DE security_id)
CREATE TABLE IF NOT EXISTS bridge_fund_security (
    fund_id         BIGINT       NOT NULL,
    security_id     VARCHAR(32)  NOT NULL,
    weight_pct      DECIMAL(9, 6) NULL,
    mapping_version VARCHAR(16)  NOT NULL,
    PRIMARY KEY (fund_id, security_id)
);
