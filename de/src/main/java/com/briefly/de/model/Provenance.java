package com.briefly.de.model;

import com.briefly.de.catalog.DataSourceCatalog;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

public final class Provenance {
    private final DataSourceCatalog sourceDataset;
    private final String sourceUrl;
    private final LocalDate baseDate;
    private final Instant fetchedAt;
    private final String pipelineRunId;
    private final String checksum;
    private final String schemaVersion;

    public Provenance(
            DataSourceCatalog sourceDataset,
            String sourceUrl,
            LocalDate baseDate,
            Instant fetchedAt,
            String pipelineRunId,
            String checksum,
            String schemaVersion
    ) {
        this.sourceDataset = Objects.requireNonNull(sourceDataset);
        this.sourceUrl = Objects.requireNonNull(sourceUrl);
        this.baseDate = baseDate;
        this.fetchedAt = Objects.requireNonNull(fetchedAt);
        this.pipelineRunId = Objects.requireNonNull(pipelineRunId);
        this.checksum = Objects.requireNonNull(checksum);
        this.schemaVersion = Objects.requireNonNull(schemaVersion);
    }

    public DataSourceCatalog sourceDataset() { return sourceDataset; }
    public String sourceUrl() { return sourceUrl; }
    public LocalDate baseDate() { return baseDate; }
    public Instant fetchedAt() { return fetchedAt; }
    public String pipelineRunId() { return pipelineRunId; }
    public String checksum() { return checksum; }
    public String schemaVersion() { return schemaVersion; }
}
