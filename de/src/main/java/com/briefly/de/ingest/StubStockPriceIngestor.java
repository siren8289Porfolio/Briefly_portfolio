package com.briefly.de.ingest;

import com.briefly.de.catalog.DataSourceCatalog;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class StubStockPriceIngestor implements ExternalDataIngestor {
    private final List<Map<String, String>> rows;

    public StubStockPriceIngestor(List<Map<String, String>> rows) {
        this.rows = List.copyOf(rows);
    }

    @Override
    public DataSourceCatalog source() {
        return DataSourceCatalog.FSC_STOCK_PRICE;
    }

    @Override
    public IngestResult fetch(String baseDateYyyyMmDd, String pipelineRunId) {
        return new IngestResult(
                rows,
                source().sourceUrl(),
                Instant.parse("2026-08-22T06:00:00Z"),
                "stub-checksum"
        );
    }
}
