from dataclasses import dataclass
from typing import Dict, List, Optional, Protocol


@dataclass(frozen=True)
class Prediction:
    label: str
    confidence: float
    model_version: str
    uncertainty_note: Optional[str] = None


class TextClassifier(Protocol):
    model_version: str

    def predict(self, text: str) -> Prediction: ...

    def predict_batch(self, texts: List[str]) -> List[Prediction]: ...


@dataclass(frozen=True)
class ModelContract:
    model_version: str
    tokenizer_version: str
    preprocess_version: str
    label_schema_version: str
    max_input_length: int

    def assert_compatible(self, tokenizer_version: str, preprocess_version: str) -> None:
        if tokenizer_version != self.tokenizer_version:
            raise ValueError(
                f"tokenizer mismatch: expected {self.tokenizer_version}, got {tokenizer_version}"
            )
        if preprocess_version != self.preprocess_version:
            raise ValueError(
                f"preprocess mismatch: expected {self.preprocess_version}, got {preprocess_version}"
            )
