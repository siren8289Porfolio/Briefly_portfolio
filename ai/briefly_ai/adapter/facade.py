from typing import List, Optional

from briefly_ai.adapter.base import AdapterResult, StubBriefDraftAdapter, StubRiskCandidateAdapter
from briefly_ai.hitl.review_gate import AiOutput, HumanReviewGate, ReviewDecision
from briefly_ai.policy.feature_flags import FeatureFlagStore
from briefly_ai.safety.investment_filter import InvestmentAdviceSafetyFilter


class NonAiPath:
    """Always-available synchronous path when AI is off or fails."""

    def list_manual_briefs(self) -> List[str]:
        # Wired to ReportService / FundService in product; empty stub here.
        return []

    def list_rule_based_funds(self) -> List[str]:
        return []


class AssistiveAiFacade:
    """Composition root for future AI. MVP: all flags off → NonAiPath only."""

    def __init__(
        self,
        flags: Optional[FeatureFlagStore] = None,
        safety: Optional[InvestmentAdviceSafetyFilter] = None,
        review: Optional[HumanReviewGate] = None,
    ):
        self.flags = flags or FeatureFlagStore()
        self.safety = safety or InvestmentAdviceSafetyFilter()
        self.review = review or HumanReviewGate()
        self.non_ai = NonAiPath()
        self.brief_draft = StubBriefDraftAdapter(self.flags, self.safety)
        self.risk_candidate = StubRiskCandidateAdapter(self.flags, self.safety)

    def try_brief_draft(self, fund_id: int, source_text: str) -> AdapterResult:
        result = self.brief_draft.invoke(fund_id, source_text)
        if not result.ok:
            _ = self.non_ai.list_manual_briefs()
        return result

    def try_risk_candidate(self, disclosure_text: str) -> AdapterResult:
        result = self.risk_candidate.invoke(disclosure_text)
        return result

    def publish_after_review(
        self,
        output: AiOutput,
        decision: ReviewDecision,
    ) -> bool:
        return self.review.can_publish(output, decision)
