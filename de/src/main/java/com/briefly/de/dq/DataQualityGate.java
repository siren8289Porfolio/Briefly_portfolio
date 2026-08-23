package com.briefly.de.dq;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** DQ gate before mart promotion. Evidence: DESIGNED; prod reports NOT TESTED. */
public final class DataQualityGate {
    public static final BigDecimal MARKET_COMPLETENESS_MIN = new BigDecimal("0.95");
    public static final BigDecimal MASTER_MATCH_MIN = new BigDecimal("0.98");
    public static final BigDecimal SCHEMA_PASS_MIN = new BigDecimal("0.99");
    public static final BigDecimal RECONCILE_TOLERANCE = new BigDecimal("0.001");

    public GateResult evaluate(DqMetrics metrics) {
        Map<String, MetricOutcome> outcomes = new LinkedHashMap<>();
        outcomes.put("market_snapshot_completeness",
                checkMin(metrics.marketCompleteness(), MARKET_COMPLETENESS_MIN, "market_snapshot_completeness"));
        outcomes.put("security_master_match_rate",
                checkMin(metrics.masterMatchRate(), MASTER_MATCH_MIN, "security_master_match_rate"));
        outcomes.put("schema_pass_rate",
                checkMin(metrics.schemaPassRate(), SCHEMA_PASS_MIN, "schema_pass_rate"));
        outcomes.put("disclosure_dedup",
                new MetricOutcome(
                        "disclosure_dedup",
                        BigDecimal.valueOf(metrics.disclosureDuplicateCount()),
                        BigDecimal.ZERO,
                        metrics.disclosureDuplicateCount() == 0
                ));
        outcomes.put("raw_normalized_reconcile",
                checkMaxAbs(metrics.reconcileDiffRatio(), RECONCILE_TOLERANCE, "raw_normalized_reconcile"));

        boolean passed = outcomes.values().stream().allMatch(MetricOutcome::passed);
        return new GateResult(passed, List.copyOf(outcomes.values()));
    }

    private static MetricOutcome checkMin(BigDecimal value, BigDecimal threshold, String name) {
        return new MetricOutcome(name, value, threshold, value.compareTo(threshold) >= 0);
    }

    private static MetricOutcome checkMaxAbs(BigDecimal value, BigDecimal threshold, String name) {
        return new MetricOutcome(name, value, threshold, value.abs().compareTo(threshold) <= 0);
    }

    public record DqMetrics(
            BigDecimal marketCompleteness,
            BigDecimal masterMatchRate,
            BigDecimal schemaPassRate,
            long disclosureDuplicateCount,
            BigDecimal reconcileDiffRatio
    ) {
        public static DqMetrics ofRates(
                double marketCompleteness,
                double masterMatchRate,
                double schemaPassRate,
                long disclosureDuplicateCount,
                double reconcileDiffRatio
        ) {
            return new DqMetrics(
                    scale(marketCompleteness),
                    scale(masterMatchRate),
                    scale(schemaPassRate),
                    disclosureDuplicateCount,
                    scale(reconcileDiffRatio)
            );
        }

        private static BigDecimal scale(double v) {
            return BigDecimal.valueOf(v).setScale(6, RoundingMode.HALF_UP);
        }
    }

    public record MetricOutcome(String name, BigDecimal value, BigDecimal threshold, boolean passed) {}

    public record GateResult(boolean passed, List<MetricOutcome> outcomes) {
        public List<String> failures() {
            List<String> fails = new ArrayList<>();
            for (MetricOutcome o : outcomes) {
                if (!o.passed()) {
                    fails.add(o.name() + " value=" + o.value() + " threshold=" + o.threshold());
                }
            }
            return fails;
        }
    }
}
