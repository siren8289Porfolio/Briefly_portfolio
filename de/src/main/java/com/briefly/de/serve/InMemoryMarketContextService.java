package com.briefly.de.serve;

import com.briefly.de.model.MarketSnapshotRecord;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryMarketContextService implements MarketContextService {
    private final Map<String, MarketSnapshotRecord> lastGood = new ConcurrentHashMap<>();
    private final Clock clock;
    private final int staleAfterBusinessDays;

    public InMemoryMarketContextService(Clock clock, int staleAfterBusinessDays) {
        this.clock = clock;
        this.staleAfterBusinessDays = staleAfterBusinessDays;
    }

    public void put(MarketSnapshotRecord record) {
        lastGood.put(record.securityId().value(), record);
    }

    @Override
    public Optional<MarketContext> findLatest(String securityId) {
        MarketSnapshotRecord record = lastGood.get(securityId);
        if (record == null) {
            return Optional.empty();
        }
        long days = ChronoUnit.DAYS.between(record.baseDate(), LocalDate.now(clock));
        FreshnessStatus freshness = days <= staleAfterBusinessDays
                ? FreshnessStatus.FRESH
                : FreshnessStatus.STALE;
        return Optional.of(MarketContext.from(record, freshness));
    }
}
