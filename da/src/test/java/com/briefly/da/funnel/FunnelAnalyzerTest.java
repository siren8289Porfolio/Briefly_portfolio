package com.briefly.da.funnel;

import com.briefly.da.event.AnalyticsEvent;
import com.briefly.da.event.AnalyticsEventType;
import com.briefly.da.privacy.UserIdMasker;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FunnelAnalyzerTest {
    private static final String USER = new UserIdMasker("salt").hashUserId(42L);

    @Test
    void analyze_countsUsersPerStage() {
        List<AnalyticsEvent> events = List.of(
                new AnalyticsEvent(
                        "1", AnalyticsEventType.PRODUCT_INTEREST_ADD,
                        Instant.parse("2026-08-21T09:00:00Z"), USER, 1L, null, false, null, null
                ),
                new AnalyticsEvent(
                        "2", AnalyticsEventType.MOCK_JOIN_SUBMIT,
                        Instant.parse("2026-08-21T10:00:00Z"), USER, 1L, null, false,
                        new BigDecimal("5000"), null
                ),
                new AnalyticsEvent(
                        "3", AnalyticsEventType.BRIEF_VIEW,
                        Instant.parse("2026-08-21T11:00:00Z"), USER, 1L, null, true, null, null
                )
        );

        FunnelAnalyzer.FunnelResult result = new FunnelAnalyzer().analyze(events);

        assertEquals(1, result.exploreUsers());
        assertEquals(1, result.interestUsers());
        assertEquals(1, result.mockJoinUsers());
        assertEquals(1, result.briefUsers());
        assertEquals(1.0, result.stageRate(FunnelStage.INTEREST, FunnelStage.MOCK_JOIN));
    }
}
