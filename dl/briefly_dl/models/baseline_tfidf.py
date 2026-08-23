import math
import re
from collections import Counter, defaultdict
from typing import Dict, Iterable, List, Tuple

from briefly_dl.models.model_contract import Prediction

TOKEN = re.compile(r"[a-z0-9]+")


class TfidfLinearBaseline:
    """L1 TF-IDF + multinomial Naive Bayes-style linear baseline (no sklearn dep)."""

    model_version = "tfidf-linear-1.0.0"

    def __init__(self) -> None:
        self._df: Counter[str] = Counter()
        self._class_token_counts: Dict[str, Counter[str]] = defaultdict(Counter)
        self._class_doc_counts: Counter[str] = Counter()
        self._vocab: set[str] = set()
        self._num_docs = 0

    def _tokenize(self, text: str) -> List[str]:
        return TOKEN.findall(text.lower())

    def fit(self, texts: Iterable[str], labels: Iterable[str]) -> None:
        self._df.clear()
        self._class_token_counts.clear()
        self._class_doc_counts.clear()
        self._vocab.clear()
        self._num_docs = 0

        for text, label in zip(texts, labels):
            tokens = self._tokenize(text)
            self._num_docs += 1
            self._class_doc_counts[label] += 1
            seen_in_doc: set[str] = set()
            for tok in tokens:
                self._class_token_counts[label][tok] += 1
                if tok not in seen_in_doc:
                    self._df[tok] += 1
                    seen_in_doc.add(tok)
                    self._vocab.add(tok)

    def _tfidf_vector(self, text: str) -> Counter[str]:
        tokens = self._tokenize(text)
        tf = Counter(tokens)
        vec: Counter[str] = Counter()
        for tok, count in tf.items():
            if tok not in self._vocab:
                continue
            idf = math.log((1 + self._num_docs) / (1 + self._df[tok])) + 1.0
            vec[tok] = (count / len(tokens)) * idf
        return vec

    def predict(self, text: str) -> Prediction:
        if self._num_docs == 0:
            raise RuntimeError("model not fitted")
        vec = self._tfidf_vector(text)
        best_label = "unknown"
        best_score = float("-inf")
        for label in self._class_doc_counts:
            score = self._score_label(vec, label)
            if score > best_score:
                best_score = score
                best_label = label
        confidence = self._to_confidence(best_score)
        return Prediction(
            label=best_label,
            confidence=confidence,
            model_version=self.model_version,
        )

    def predict_batch(self, texts: List[str]) -> List[Prediction]:
        return [self.predict(t) for t in texts]

    def _score_label(self, vec: Counter[str], label: str) -> float:
        token_counts = self._class_token_counts[label]
        total = sum(token_counts.values()) or 1
        score = math.log(self._class_doc_counts[label] / self._num_docs)
        for tok, weight in vec.items():
            prob = (token_counts[tok] + 1) / (total + len(self._vocab))
            score += weight * math.log(prob)
        return score

    @staticmethod
    def _to_confidence(log_score: float) -> float:
        # monotonic squash for display; not calibrated probability
        return round(1.0 / (1.0 + math.exp(-log_score)), 4)
