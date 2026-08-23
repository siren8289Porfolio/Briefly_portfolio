package com.briefly.qa.domain;

import java.util.EnumSet;
import java.util.Set;

/**
 * Application status machine oracle (POL / BR-TC-004, BR-TC-014).
 * New applications must start as PENDING.
 */
public final class ApplicationStatusMachine {

    public enum Status {
        PENDING, APPROVED, REJECTED, CANCELED
    }

    private static final Set<Status> FROM_PENDING = EnumSet.of(
            Status.APPROVED, Status.REJECTED, Status.CANCELED
    );

    private ApplicationStatusMachine() {}

    public static Status initialOnCreate() {
        return Status.PENDING;
    }

    public static boolean canTransition(Status from, Status to) {
        if (from == null || to == null) {
            return false;
        }
        if (from == Status.PENDING) {
            return FROM_PENDING.contains(to);
        }
        return false;
    }

    public static void assertTransition(Status from, Status to) {
        if (!canTransition(from, to)) {
            throw new IllegalStateException("illegal transition: " + from + " → " + to);
        }
    }
}
