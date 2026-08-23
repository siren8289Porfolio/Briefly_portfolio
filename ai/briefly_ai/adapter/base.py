from abc import ABC, abstractmethod
from dataclasses import dataclass
from typing import List, Optional

from briefly_ai.hitl.review_gate import AiOutput, OutputKind
from briefly_ai.policy.feature_flags import FeatureFlagStore
from briefly_ai.safety.investment_filter import InvestmentAdviceSafetyFilter


@dataclass(frozen=True)
class AdapterResult:
    ok: bool
    output: Optional[AiOutput]
    fallback_reason: Optional[str] = None
    safety_blocked: bool = False
    safety_findings: Optional[List[str]] = None


class AiAdapter(ABC):
    """Optional AI adapter. When flag off or failure → non-AI fallback."""

    feature_key: str

    def __init__(
        self,
        flags: FeatureFlagStore,
        safety: InvestmentAdviceSafetyFilter,
    ):
        self.flags = flags
        self.safety = safety

    def invoke(self, *args, **kwargs) -> AdapterResult:
        if not self.flags.is_enabled(self.feature_key):
            return AdapterResult(False, None, fallback_reason="feature_disabled")
        try:
            draft = self._generate(*args, **kwargs)
        except Exception as exc:  # noqa: BLE001 — adapter boundary
            return AdapterResult(False, None, fallback_reason=str(exc))

        safety = self.safety.check(draft.content)
        if safety.blocked:
            return AdapterResult(
                False,
                None,
                fallback_reason="safety_violation",
                safety_blocked=True,
                safety_findings=safety.findings,
            )
        return AdapterResult(True, draft)

    @abstractmethod
    def _generate(self, *args, **kwargs) -> AiOutput:
        raise NotImplementedError


class StubBriefDraftAdapter(AiAdapter):
    """Placeholder — NOT IMPLEMENTED LLM. Raises if somehow called for real gen."""

    feature_key = "ai.brief_draft.enabled"

    def _generate(self, fund_id: int, source_text: str) -> AiOutput:
        raise NotImplementedError(
            "LLM brief draft is NOT IMPLEMENTED in MVP; enable only after model evidence"
        )


class StubRiskCandidateAdapter(AiAdapter):
    feature_key = "ai.risk_candidate.enabled"

    def _generate(self, disclosure_text: str) -> AiOutput:
        raise NotImplementedError(
            "Risk candidate extraction is NOT IMPLEMENTED; disclosure ≠ auto RiskAlert"
        )
