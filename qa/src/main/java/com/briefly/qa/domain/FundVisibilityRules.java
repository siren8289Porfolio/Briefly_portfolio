package com.briefly.qa.domain;

/** Fund list visibility (BR-TC-015). */
public final class FundVisibilityRules {

    public enum Status {
        ACTIVE, INACTIVE
    }

    private FundVisibilityRules() {}

    public static boolean visibleInPublicList(Status status) {
        return status == Status.ACTIVE;
    }
}
