from briefly_ai.policy.feature_flags import DEFAULT_FLAGS, FeatureFlagStore


def test_mvp_defaults_all_disabled() -> None:
    store = FeatureFlagStore()
    store.assert_mvp_all_disabled()
    for key in DEFAULT_FLAGS:
        assert store.is_enabled(key) is False


def test_unknown_flag_rejected() -> None:
    store = FeatureFlagStore()
    try:
        store.set_enabled("ai.unknown.enabled", True)
        assert False, "expected KeyError"
    except KeyError:
        pass
