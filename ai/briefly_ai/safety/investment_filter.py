import re
from dataclasses import dataclass
from typing import List, Pattern, Tuple


# Investment advice / definitive language — 0 tolerance (AI-R5)
BLOCK_PATTERNS: List[Tuple[str, Pattern[str]]] = [
    ("buy_sell", re.compile(r"(매수|매도|사세요|팔세요|buy\s+now|sell\s+now)", re.I)),
    ("guaranteed", re.compile(r"(확정\s*수익|원금\s*보장|보장\s*수익|guaranteed\s+return)", re.I)),
    ("recommendation", re.compile(r"(투자\s*권유|추천\s*드립니다|must\s+invest|you\s+should\s+invest)", re.I)),
    ("auto_risk", re.compile(r"(자동\s*위험\s*확정|위험등급\s*확정|auto[- ]?risk\s+confirmed)", re.I)),
]


@dataclass(frozen=True)
class SafetyResult:
    blocked: bool
    findings: List[str]
    sanitized_text: str


class InvestmentAdviceSafetyFilter:
    """Blocks investment advice / definitive claims before any AI text is shown."""

    def check(self, text: str) -> SafetyResult:
        findings: List[str] = []
        for code, pattern in BLOCK_PATTERNS:
            if pattern.search(text):
                findings.append(code)
        if findings:
            return SafetyResult(True, findings, "")
        return SafetyResult(False, [], text)
