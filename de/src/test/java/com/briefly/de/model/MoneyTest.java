package com.briefly.de.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoneyTest {
    @Test
    void ofPrice_usesExplicitScale() {
        Money price = Money.ofPrice("1234.5", "KRW");
        assertEquals(new BigDecimal("1234.500000"), price.amount());
        assertEquals("KRW", price.currency());
    }

    @Test
    void ofPrice_rejectsBlankAndNegative() {
        assertThrows(IllegalArgumentException.class, () -> Money.ofPrice("", "KRW"));
        assertThrows(IllegalArgumentException.class, () -> Money.ofPrice("-1", "KRW"));
    }
}
