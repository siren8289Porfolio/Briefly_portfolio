import re
from dataclasses import dataclass

from briefly_dl.data.pii_filter import PiiFilter
from briefly_dl.preprocess.tokenizer_config import TokenizerConfig

WHITESPACE = re.compile(r"\s+")


@dataclass(frozen=True)
class PreprocessResult:
    text: str
    tokens: list[str]
    truncated: bool
    preprocess_version: str
    tokenizer_version: str


class TextPreprocessor:
    """Deterministic preprocess for training-serving parity."""

    def __init__(self, config: TokenizerConfig, preprocess_version: str = "1.0.0"):
        self.config = config
        self.preprocess_version = preprocess_version
        self.pii_filter = PiiFilter()

    def process(self, raw_text: str) -> PreprocessResult:
        normalized = WHITESPACE.sub(" ", raw_text.strip().lower())
        redacted = self.pii_filter.redact(normalized).text
        tokens = redacted.split(" ")
        truncated = len(tokens) > self.config.max_length
        if truncated:
            if self.config.truncation == "tail":
                tokens = tokens[: self.config.max_length]
            else:
                tokens = tokens[-self.config.max_length :]
        return PreprocessResult(
            text=" ".join(tokens),
            tokens=tokens,
            truncated=truncated,
            preprocess_version=self.preprocess_version,
            tokenizer_version=self.config.version,
        )
