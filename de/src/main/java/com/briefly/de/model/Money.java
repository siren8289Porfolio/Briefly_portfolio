package com.briefly.de.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/** Price/amount with explicit scale. Float/double forbidden. */
public final class Money {
    public static final int PRICE_SCALE = 6;
    public static final int AMOUNT_SCALE = 2;

    private final BigDecimal amount;
    private final String currency;

    private Money(BigDecimal amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public static Money ofPrice(String raw, String currency) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("price is required");
        }
        BigDecimal value = new BigDecimal(raw.trim()).setScale(PRICE_SCALE, RoundingMode.HALF_UP);
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("price must be >= 0");
        }
        return new Money(value, requireCurrency(currency));
    }

    public static Money ofAmount(String raw, String currency) {
        BigDecimal value = new BigDecimal(raw.trim()).setScale(AMOUNT_SCALE, RoundingMode.HALF_UP);
        return new Money(value, requireCurrency(currency));
    }

    private static String requireCurrency(String currency) {
        if (currency == null || currency.length() != 3) {
            throw new IllegalArgumentException("currency must be ISO-4217");
        }
        return currency.toUpperCase();
    }

    public BigDecimal amount() { return amount; }
    public String currency() { return currency; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money money)) return false;
        return amount.compareTo(money.amount) == 0 && currency.equals(money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount.stripTrailingZeros(), currency);
    }
}
