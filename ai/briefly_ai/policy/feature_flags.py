from dataclasses import dataclass, field
from typing import Dict


# MVP: LLM draft / risk-candidate / search / recommendation stay OFF.
# Assistive explain (template FastAPI) is ON for v1 Servlet integration.
DEFAULT_FLAGS: Dict[str, bool] = {
    "ai.brief_draft.enabled": False,
    "ai.risk_candidate.enabled": False,
    "ai.nl_search.enabled": False,
    "ai.recommendation.enabled": False,
    "ai.assistive_explain.enabled": True,
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

    def assert_mvp_generative_disabled(self) -> None:
        generative = [
            "ai.brief_draft.enabled",
            "ai.risk_candidate.enabled",
            "ai.nl_search.enabled",
            "ai.recommendation.enabled",
        ]
        enabled = [k for k in generative if self.flags.get(k)]
        if enabled:
            raise AssertionError(
                "MVP requires generative AI flags off: " + ", ".join(enabled)
            )

    def assert_mvp_all_disabled(self) -> None:
        # Kept for older tests; generative subset is the MVP hard gate.
        self.assert_mvp_generative_disabled()
