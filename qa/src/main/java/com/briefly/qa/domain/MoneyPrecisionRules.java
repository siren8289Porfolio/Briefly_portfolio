package com.briefly.qa.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Amount / return precision oracle (BR-TC-009).
 * Float/double equality is forbidden.
 */
public final class MoneyPrecisionRules {

    public static final int AMOUNT_SCALE = 2;
    public static final int RETURN_SCALE = 2;

    private MoneyPrecisionRules() {}

    public static BigDecimal normalizeAmount(BigDecimal raw) {
        if (raw == null) {
            throw new IllegalArgumentException("amount required");
        }
        if (raw.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be > 0");
        }
        return raw.setScale(AMOUNT_SCALE, RoundingMode.HALF_UP);
    }

    public static boolean equalsAmount(BigDecimal a, BigDecimal b) {
        return normalizeAmount(a).compareTo(normalizeAmount(b)) == 0;
    }

    /** Detect accidental float usage in tests / reviews. */
    public static boolean isFloatComparisonForbidden(Object left, Object right) {
        return left instanceof Float || left instanceof Double
                || right instanceof Float || right instanceof Double;
    }
}
