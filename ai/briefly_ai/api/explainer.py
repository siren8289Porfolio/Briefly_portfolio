"""Template-based assistive explanations (no LLM, no investment advice)."""

from typing import List, Optional, Tuple

from briefly_ai.api.schemas import (
    BriefExplainRequest,
    FundExplainRequest,
    RiskExplainRequest,
)
from briefly_ai.safety.investment_filter import InvestmentAdviceSafetyFilter

RISK_LABELS = {
    1: "낮은 위험",
    2: "다소 낮은 위험",
    3: "보통 위험",
    4: "높은 위험",
    5: "매우 높은 위험",
}

_MAX_SNIPPET = 180


def _clip(text: str, limit: int = _MAX_SNIPPET) -> str:
    cleaned = " ".join((text or "").split())
    if len(cleaned) <= limit:
        return cleaned
    return cleaned[: limit - 1] + "…"


class AssistiveExplainer:
    """Builds user-friendly copy from structured fund/brief/alert fields."""

    def __init__(self, safety: Optional[InvestmentAdviceSafetyFilter] = None):
        self.safety = safety or InvestmentAdviceSafetyFilter()

    def explain_fund(self, req: FundExplainRequest) -> Tuple[bool, str, List[str]]:
        label = RISK_LABELS.get(req.risk_grade, "보통 위험")
        return_part = ""
        if req.expected_return is not None:
            return_part = f" 화면에 표시된 예상 수익률은 {req.expected_return}%입니다."
        desc = _clip(req.description) or "상세 설명이 등록되어 있습니다."
        text = (
            f"‘{req.name}’은(는) 위험등급 {req.risk_grade}({label}) 상품입니다."
            f"{return_part} 핵심 내용은 다음과 같습니다: {desc} "
            "이 문구는 상품 정보를 쉽게 읽기 위한 보조 설명입니다."
        )
        return self._safe(text)

    def explain_brief(self, req: BriefExplainRequest) -> Tuple[bool, str, List[str]]:
        date_part = f" ({req.report_date})" if req.report_date else ""
        body = _clip(req.content)
        text = (
            f"운용 브리프 ‘{req.title}’{date_part}의 요지입니다: {body} "
            "원문 브리프를 대체하지 않으며, 이해를 돕는 요약 보조입니다."
        )
        return self._safe(text)

    def explain_risk(self, req: RiskExplainRequest) -> Tuple[bool, str, List[str]]:
        direction = "상향" if req.new_grade > req.previous_grade else (
            "하향" if req.new_grade < req.previous_grade else "유지"
        )
        msg = _clip(req.message)
        text = (
            f"위험 알림 ‘{req.title}’은 위험등급이 "
            f"{req.previous_grade}에서 {req.new_grade}로 {direction}된 안내입니다. "
            f"메시지 요지: {msg} "
            "공시·시세 발생만으로 위험이 자동 확정된 것이 아니며, "
            "관리자 검토 후 등록된 서비스 알림을 설명합니다."
        )
        return self._safe(text)

    def _safe(self, text: str) -> Tuple[bool, str, List[str]]:
        result = self.safety.check(text)
        if result.blocked:
            return False, "", list(result.findings)
        return True, result.sanitized_text, []
