from dataclasses import asdict, dataclass
from typing import Any, Dict, Optional


@dataclass(frozen=True)
class ArtifactManifest:
    model_version: str
    tokenizer_version: str
    preprocess_version: str
    label_schema_version: str
    training_dataset_hash: str
    task_type: str
    checkpoint_path: Optional[str] = None
    production_artifact_path: Optional[str] = None

    def to_dict(self) -> Dict[str, Any]:
        return asdict(self)

    def is_production_ready(self) -> bool:
        return self.production_artifact_path is not None
