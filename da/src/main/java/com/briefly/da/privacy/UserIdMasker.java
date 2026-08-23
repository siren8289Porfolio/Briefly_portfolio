package com.briefly.da.privacy;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/** Masks raw user_id for analytics (SHA-256 + salt). PII separation. */
public final class UserIdMasker {
    private final String salt;

    public UserIdMasker(String salt) {
        if (salt == null || salt.isBlank()) {
            throw new IllegalArgumentException("salt required");
        }
        this.salt = salt;
    }

    public String hashUserId(long userId) {
        return sha256(salt + ":" + userId);
    }

    public String hashUserId(String userId) {
        return sha256(salt + ":" + userId);
    }

    private static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
