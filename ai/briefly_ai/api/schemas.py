from datetime import datetime, timezone
from typing import Optional

from pydantic import BaseModel, Field


DISCLAIMER = (
    "본 설명은 정보 이해를 돕기 위한 보조 문구이며 "
    "투자 권유·확정 수익·자동 위험 판정이 아닙니다."
)


class FundExplainRequest(BaseModel):
    fund_id: int
    name: str = Field(min_length=1)
    description: str = ""
    risk_grade: int = Field(ge=1, le=5)
    expected_return: Optional[float] = None


class BriefExplainRequest(BaseModel):
    fund_id: int
    title: str = Field(min_length=1)
    content: str = Field(min_length=1)
    report_date: Optional[str] = None


class RiskExplainRequest(BaseModel):
    fund_id: int
    title: str = Field(min_length=1)
    message: str = Field(min_length=1)
    previous_grade: int = Field(ge=1, le=5)
    new_grade: int = Field(ge=1, le=5)


class ExplainResponse(BaseModel):
    ok: bool
    explanation: Optional[str] = None
    disclaimer: str = DISCLAIMER
    model: str = "template-v1"
    generated_at: str = Field(
        default_factory=lambda: datetime.now(timezone.utc).isoformat()
    )
    fallback_reason: Optional[str] = None
    safety_blocked: bool = False
