-- Briefly database schema (MySQL 8+)
CREATE DATABASE IF NOT EXISTS briefly
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE briefly;

CREATE TABLE users (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    email        VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    name         VARCHAR(80)  NOT NULL,
    role         ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE funds (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(120) NOT NULL,
    description     TEXT,
    risk_grade      TINYINT NOT NULL CHECK (risk_grade BETWEEN 1 AND 5),
    expected_return DECIMAL(5, 2) NOT NULL,
    status          ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE watchlists (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    fund_id    BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_watchlist_user_fund (user_id, fund_id),
    CONSTRAINT fk_watchlist_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_watchlist_fund FOREIGN KEY (fund_id) REFERENCES funds(id) ON DELETE CASCADE
);

CREATE TABLE fund_applications (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    fund_id    BIGINT NOT NULL,
    amount     DECIMAL(15, 2) NOT NULL,
    status     ENUM('PENDING', 'APPROVED', 'REJECTED', 'CANCELED') NOT NULL DEFAULT 'PENDING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_application_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_application_fund FOREIGN KEY (fund_id) REFERENCES funds(id) ON DELETE CASCADE
);

CREATE TABLE fund_reports (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    fund_id     BIGINT NOT NULL,
    title       VARCHAR(200) NOT NULL,
    content     TEXT NOT NULL,
    report_date DATE NOT NULL,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_report_fund FOREIGN KEY (fund_id) REFERENCES funds(id) ON DELETE CASCADE
);

CREATE TABLE risk_alerts (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    fund_id         BIGINT NOT NULL,
    title           VARCHAR(200) NOT NULL,
    message         TEXT NOT NULL,
    previous_grade  TINYINT NOT NULL CHECK (previous_grade BETWEEN 1 AND 5),
    new_grade       TINYINT NOT NULL CHECK (new_grade BETWEEN 1 AND 5),
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_alert_fund FOREIGN KEY (fund_id) REFERENCES funds(id) ON DELETE CASCADE
);

CREATE INDEX idx_funds_status ON funds(status);
CREATE INDEX idx_applications_user ON fund_applications(user_id);
CREATE INDEX idx_applications_status ON fund_applications(status);
CREATE INDEX idx_reports_fund ON fund_reports(fund_id);
CREATE INDEX idx_alerts_fund ON risk_alerts(fund_id);
