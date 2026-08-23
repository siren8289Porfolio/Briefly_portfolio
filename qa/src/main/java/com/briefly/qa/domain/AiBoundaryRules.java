package com.briefly.qa.domain;

/**
 * MVP AI boundary (BR-TC-011).
 * No personalized recommendation / auto approval in product paths.
 */
public final class AiBoundaryRules {

    private AiBoundaryRules() {}

    public static boolean personalizedRecommendationAllowedInMvp() {
        return false;
    }

    public static boolean autoRiskAlertFromDisclosureAllowedInMvp() {
        return false;
    }

    public static boolean autoApproveApplicationAllowedInMvp() {
        return false;
    }
}
