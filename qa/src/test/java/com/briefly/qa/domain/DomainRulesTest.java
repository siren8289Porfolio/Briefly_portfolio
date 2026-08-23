package com.briefly.qa.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DomainRulesTest {

    @Test
    void riskGradeBounds_brTc013() {
        assertTrue(RiskGradeRules.isValid(1));
        assertTrue(RiskGradeRules.isValid(5));
        assertFalse(RiskGradeRules.isValid(0));
        assertFalse(RiskGradeRules.isValid(6));
        assertThrows(IllegalArgumentException.class, () -> RiskGradeRules.assertValid(0));
    }

    @Test
    void moneyPrecisionUsesBigDecimal_brTc009() {
        BigDecimal normalized = MoneyPrecisionRules.normalizeAmount(new BigDecimal("1000.555"));
        assertEquals(new BigDecimal("1000.56"), normalized);
        assertTrue(MoneyPrecisionRules.equalsAmount(
                new BigDecimal("10.00"), new BigDecimal("10.0")));
        assertTrue(MoneyPrecisionRules.isFloatComparisonForbidden(1.0d, 1.0d));
        assertFalse(MoneyPrecisionRules.isFloatComparisonForbidden(
                new BigDecimal("1.0"), new BigDecimal("1.0")));
    }

    @Test
    void inactiveFundHidden_brTc015() {
        assertTrue(FundVisibilityRules.visibleInPublicList(FundVisibilityRules.Status.ACTIVE));
        assertFalse(FundVisibilityRules.visibleInPublicList(FundVisibilityRules.Status.INACTIVE));
    }

    @Test
    void aiBoundaryMvp_brTc011() {
        assertFalse(AiBoundaryRules.personalizedRecommendationAllowedInMvp());
        assertFalse(AiBoundaryRules.autoRiskAlertFromDisclosureAllowedInMvp());
        assertFalse(AiBoundaryRules.autoApproveApplicationAllowedInMvp());
    }
}
