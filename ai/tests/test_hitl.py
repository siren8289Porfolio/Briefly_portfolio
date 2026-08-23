from datetime import datetime, timezone

from briefly_ai.hitl.review_gate import AiOutput, HumanReviewGate, OutputKind, ReviewDecision


def _draft() -> AiOutput:
    return AiOutput(
        run_id="run-1",
        kind=OutputKind.DRAFT,
        content="초안 요약",
        model_name="stub",
        model_version="0",
        prompt_version="p1",
        created_at=datetime.now(timezone.utc),
    )


def test_publish_requires_approve() -> None:
    gate = HumanReviewGate()
    draft = _draft()
    assert gate.can_publish(draft, None) is False
    assert gate.can_publish(draft, ReviewDecision.REJECT) is False
    assert gate.can_publish(draft, ReviewDecision.APPROVE) is True
