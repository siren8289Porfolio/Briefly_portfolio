package com.briefly.da.kpi;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** KPI definitions with versioned formulas. Evidence: DESIGNED. */
public enum KpiDefinition {
    INTEREST_MOCKJOIN_CVR(
            "interest_mockjoin_cvr",
            "1.0.0",
            "mock_joins / interests",
            "Product"
    ),
    BRIEF_VIEW_RATE(
            "brief_view_rate",
            "1.0.0",
            "brief_views / active_sessions",
            "Product"
    ),
    ALERT_CONVERSION(
            "alert_conversion",
            "1.0.0",
            "post_alert_actions / alerts_sent",
            "Ops / Product"
    ),
    MARKET_DATA_COVERAGE(
            "market_data_coverage",
            "1.0.0",
            "products_with_price / active_products",
            "DE"
    ),
    BRIEF_EVIDENCE_COVERAGE(
            "brief_evidence_coverage",
            "1.0.0",
            "briefs_with_external / total_briefs",
            "Product"
    ),
    RISK_SIGNAL_REVIEW_RATE(
            "risk_signal_review_rate",
            "1.0.0",
            "reviewed / generated",
            "Ops"
    );

    private final String code;
    private final String formulaVersion;
    private final String formulaText;
    private final String owner;

    KpiDefinition(String code, String formulaVersion, String formulaText, String owner) {
        this.code = code;
        this.formulaVersion = formulaVersion;
        this.formulaText = formulaText;
        this.owner = owner;
    }

    public String code() { return code; }
    public String formulaVersion() { return formulaVersion; }
    public String formulaText() { return formulaText; }
    public String owner() { return owner; }

    /** Safe ratio: denominator zero → null (N/A). */
    public static BigDecimal ratio(long numerator, long denominator, int scale) {
        if (denominator == 0) {
            return null;
        }
        return BigDecimal.valueOf(numerator)
                .divide(BigDecimal.valueOf(denominator), scale, RoundingMode.HALF_UP);
    }
}
