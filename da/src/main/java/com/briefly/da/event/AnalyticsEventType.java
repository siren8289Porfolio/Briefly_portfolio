package com.briefly.da.event;

/** GA4-style analytics events. Evidence: DESIGNED. */
public enum AnalyticsEventType {
    PRODUCT_INTEREST_ADD("product_interest_add"),
    MOCK_JOIN_SUBMIT("mock_join_submit"),
    BRIEF_VIEW("brief_view"),
    RISK_ALERT_VIEW("risk_alert_view"),
    INTEREST_REMOVE("interest_remove"),
    MARKET_CONTEXT_VIEW("market_context_view");

    private final String eventName;

    AnalyticsEventType(String eventName) {
        this.eventName = eventName;
    }

    public String eventName() {
        return eventName;
    }

    public static AnalyticsEventType fromName(String name) {
        for (AnalyticsEventType type : values()) {
            if (type.eventName.equals(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown event: " + name);
    }
}
