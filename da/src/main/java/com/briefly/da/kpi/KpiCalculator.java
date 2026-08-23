package com.briefly.da.kpi;

import com.briefly.da.event.AnalyticsEvent;
import com.briefly.da.event.AnalyticsEventType;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Computes KPIs from validated events. Reproducible for same input. */
public final class KpiCalculator {

    public MetricSnapshot interestMockJoinConversion(LocalDate date, List<AnalyticsEvent> events, Long productId) {
        List<AnalyticsEvent> dayEvents = filterByDateAndProduct(events, date, productId);
        long interests = countDistinctUsers(dayEvents, AnalyticsEventType.PRODUCT_INTEREST_ADD);
        long mockJoins = countDistinctUsers(dayEvents, AnalyticsEventType.MOCK_JOIN_SUBMIT);
        return new MetricSnapshot(
                KpiDefinition.INTEREST_MOCKJOIN_CVR,
                date,
                productId,
                "all",
                KpiDefinition.ratio(mockJoins, interests, 6),
                mockJoins,
                interests
        );
    }

    public MetricSnapshot briefViewRate(LocalDate date, List<AnalyticsEvent> events) {
        List<AnalyticsEvent> dayEvents = filterByDate(events, date);
        long briefViews = dayEvents.stream()
                .filter(e -> e.type() == AnalyticsEventType.BRIEF_VIEW)
                .count();
        long activeUsers = dayEvents.stream()
                .map(AnalyticsEvent::userIdHash)
                .collect(Collectors.toSet())
                .size();
        return new MetricSnapshot(
                KpiDefinition.BRIEF_VIEW_RATE,
                date,
                null,
                "all",
                KpiDefinition.ratio(briefViews, activeUsers, 6),
                briefViews,
                activeUsers
        );
    }

    public MetricSnapshot marketDataCoverage(LocalDate date, long activeProducts, long productsWithPrice) {
        return new MetricSnapshot(
                KpiDefinition.MARKET_DATA_COVERAGE,
                date,
                null,
                "all",
                KpiDefinition.ratio(productsWithPrice, activeProducts, 6),
                productsWithPrice,
                activeProducts
        );
    }

    public MetricSnapshot briefEvidenceCoverage(
            LocalDate weekEnding,
            long briefsWithExternal,
            long totalBriefs
    ) {
        return new MetricSnapshot(
                KpiDefinition.BRIEF_EVIDENCE_COVERAGE,
                weekEnding,
                null,
                "all",
                KpiDefinition.ratio(briefsWithExternal, totalBriefs, 6),
                briefsWithExternal,
                totalBriefs
        );
    }

    public MetricSnapshot riskSignalReviewRate(LocalDate weekEnding, long reviewed, long generated) {
        return new MetricSnapshot(
                KpiDefinition.RISK_SIGNAL_REVIEW_RATE,
                weekEnding,
                null,
                "all",
                KpiDefinition.ratio(reviewed, generated, 6),
                reviewed,
                generated
        );
    }

    private static List<AnalyticsEvent> filterByDate(List<AnalyticsEvent> events, LocalDate date) {
        return events.stream()
                .filter(e -> LocalDate.ofInstant(e.eventTs(), ZoneOffset.UTC).equals(date))
                .toList();
    }

    private static List<AnalyticsEvent> filterByDateAndProduct(
            List<AnalyticsEvent> events,
            LocalDate date,
            Long productId
    ) {
        return events.stream()
                .filter(e -> LocalDate.ofInstant(e.eventTs(), ZoneOffset.UTC).equals(date))
                .filter(e -> productId == null || productId.equals(e.productId()))
                .toList();
    }

    private static long countDistinctUsers(List<AnalyticsEvent> events, AnalyticsEventType type) {
        Set<String> users = events.stream()
                .filter(e -> e.type() == type)
                .map(AnalyticsEvent::userIdHash)
                .collect(Collectors.toSet());
        return users.size();
    }
}
