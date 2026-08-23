package com.briefly.qa.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationStatusMachineTest {

    @Test
    void createStartsPending_brTc014() {
        assertEquals(ApplicationStatusMachine.Status.PENDING, ApplicationStatusMachine.initialOnCreate());
    }

    @Test
    void allowsFromPending_brTc004() {
        assertTrue(ApplicationStatusMachine.canTransition(
                ApplicationStatusMachine.Status.PENDING,
                ApplicationStatusMachine.Status.APPROVED));
        assertTrue(ApplicationStatusMachine.canTransition(
                ApplicationStatusMachine.Status.PENDING,
                ApplicationStatusMachine.Status.REJECTED));
        assertTrue(ApplicationStatusMachine.canTransition(
                ApplicationStatusMachine.Status.PENDING,
                ApplicationStatusMachine.Status.CANCELED));
    }

    @Test
    void rejectsFromRejectedToApproved_brTc004() {
        assertFalse(ApplicationStatusMachine.canTransition(
                ApplicationStatusMachine.Status.REJECTED,
                ApplicationStatusMachine.Status.APPROVED));
        assertThrows(IllegalStateException.class, () ->
                ApplicationStatusMachine.assertTransition(
                        ApplicationStatusMachine.Status.REJECTED,
                        ApplicationStatusMachine.Status.APPROVED));
    }
}
