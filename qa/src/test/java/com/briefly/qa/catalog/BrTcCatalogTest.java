package com.briefly.qa.catalog;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BrTcCatalogTest {

    @Test
    void catalogHasFifteenCases() {
        assertEquals(15, BrTcCatalog.all().size());
        assertTrue(BrTcCatalog.p0().size() >= 12);
        assertTrue(BrTcCatalog.find("BR-TC-001").isPresent());
    }

    @Test
    void noPassedWithoutEvidenceYet() {
        assertEquals(15, BrTcCatalog.designedCount());
        assertFalse(BrTcCatalog.hasPassedWithoutEvidence());
    }
}
