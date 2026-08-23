from briefly_ai.safety.investment_filter import InvestmentAdviceSafetyFilter


def test_blocks_buy_sell_language() -> None:
    result = InvestmentAdviceSafetyFilter().check("지금 매수하세요")
    assert result.blocked
    assert "buy_sell" in result.findings


def test_blocks_guaranteed_return() -> None:
    result = InvestmentAdviceSafetyFilter().check("원금 보장 상품입니다")
    assert result.blocked
    assert "guaranteed" in result.findings


def test_allows_neutral_context() -> None:
    text = "시세 기준일 2026-08-21 (fetched_at 포함). 투자 판단은 이용자 책임입니다."
    result = InvestmentAdviceSafetyFilter().check(text)
    assert not result.blocked
    assert result.sanitized_text == text
