from dataclasses import dataclass
from typing import Optional

from briefly_dl.eval.classification_metrics import ClassificationMetrics


@dataclass
class EvaluationReport:
    experiment_id: str
    evaluation_set_id: str
    evaluation_set_version: str
    metrics: ClassificationMetrics
    baseline_f1_macro: Optional[float] = None

    @property
    def improves_over_baseline(self) -> Optional[bool]:
        if self.baseline_f1_macro is None:
            return None
        return self.metrics.f1_macro > self.baseline_f1_macro
