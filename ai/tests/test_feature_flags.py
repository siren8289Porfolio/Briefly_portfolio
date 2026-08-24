from briefly_ai.policy.feature_flags import DEFAULT_FLAGS, FeatureFlagStore


def test_mvp_generative_defaults_disabled() -> None:
    store = FeatureFlagStore()
    store.assert_mvp_generative_disabled()
    assert store.is_enabled("ai.brief_draft.enabled") is False
    assert store.is_enabled("ai.risk_candidate.enabled") is False
    assert store.is_enabled("ai.nl_search.enabled") is False
    assert store.is_enabled("ai.recommendation.enabled") is False
    assert store.is_enabled("ai.assistive_explain.enabled") is True


def test_unknown_flag_rejected() -> None:
    store = FeatureFlagStore()
    try:
        store.set_enabled("ai.unknown.enabled", True)
        assert False, "expected KeyError"
    except KeyError:
        pass


def test_default_flags_include_assistive_explain() -> None:
    assert "ai.assistive_explain.enabled" in DEFAULT_FLAGS
