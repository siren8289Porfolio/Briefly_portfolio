package com.briefly.qa.releasegate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReleaseGateEvaluatorTest {

    private final ReleaseGateEvaluator evaluator = new ReleaseGateEvaluator();

    @Test
    void passesOnlyWhenAllChecksGreen() {
        var result = evaluator.evaluate(true, 0, 0, true, true, true, true);
        assertTrue(result.passed());
    }

    @Test
    void failsWhenHighDefectOpen() {
        var result = evaluator.evaluate(true, 0, 1, true, true, true, true);
        assertFalse(result.passed());
    }

    @Test
    void requireRunIdForPassEvidence() {
        assertThrows(IllegalArgumentException.class, () -> ReleaseGateEvaluator.requireRunId(" "));
        ReleaseGateEvaluator.requireRunId("qa-20260823-001");
    }
}
