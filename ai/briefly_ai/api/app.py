"""
FastAPI AI Service — assistive explanations for Servlet/JSP.

Does not write to Briefly product DB. Failures must not break core flows;
the Java AIClient treats non-2xx / timeouts as empty Optional.
"""

from fastapi import FastAPI

from briefly_ai.api.explainer import AssistiveExplainer
from briefly_ai.api.schemas import (
    BriefExplainRequest,
    DISCLAIMER,
    ExplainResponse,
    FundExplainRequest,
    RiskExplainRequest,
)
from briefly_ai.policy.feature_flags import FeatureFlagStore

app = FastAPI(
    title="Briefly Assistive AI",
    version="0.1.0",
    description="Fund/brief/risk explanation helper. Not investment advice.",
)

_flags = FeatureFlagStore()
_explainer = AssistiveExplainer()


@app.get("/health")
def health() -> dict:
    return {
        "status": "ok",
        "service": "briefly-ai",
        "assistive_explain": _flags.is_enabled("ai.assistive_explain.enabled"),
    }


@app.post("/v1/explain/fund", response_model=ExplainResponse)
def explain_fund(body: FundExplainRequest) -> ExplainResponse:
    if not _flags.is_enabled("ai.assistive_explain.enabled"):
        return ExplainResponse(ok=False, fallback_reason="feature_disabled")
    ok, text, _findings = _explainer.explain_fund(body)
    if not ok:
        return ExplainResponse(
            ok=False,
            fallback_reason="safety_violation",
            safety_blocked=True,
            disclaimer=DISCLAIMER,
        )
    return ExplainResponse(ok=True, explanation=text, disclaimer=DISCLAIMER)


@app.post("/v1/explain/brief", response_model=ExplainResponse)
def explain_brief(body: BriefExplainRequest) -> ExplainResponse:
    if not _flags.is_enabled("ai.assistive_explain.enabled"):
        return ExplainResponse(ok=False, fallback_reason="feature_disabled")
    ok, text, _findings = _explainer.explain_brief(body)
    if not ok:
        return ExplainResponse(
            ok=False,
            fallback_reason="safety_violation",
            safety_blocked=True,
        )
    return ExplainResponse(ok=True, explanation=text)


@app.post("/v1/explain/risk", response_model=ExplainResponse)
def explain_risk(body: RiskExplainRequest) -> ExplainResponse:
    if not _flags.is_enabled("ai.assistive_explain.enabled"):
        return ExplainResponse(ok=False, fallback_reason="feature_disabled")
    ok, text, _findings = _explainer.explain_risk(body)
    if not ok:
        return ExplainResponse(
            ok=False,
            fallback_reason="safety_violation",
            safety_blocked=True,
        )
    return ExplainResponse(ok=True, explanation=text)
