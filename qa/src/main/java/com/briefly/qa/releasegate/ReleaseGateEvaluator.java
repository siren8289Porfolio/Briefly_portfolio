package com.briefly.qa.releasegate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Release gate evaluation helper (checklist automation stub). */
public final class ReleaseGateEvaluator {

    public record CheckItem(String code, boolean passed, String note) {}

    public record GateResult(boolean passed, List<CheckItem> items) {}

    public GateResult evaluate(
            boolean p0CoverageComplete,
            int openCritical,
            int openHigh,
            boolean securitySuitePassed,
            boolean rollbackDrillPassed,
            boolean evidencePackComplete,
            boolean aiBoundaryConfirmed
    ) {
        List<CheckItem> items = new ArrayList<>();
        items.add(new CheckItem("P0_COVERAGE", p0CoverageComplete, "P0/P1 FR coverage"));
        items.add(new CheckItem("NO_CRITICAL", openCritical == 0, "open critical=" + openCritical));
        items.add(new CheckItem("NO_HIGH", openHigh == 0, "open high=" + openHigh));
        items.add(new CheckItem("SECURITY", securitySuitePassed, "Session/CSRF/XSS/SQLi"));
        items.add(new CheckItem("ROLLBACK", rollbackDrillPassed, "rollback drill"));
        items.add(new CheckItem("EVIDENCE", evidencePackComplete, "run_id + artifacts"));
        items.add(new CheckItem("AI_BOUNDARY", aiBoundaryConfirmed, "BR-TC-011"));

        boolean passed = items.stream().allMatch(CheckItem::passed);
        return new GateResult(passed, List.copyOf(items));
    }

    public static void requireRunId(String runId) {
        Objects.requireNonNull(runId, "run_id required for PASS evidence");
        if (runId.isBlank()) {
            throw new IllegalArgumentException("run_id required for PASS evidence");
        }
    }
}
