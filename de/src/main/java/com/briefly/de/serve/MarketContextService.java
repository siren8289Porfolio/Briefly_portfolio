package com.briefly.de.serve;

import com.briefly.de.model.MarketSnapshotRecord;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Serving contract for Brief/Risk Context.
 * Graceful degradation: last-good snapshot + freshness. Never blocks OLTP.
 * Auto risk labeling is forbidden.
 */
public interface MarketContextService {
    Optional<MarketContext> findLatest(String securityId);

    record MarketContext(
            String securityId,
            LocalDate baseDate,
            String closePricePlain,
            String currency,
            String sourceName,
            String sourceUrl,
            Instant fetchedAt,
            FreshnessStatus freshness
    ) {
        public static MarketContext from(MarketSnapshotRecord record, FreshnessStatus freshness) {
            return new MarketContext(
                    record.securityId().value(),
                    record.baseDate(),
                    record.closePrice().amount().toPlainString(),
                    record.closePrice().currency(),
                    record.provenance().sourceDataset().sourceName(),
                    record.provenance().sourceUrl(),
                    record.provenance().fetchedAt(),
                    freshness
            );
        }
    }

    enum FreshnessStatus { FRESH, STALE, UNAVAILABLE }
}
