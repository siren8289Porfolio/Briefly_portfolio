package com.briefly.da.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public record AnalyticsEvent(
        String eventId,
        AnalyticsEventType type,
        Instant eventTs,
        String userIdHash,
        Long productId,
        String securityId,
        boolean hasMarketContext,
        BigDecimal amount,
        String reason
) {
    public AnalyticsEvent {
        Objects.requireNonNull(eventId, "eventId");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(eventTs, "eventTs");
        Objects.requireNonNull(userIdHash, "userIdHash");
    }

    public String contextSegment() {
        if (type == AnalyticsEventType.BRIEF_VIEW || type == AnalyticsEventType.MARKET_CONTEXT_VIEW) {
            return hasMarketContext ? "context_linked" : "context_unlinked";
        }
        if (securityId == null || securityId.isBlank()) {
            return "security_unknown";
        }
        return "context_linked";
    }
}
