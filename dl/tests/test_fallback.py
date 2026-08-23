from briefly_dl.models.baseline_keyword import KeywordBaselineClassifier
from briefly_dl.models.baseline_tfidf import TfidfLinearBaseline
from briefly_dl.models.model_contract import ModelContract
from briefly_dl.preprocess.text_preprocessor import TextPreprocessor
from briefly_dl.preprocess.tokenizer_config import TokenizerConfig
from briefly_dl.serve.fallback_router import FallbackRouter, Route


def test_fallback_router_uses_tfidf_when_primary_down() -> None:
    texts = ["market up", "market rally", "risk high", "risk alert"]
    labels = ["market", "market", "risk", "risk"]
    tfidf = TfidfLinearBaseline()
    tfidf.fit(texts, labels)
    keyword = KeywordBaselineClassifier({"market": ["market"], "risk": ["risk"]})
    config = TokenizerConfig(version="tok-1", max_length=32)
    pp = TextPreprocessor(config, preprocess_version="pp-1")
    contract = ModelContract(
        model_version="dl-0",
        tokenizer_version="tok-1",
        preprocess_version="pp-1",
        label_schema_version="1.0",
        max_input_length=32,
    )
    router = FallbackRouter(contract, pp, primary=None, tfidf=tfidf, keyword=keyword)
    result = router.predict("market rally today", primary_available=False)
    assert result.route == Route.TFIDF_FALLBACK
    assert result.prediction.label == "market"


def test_fallback_router_keyword_on_version_mismatch() -> None:
    tfidf = TfidfLinearBaseline()
    tfidf.fit(["market"], ["market"])
    keyword = KeywordBaselineClassifier({"market": ["market"]})
    pp = TextPreprocessor(TokenizerConfig(version="tok-1", max_length=16), "pp-1")
    contract = ModelContract("m1", "tok-WRONG", "pp-1", "1.0", 16)
    router = FallbackRouter(contract, pp, None, tfidf, keyword)
    result = router.predict("market news")
    assert result.route == Route.KEYWORD_FALLBACK
