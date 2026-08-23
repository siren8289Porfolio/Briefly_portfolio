package com.briefly.de.ingest;

import com.briefly.de.catalog.DataSourceCatalog;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/** Ingestion port. Live HTTP: PLANNED / NOT TESTED. Must not mutate OLTP. */
public interface ExternalDataIngestor {
    DataSourceCatalog source();

    IngestResult fetch(String baseDateYyyyMmDd, String pipelineRunId) throws IngestException;

    record IngestResult(
            List<Map<String, String>> rows,
            String sourceUrl,
            Instant fetchedAt,
            String rawPayloadChecksum
    ) {}

    class IngestException extends Exception {
        public IngestException(String message) { super(message); }
        public IngestException(String message, Throwable cause) { super(message, cause); }
    }
}
