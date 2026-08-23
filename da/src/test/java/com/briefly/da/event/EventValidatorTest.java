package com.briefly.da.event;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventValidatorTest {
    private static final String VALID_HASH = "a".repeat(64);
    private final EventValidator validator = new EventValidator();

    @Test
    void acceptsValidInterestAdd() {
        var result = validator.validate(new AnalyticsEvent(
                "evt-1",
                AnalyticsEventType.PRODUCT_INTEREST_ADD,
                Instant.parse("2026-08-21T10:00:00Z"),
                VALID_HASH,
                1L,
                "KR7005930003",
                false,
                null,
                null
        ));
        assertTrue(result.passed());
    }

    @Test
    void rejectsMockJoinWithoutAmount() {
        var result = validator.validate(new AnalyticsEvent(
                "evt-2",
                AnalyticsEventType.MOCK_JOIN_SUBMIT,
                Instant.parse("2026-08-21T10:00:00Z"),
                VALID_HASH,
                1L,
                null,
                false,
                null,
                null
        ));
        assertFalse(result.passed());
    }

    @Test
    void briefViewContextSegment() {
        var linked = new AnalyticsEvent(
                "evt-3",
                AnalyticsEventType.BRIEF_VIEW,
                Instant.now(),
                VALID_HASH,
                1L,
                "KR7005930003",
                true,
                null,
                null
        );
        assertEquals("context_linked", linked.contextSegment());

        var unlinked = new AnalyticsEvent(
                "evt-4",
                AnalyticsEventType.BRIEF_VIEW,
                Instant.now(),
                VALID_HASH,
                1L,
                null,
                false,
                null,
                null
        );
        assertEquals("context_unlinked", unlinked.contextSegment());
    }

    private static void assertEquals(String expected, String actual) {
        org.junit.jupiter.api.Assertions.assertEquals(expected, actual);
    }
}
