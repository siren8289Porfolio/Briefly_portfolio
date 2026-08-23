package com.briefly.de.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityIdTest {
    @Test
    void prefersIsinAsCanonical() {
        SecurityId id = SecurityId.fromIsinOrSrtn("KR7005930003", "005930");
        assertEquals("KR7005930003", id.value());
        assertEquals(SecurityId.Kind.ISIN, id.kind());
        assertTrue(id.isin().isPresent());
    }

    @Test
    void fallsBackToSrtn() {
        SecurityId id = SecurityId.fromIsinOrSrtn(null, "005930");
        assertEquals("SRTN:005930", id.value());
        assertEquals(SecurityId.Kind.SRTN_CD, id.kind());
    }

    @Test
    void rejectsInvalidIsinLength() {
        assertThrows(IllegalArgumentException.class,
                () -> SecurityId.fromIsinOrSrtn("SHORT", null));
    }
}
