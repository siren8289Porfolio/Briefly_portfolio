from briefly_dl.models.baseline_keyword import KeywordBaselineClassifier


def test_keyword_baseline_matches_label() -> None:
    clf = KeywordBaselineClassifier(
        {
            "market": ["시장", "market"],
            "risk": ["위험", "risk"],
        }
    )
    pred = clf.predict("시장 변동성 확대")
    assert pred.label == "market"
    assert pred.confidence > 0


def test_keyword_baseline_unknown() -> None:
    clf = KeywordBaselineClassifier({"market": ["시장"]})
    pred = clf.predict("unrelated content")
    assert pred.label == "unknown"
    assert pred.uncertainty_note
