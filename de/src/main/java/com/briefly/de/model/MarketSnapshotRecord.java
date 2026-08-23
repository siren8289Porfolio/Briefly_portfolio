package com.briefly.de.model;

import java.time.LocalDate;
import java.util.Objects;

public final class MarketSnapshotRecord {
    private final SecurityId securityId;
    private final LocalDate baseDate;
    private final Money closePrice;
    private final Money openPrice;
    private final Provenance provenance;

    public MarketSnapshotRecord(
            SecurityId securityId,
            LocalDate baseDate,
            Money closePrice,
            Money openPrice,
            Provenance provenance
    ) {
        this.securityId = Objects.requireNonNull(securityId);
        this.baseDate = Objects.requireNonNull(baseDate);
        this.closePrice = Objects.requireNonNull(closePrice);
        this.openPrice = openPrice;
        this.provenance = Objects.requireNonNull(provenance);
        if (provenance.baseDate() != null && !baseDate.equals(provenance.baseDate())) {
            throw new IllegalArgumentException("baseDate must match provenance.baseDate");
        }
    }

    public SecurityId securityId() { return securityId; }
    public LocalDate baseDate() { return baseDate; }
    public Money closePrice() { return closePrice; }
    public Money openPrice() { return openPrice; }
    public Provenance provenance() { return provenance; }
    public String businessKey() { return securityId.value() + "|" + baseDate; }
}
