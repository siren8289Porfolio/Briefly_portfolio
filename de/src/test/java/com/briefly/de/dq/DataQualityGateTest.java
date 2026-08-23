package com.briefly.de.dq;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataQualityGateTest {
    private final DataQualityGate gate = new DataQualityGate();

    @Test
    void passesWhenAllThresholdsMet() {
        var result = gate.evaluate(DataQualityGate.DqMetrics.ofRates(
                0.96, 0.99, 0.995, 0, 0.0005
        ));
        assertTrue(result.passed());
        assertTrue(result.failures().isEmpty());
    }

    @Test
    void failsOnLowCompletenessOrDisclosureDup() {
        assertFalse(gate.evaluate(DataQualityGate.DqMetrics.ofRates(
                0.90, 0.99, 0.995, 0, 0.0
        )).passed());
        assertFalse(gate.evaluate(DataQualityGate.DqMetrics.ofRates(
                0.96, 0.99, 0.995, 1, 0.0
        )).passed());
    }
}
