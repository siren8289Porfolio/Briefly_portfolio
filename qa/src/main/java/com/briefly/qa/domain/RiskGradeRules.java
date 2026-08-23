package com.briefly.qa.domain;

/** Risk grade must be integer 1..5 (BR-TC-013). */
public final class RiskGradeRules {

    private RiskGradeRules() {}

    public static boolean isValid(int riskGrade) {
        return riskGrade >= 1 && riskGrade <= 5;
    }

    public static void assertValid(int riskGrade) {
        if (!isValid(riskGrade)) {
            throw new IllegalArgumentException("risk grade must be 1..5, got " + riskGrade);
        }
    }
}
