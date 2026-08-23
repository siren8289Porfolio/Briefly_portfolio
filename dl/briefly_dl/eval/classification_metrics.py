from dataclasses import dataclass
from typing import Dict, Iterable, List, Tuple


@dataclass(frozen=True)
class ClassificationMetrics:
    precision_macro: float
    recall_macro: float
    f1_macro: float
    per_class: Dict[str, Dict[str, float]]
    confusion: Dict[str, Dict[str, int]]


def _prf(tp: int, fp: int, fn: int) -> Tuple[float, float, float]:
    precision = tp / (tp + fp) if (tp + fp) else 0.0
    recall = tp / (tp + fn) if (tp + fn) else 0.0
    f1 = (2 * precision * recall / (precision + recall)) if (precision + recall) else 0.0
    return precision, recall, f1


def compute_classification_metrics(
    y_true: Iterable[str],
    y_pred: Iterable[str],
) -> ClassificationMetrics:
    labels = sorted(set(list(y_true) + list(y_pred)))
    confusion: Dict[str, Dict[str, int]] = {a: {b: 0 for b in labels} for a in labels}
    per_class: Dict[str, Dict[str, float]] = {}

    yt = list(y_true)
    yp = list(y_pred)
    if len(yt) != len(yp):
        raise ValueError("y_true and y_pred length mismatch")

    for t, p in zip(yt, yp):
        confusion[t][p] += 1

    precisions: list[float] = []
    recalls: list[float] = []
    f1s: list[float] = []

    for label in labels:
        tp = confusion[label][label]
        fp = sum(confusion[other][label] for other in labels if other != label)
        fn = sum(confusion[label][other] for other in labels if other != label)
        p, r, f1 = _prf(tp, fp, fn)
        per_class[label] = {"precision": round(p, 6), "recall": round(r, 6), "f1": round(f1, 6)}
        precisions.append(p)
        recalls.append(r)
        f1s.append(f1)

    n = len(labels) or 1
    return ClassificationMetrics(
        precision_macro=round(sum(precisions) / n, 6),
        recall_macro=round(sum(recalls) / n, 6),
        f1_macro=round(sum(f1s) / n, 6),
        per_class=per_class,
        confusion=confusion,
    )


def beats_baseline(candidate_f1: float, baseline_f1: float, min_delta: float = 0.01) -> bool:
    return candidate_f1 >= baseline_f1 + min_delta
