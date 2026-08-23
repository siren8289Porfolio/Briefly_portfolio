from dataclasses import dataclass
from datetime import datetime
from enum import Enum
from typing import Optional


class OutputKind(str, Enum):
    DRAFT = "DRAFT"
    CANDIDATE = "CANDIDATE"


class ReviewDecision(str, Enum):
    APPROVE = "APPROVE"
    REJECT = "REJECT"
    EDIT_THEN_APPROVE = "EDIT_THEN_APPROVE"


@dataclass(frozen=True)
class AiOutput:
    """AI output is never final publishable content without review."""

    run_id: str
    kind: OutputKind
    content: str
    model_name: str
    model_version: str
    prompt_version: Optional[str]
    created_at: datetime
    confidence: Optional[float] = None


@dataclass
class HumanReviewGate:
    """Mandatory HITL for Brief publish and RiskAlert (AI-R1)."""

    def can_publish(self, output: AiOutput, decision: Optional[ReviewDecision]) -> bool:
        if output.kind not in (OutputKind.DRAFT, OutputKind.CANDIDATE):
            return False
        return decision in (ReviewDecision.APPROVE, ReviewDecision.EDIT_THEN_APPROVE)

    def require_review(self, output: AiOutput) -> None:
        if output.kind == OutputKind.CANDIDATE or output.kind == OutputKind.DRAFT:
            return
        raise ValueError("unsupported output kind for review")
