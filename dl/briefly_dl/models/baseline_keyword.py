from collections import Counter
from typing import Dict, Iterable, List

from briefly_dl.models.model_contract import Prediction


class KeywordBaselineClassifier:
    """L0 rule/keyword baseline — always available fallback."""

    model_version = "keyword-baseline-1.0.0"

    def __init__(self, label_keywords: Dict[str, List[str]]):
        self.label_keywords = {
            label: [kw.lower() for kw in keywords]
            for label, keywords in label_keywords.items()
        }

    def predict(self, text: str) -> Prediction:
        lowered = text.lower()
        scores: dict[str, int] = {}
        for label, keywords in self.label_keywords.items():
            scores[label] = sum(1 for kw in keywords if kw in lowered)
        if not scores or max(scores.values()) == 0:
            return Prediction(
                label="unknown",
                confidence=0.0,
                model_version=self.model_version,
                uncertainty_note="no keyword match",
            )
        best_label = max(scores, key=scores.get)
        total = sum(scores.values()) or 1
        confidence = scores[best_label] / total
        return Prediction(
            label=best_label,
            confidence=round(confidence, 4),
            model_version=self.model_version,
        )

    def predict_batch(self, texts: List[str]) -> List[Prediction]:
        return [self.predict(t) for t in texts]

    def fit(self, texts: Iterable[str], labels: Iterable[str]) -> None:
        """No-op for keyword baseline; keywords are configured explicitly."""
        _ = list(zip(texts, labels))
