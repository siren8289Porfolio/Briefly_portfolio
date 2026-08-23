-- Analytics dimensions (DESIGNED)

CREATE TABLE IF NOT EXISTS dim_date (
    date_key        INT PRIMARY KEY,
    full_date       DATE NOT NULL UNIQUE,
    year_num        SMALLINT NOT NULL,
    month_num       TINYINT NOT NULL,
    week_num        TINYINT NOT NULL,
    is_business_day BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS dim_product (
    product_id      BIGINT PRIMARY KEY,
    product_name    VARCHAR(120) NOT NULL,
    risk_level      TINYINT NOT NULL,
    status          VARCHAR(16) NOT NULL,
    effective_from  DATE NOT NULL,
    effective_to    DATE NULL
);

CREATE TABLE IF NOT EXISTS dim_security (
    security_id     VARCHAR(32) PRIMARY KEY,
    isin            VARCHAR(12) NULL,
    srtn_cd         VARCHAR(12) NULL,
    security_name   VARCHAR(200) NULL
);

CREATE TABLE IF NOT EXISTS dim_company (
    corp_code       VARCHAR(16) PRIMARY KEY,
    corp_name       VARCHAR(200) NOT NULL,
    stock_code      VARCHAR(12) NULL
);

CREATE TABLE IF NOT EXISTS dim_user (
    user_id_hash    CHAR(64) PRIMARY KEY,
    segment         VARCHAR(32) NOT NULL DEFAULT 'general',
    first_seen_date DATE NULL
);

CREATE TABLE IF NOT EXISTS dim_status (
    status_code     VARCHAR(32) PRIMARY KEY,
    status_domain   VARCHAR(32) NOT NULL,
    description     VARCHAR(200) NULL
);
