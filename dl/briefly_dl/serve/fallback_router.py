from dataclasses import dataclass
from enum import Enum
from typing import List, Optional

from briefly_dl.models.baseline_keyword import KeywordBaselineClassifier
from briefly_dl.models.baseline_tfidf import TfidfLinearBaseline
from briefly_dl.models.model_contract import ModelContract, Prediction, TextClassifier
from briefly_dl.preprocess.text_preprocessor import TextPreprocessor


class Route(str, Enum):
    PRIMARY = "primary"
    TFIDF_FALLBACK = "tfidf_fallback"
    KEYWORD_FALLBACK = "keyword_fallback"
    UNAVAILABLE = "model_unavailable"


@dataclass
class ServeResult:
    prediction: Prediction
    route: Route
    fallback_reason: Optional[str] = None
    input_truncated: bool = False


class FallbackRouter:
    """Serving with explicit fallback chain and version contract."""

    def __init__(
        self,
        contract: ModelContract,
        preprocessor: TextPreprocessor,
        primary: Optional[TextClassifier],
        tfidf: TfidfLinearBaseline,
        keyword: KeywordBaselineClassifier,
        timeout_ms: int = 500,
    ):
        self.contract = contract
        self.preprocessor = preprocessor
        self.primary = primary
        self.tfidf = tfidf
        self.keyword = keyword
        self.timeout_ms = timeout_ms

    def predict(self, text: str, primary_available: bool = True) -> ServeResult:
        processed = self.preprocessor.process(text)
        try:
            self.contract.assert_compatible(
                processed.tokenizer_version,
                processed.preprocess_version,
            )
        except ValueError as exc:
            pred = self.keyword.predict(processed.text)
            return ServeResult(
                prediction=pred,
                route=Route.KEYWORD_FALLBACK,
                fallback_reason=str(exc),
                input_truncated=processed.truncated,
            )

        if primary_available and self.primary is not None:
            try:
                pred = self.primary.predict(processed.text)
                return ServeResult(
                    prediction=pred,
                    route=Route.PRIMARY,
                    input_truncated=processed.truncated,
                )
            except Exception as exc:  # noqa: BLE001 — serving boundary
                return self._tfidf_or_keyword(processed.text, str(exc), processed.truncated)

        return self._tfidf_or_keyword(processed.text, "primary unavailable", processed.truncated)

    def _tfidf_or_keyword(self, text: str, reason: str, truncated: bool) -> ServeResult:
        try:
            pred = self.tfidf.predict(text)
            return ServeResult(
                prediction=pred,
                route=Route.TFIDF_FALLBACK,
                fallback_reason=reason,
                input_truncated=truncated,
            )
        except Exception:
            pred = self.keyword.predict(text)
            return ServeResult(
                prediction=pred,
                route=Route.KEYWORD_FALLBACK,
                fallback_reason=reason,
                input_truncated=truncated,
            )
