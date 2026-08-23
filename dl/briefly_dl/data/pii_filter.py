import re
from dataclasses import dataclass
from typing import List, Tuple

EMAIL = re.compile(r"[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+")
PHONE = re.compile(r"\b01[0-9]-?\d{3,4}-?\d{4}\b")
RRN = re.compile(r"\b\d{6}-?[1-4]\d{6}\b")


@dataclass
class PiiFilterResult:
    text: str
    redacted: bool
    findings: List[str]


class PiiFilter:
    """Minimal PII masking for training/serving preprocess."""

    def redact(self, text: str) -> PiiFilterResult:
        findings: list[str] = []
        out = text
        for name, pattern in (("email", EMAIL), ("phone", PHONE), ("rrn", RRN)):
            if pattern.search(out):
                findings.append(name)
                out = pattern.sub(f"[REDACTED_{name.upper()}]", out)
        return PiiFilterResult(out, bool(findings), findings)
