package com.briefly.de.model;

import java.util.Objects;
import java.util.Optional;

/** Canonical security id: ISIN preferred. */
public final class SecurityId {
    public enum Kind { ISIN, SRTN_CD }

    private final String value;
    private final Kind kind;

    private SecurityId(String value, Kind kind) {
        this.value = value;
        this.kind = kind;
    }

    public static SecurityId fromIsinOrSrtn(String isin, String srtnCd) {
        if (isin != null && !isin.isBlank()) {
            String normalized = isin.trim().toUpperCase();
            if (normalized.length() != 12) {
                throw new IllegalArgumentException("ISIN must be 12 characters");
            }
            return new SecurityId(normalized, Kind.ISIN);
        }
        if (srtnCd != null && !srtnCd.isBlank()) {
            return new SecurityId("SRTN:" + srtnCd.trim(), Kind.SRTN_CD);
        }
        throw new IllegalArgumentException("isin or srtnCd required");
    }

    public String value() { return value; }
    public Kind kind() { return kind; }
    public Optional<String> isin() {
        return kind == Kind.ISIN ? Optional.of(value) : Optional.empty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SecurityId that)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() { return Objects.hash(value); }
}
