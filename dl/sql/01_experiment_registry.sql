-- ML/DL experiment registry (DESIGNED)
-- Checkpoint vs production artifact paths are distinct.

CREATE TABLE IF NOT EXISTS ml_experiment (
    experiment_id       VARCHAR(64) PRIMARY KEY,
    task_type           ENUM('classification', 'representation', 'generative_summary') NOT NULL,
    dataset_hash        CHAR(64) NOT NULL,
    dataset_version     VARCHAR(32) NOT NULL,
    tokenizer_version   VARCHAR(32) NOT NULL,
    preprocess_version  VARCHAR(32) NOT NULL,
    model_architecture  VARCHAR(64) NOT NULL,
    model_version       VARCHAR(32) NOT NULL,
    label_schema_version VARCHAR(32) NOT NULL,
    seed                INT NOT NULL,
    learning_rate       DECIMAL(12, 8) NULL,
    batch_size          INT NULL,
    epochs              INT NULL,
    loss_function       VARCHAR(64) NULL,
    checkpoint_path     VARCHAR(512) NULL,
    production_artifact_path VARCHAR(512) NULL,
    status              ENUM('RUNNING', 'COMPLETED', 'FAILED', 'PROMOTED') NOT NULL,
    val_f1_macro        DECIMAL(8, 6) NULL,
    baseline_f1_macro   DECIMAL(8, 6) NULL,
    started_at          DATETIME(3) NOT NULL,
    finished_at         DATETIME(3) NULL,
    git_commit          VARCHAR(64) NULL,
    notes               TEXT NULL
);

CREATE TABLE IF NOT EXISTS ml_evaluation_run (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    experiment_id       VARCHAR(64) NOT NULL,
    evaluation_set_id   VARCHAR(64) NOT NULL,
    evaluation_set_version VARCHAR(32) NOT NULL,
    precision_macro     DECIMAL(8, 6) NULL,
    recall_macro        DECIMAL(8, 6) NULL,
    f1_macro            DECIMAL(8, 6) NULL,
    confusion_json      JSON NULL,
    evaluated_at        DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    KEY idx_eval_experiment (experiment_id)
);

CREATE TABLE IF NOT EXISTS ml_serving_event (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    model_version       VARCHAR(32) NOT NULL,
    route_taken         VARCHAR(32) NOT NULL,
    latency_ms          INT NULL,
    input_length        INT NOT NULL,
    success             BOOLEAN NOT NULL,
    fallback_reason     VARCHAR(128) NULL,
    served_at           DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
);
