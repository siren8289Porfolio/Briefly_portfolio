from briefly_dl.eval.classification_metrics import beats_baseline, compute_classification_metrics


def test_classification_metrics_perfect() -> None:
    y = ["a", "a", "b", "b"]
    metrics = compute_classification_metrics(y, y)
    assert metrics.f1_macro == 1.0
    assert metrics.confusion["a"]["a"] == 2


def test_beats_baseline_requires_delta() -> None:
    assert beats_baseline(0.80, 0.75, min_delta=0.01)
    assert not beats_baseline(0.755, 0.75, min_delta=0.01)
