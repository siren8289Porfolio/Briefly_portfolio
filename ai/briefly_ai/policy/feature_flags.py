from dataclasses import dataclass, field
from typing import Dict


# MVP defaults: all AI features OFF
DEFAULT_FLAGS: Dict[str, bool] = {
    "ai.brief_draft.enabled": False,
    "ai.risk_candidate.enabled": False,
    "ai.nl_search.enabled": False,
    "ai.recommendation.enabled": False,
}


@dataclass
class FeatureFlagStore:
    """In-memory feature flags. Production would load from ai_feature_flag table."""

    flags: Dict[str, bool] = field(default_factory=lambda: dict(DEFAULT_FLAGS))

    def is_enabled(self, key: str) -> bool:
        return bool(self.flags.get(key, False))

    def set_enabled(self, key: str, enabled: bool) -> None:
        if key not in DEFAULT_FLAGS:
            raise KeyError("unknown AI feature flag: " + key)
        self.flags[key] = enabled

    def assert_mvp_all_disabled(self) -> None:
        enabled = [k for k, v in self.flags.items() if v]
        if enabled:
            raise AssertionError("MVP requires all AI flags off: " + ", ".join(enabled))
