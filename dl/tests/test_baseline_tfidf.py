from briefly_dl.models.baseline_tfidf import TfidfLinearBaseline


def test_tfidf_baseline_fit_predict() -> None:
    texts = [
        "market volatility increase",
        "market index down",
        "risk alert triggered",
        "risk level high",
    ]
    labels = ["market", "market", "risk", "risk"]
    model = TfidfLinearBaseline()
    model.fit(texts, labels)
    pred = model.predict("market volatility")
    assert pred.label == "market"
    assert pred.confidence > 0
