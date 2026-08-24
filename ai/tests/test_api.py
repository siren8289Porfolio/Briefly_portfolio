from fastapi.testclient import TestClient

from briefly_ai.api.app import app
from briefly_ai.api.explainer import AssistiveExplainer
from briefly_ai.api.schemas import FundExplainRequest
from briefly_ai.policy.feature_flags import FeatureFlagStore


client = TestClient(app)


def test_health() -> None:
    res = client.get("/health")
    assert res.status_code == 200
    body = res.json()
    assert body["status"] == "ok"
    assert body["assistive_explain"] is True


def test_explain_fund_ok() -> None:
    res = client.post(
        "/v1/explain/fund",
        json={
            "fund_id": 1,
            "name": "글로벌 채권형",
            "description": "중장기 채권에 분산 투자하는 상품입니다.",
            "risk_grade": 2,
            "expected_return": 3.5,
        },
    )
    assert res.status_code == 200
    body = res.json()
    assert body["ok"] is True
    assert "위험등급 2" in body["explanation"]
    assert "투자 권유" in body["disclaimer"]


def test_explain_brief_ok() -> None:
    res = client.post(
        "/v1/explain/brief",
        json={
            "fund_id": 1,
            "title": "7월 운용 브리프",
            "content": "금리 변동에 따라 듀레이션을 짧게 유지했습니다.",
            "report_date": "2026-07-31",
        },
    )
    assert res.status_code == 200
    assert res.json()["ok"] is True


def test_explain_risk_ok() -> None:
    res = client.post(
        "/v1/explain/risk",
        json={
            "fund_id": 1,
            "title": "위험등급 변경",
            "message": "변동성 확대로 등급이 조정되었습니다.",
            "previous_grade": 2,
            "new_grade": 3,
        },
    )
    assert res.status_code == 200
    body = res.json()
    assert body["ok"] is True
    assert "상향" in body["explanation"]


def test_explainer_blocks_advice_language() -> None:
    explainer = AssistiveExplainer()
    ok, text, findings = explainer._safe("이 상품을 매수하세요")
    assert ok is False
    assert "buy_sell" in findings
    assert text == ""


def test_feature_flag_disables_explain() -> None:
    from briefly_ai.api import app as app_module

    flags = FeatureFlagStore()
    flags.set_enabled("ai.assistive_explain.enabled", False)
    original = app_module._flags
    app_module._flags = flags
    try:
        res = client.post(
            "/v1/explain/fund",
            json={
                "fund_id": 1,
                "name": "테스트",
                "description": "설명",
                "risk_grade": 3,
            },
        )
        assert res.status_code == 200
        assert res.json()["ok"] is False
        assert res.json()["fallback_reason"] == "feature_disabled"
    finally:
        app_module._flags = original


def test_fund_request_validation() -> None:
    req = FundExplainRequest(
        fund_id=1, name="A", description="", risk_grade=5, expected_return=None
    )
    assert req.risk_grade == 5
