package com.briefly.da.kpi;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public record MetricSnapshot(
        KpiDefinition definition,
        LocalDate metricDate,
        Long productId,
        String segment,
        BigDecimal value,
        long numerator,
        long denominator
) {
    public MetricSnapshot {
        Objects.requireNonNull(definition);
        Objects.requireNonNull(metricDate);
    }

    public boolean isAvailable() {
        return value != null;
    }
}
