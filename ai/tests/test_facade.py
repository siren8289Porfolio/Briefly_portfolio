from briefly_ai.adapter.facade import AssistiveAiFacade
from briefly_ai.audit.provenance import ProvenanceLogger
from briefly_ai.policy.feature_flags import FeatureFlagStore


def test_facade_falls_back_when_flag_off() -> None:
    facade = AssistiveAiFacade()
    result = facade.try_brief_draft(1, "공시 본문")
    assert result.ok is False
    assert result.fallback_reason == "feature_disabled"


def test_facade_does_not_auto_risk_when_flag_off() -> None:
    result = AssistiveAiFacade().try_risk_candidate("중요 공시 발생")
    assert result.ok is False
    assert result.fallback_reason == "feature_disabled"


def test_provenance_records_model_versions() -> None:
    logger = ProvenanceLogger()
    record = logger.start_run(
        feature_key="ai.brief_draft.enabled",
        model_name="gpt-stub",
        model_version="n/a",
        prompt_version="v0",
        retrieval_sources=[{"source_url": "https://opendart.fss.or.kr/", "reference_date": "2026-08-21"}],
    )
    assert record.model_version == "n/a"
    assert record.to_dict()["retrieval_sources"][0]["source_url"]


def test_enabling_flag_still_not_implemented() -> None:
    flags = FeatureFlagStore()
    flags.set_enabled("ai.brief_draft.enabled", True)
    facade = AssistiveAiFacade(flags=flags)
    result = facade.try_brief_draft(1, "text")
    assert result.ok is False
    assert "NOT IMPLEMENTED" in (result.fallback_reason or "")
