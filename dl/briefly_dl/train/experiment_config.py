from dataclasses import asdict, dataclass
from typing import Any, Dict, Optional


@dataclass
class ExperimentConfig:
    experiment_id: str
    task_type: str  # classification | representation | generative_summary
    dataset_hash: str
    dataset_version: str
    tokenizer_version: str
    preprocess_version: str
    model_architecture: str
    model_version: str
    label_schema_version: str
    seed: int
    learning_rate: Optional[float] = None
    batch_size: Optional[int] = None
    epochs: Optional[int] = None
    loss_function: Optional[str] = None

    def to_dict(self) -> Dict[str, Any]:
        return asdict(self)

    def validate(self) -> None:
        if self.task_type not in ("classification", "representation", "generative_summary"):
            raise ValueError("invalid task_type")
        if self.seed < 0:
            raise ValueError("seed must be non-negative")
