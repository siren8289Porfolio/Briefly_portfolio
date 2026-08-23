package com.briefly.de.validate;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StockPriceRowValidatorTest {
    private final StockPriceRowValidator validator = new StockPriceRowValidator();

    @Test
    void acceptsValidRow() {
        var result = validator.validate(Map.of(
                "basDt", "20260821",
                "isinCd", "KR7005930003",
                "clpr", "72000"
        ));
        assertTrue(result.passed());
    }

    @Test
    void rejectsMissingPrice() {
        var result = validator.validate(Map.of(
                "basDt", "20260821",
                "srtnCd", "005930"
        ));
        assertFalse(result.passed());
    }
}
