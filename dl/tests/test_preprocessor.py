from briefly_dl.data.pii_filter import PiiFilter
from briefly_dl.preprocess.text_preprocessor import TextPreprocessor
from briefly_dl.preprocess.tokenizer_config import TokenizerConfig


def test_preprocessor_truncates_and_redacts() -> None:
    config = TokenizerConfig(version="tok-1", max_length=5, truncation="tail")
    pp = TextPreprocessor(config, preprocess_version="pp-1")
    text = "contact user@example.com " + "hello " * 10
    result = pp.process(text)
    assert len(result.tokens) == 5
    assert result.truncated
    assert "[REDACTED_EMAIL]" in result.text


def test_pii_filter_finds_email() -> None:
    out = PiiFilter().redact("email me at a@b.co")
    assert out.redacted
    assert "email" in out.findings
