package com.briefly.da.kpi;

import com.briefly.da.event.AnalyticsEvent;
import com.briefly.da.event.AnalyticsEventType;
import com.briefly.da.privacy.UserIdMasker;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KpiCalculatorTest {
    private static final String USER_A = new UserIdMasker("test-salt").hashUserId(1L);
    private static final String USER_B = new UserIdMasker("test-salt").hashUserId(2L);
    private static final LocalDate DATE = LocalDate.of(2026, 8, 21);

    private final KpiCalculator calculator = new KpiCalculator();

    @Test
    void interestMockJoinConversion_computesDistinctUsers() {
        List<AnalyticsEvent> events = List.of(
                event("e1", AnalyticsEventType.PRODUCT_INTEREST_ADD, USER_A, 1L),
                event("e2", AnalyticsEventType.PRODUCT_INTEREST_ADD, USER_B, 1L),
                event("e3", AnalyticsEventType.MOCK_JOIN_SUBMIT, USER_A, 1L)
        );

        MetricSnapshot snapshot = calculator.interestMockJoinConversion(DATE, events, 1L);

        assertEquals(KpiDefinition.INTEREST_MOCKJOIN_CVR, snapshot.definition());
        assertEquals(2, snapshot.denominator());
        assertEquals(1, snapshot.numerator());
        assertEquals(new BigDecimal("0.500000"), snapshot.value());
    }

    @Test
    void interestMockJoinConversion_returnsNullWhenNoInterests() {
        MetricSnapshot snapshot = calculator.interestMockJoinConversion(DATE, List.of(), 1L);
        assertNull(snapshot.value());
    }

    @Test
    void marketDataCoverage_handlesZeroActiveProducts() {
        MetricSnapshot snapshot = calculator.marketDataCoverage(DATE, 0, 5);
        assertNull(snapshot.value());
    }

    private static AnalyticsEvent event(
            String id,
            AnalyticsEventType type,
            String userHash,
            Long productId
    ) {
        return new AnalyticsEvent(
                id,
                type,
                DATE.atStartOfDay().toInstant(ZoneOffset.UTC),
                userHash,
                productId,
                null,
                false,
                type == AnalyticsEventType.MOCK_JOIN_SUBMIT ? new BigDecimal("10000") : null,
                null
        );
    }
}
