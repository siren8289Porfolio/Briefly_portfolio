# Training & Experiment Design

> Evidence: **DESIGNED**

## Experiment Log (required fields)

- `experiment_id`, `dataset_hash`, `tokenizer_version`, `model_architecture`
- hyperparameters: `learning_rate`, `batch_size`, `epochs`, `loss`, `seed`
- `checkpoint_path` vs `production_artifact_path` (구분)
- git commit / pipeline run id

## Discipline

- 한 실험당 **하나의 작은 변경** (feature OR architecture OR hyperparameter)
- validation degradation / overfitting 체크 필수
- Table: `dl/sql/01_experiment_registry.sql`

## NOT IMPLEMENTED

- GPU training pipeline
- Automated hyperparameter search
- Continuous training (CT)
