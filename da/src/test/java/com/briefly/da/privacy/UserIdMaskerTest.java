package com.briefly.da.privacy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class UserIdMaskerTest {
    @Test
    void hashIsDeterministicAnd64HexChars() {
        UserIdMasker masker = new UserIdMasker("briefly-analytics");
        String hash1 = masker.hashUserId(100L);
        String hash2 = masker.hashUserId(100L);
        assertEquals(hash1, hash2);
        assertEquals(64, hash1.length());
    }

    @Test
    void differentUsersProduceDifferentHashes() {
        UserIdMasker masker = new UserIdMasker("briefly-analytics");
        assertNotEquals(masker.hashUserId(1L), masker.hashUserId(2L));
    }
}
