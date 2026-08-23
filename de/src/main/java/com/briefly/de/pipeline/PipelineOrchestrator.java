package com.briefly.de.pipeline;

import com.briefly.de.dq.DataQualityGate;
import com.briefly.de.ingest.ExternalDataIngestor;
import com.briefly.de.model.MarketSnapshotRecord;
import com.briefly.de.normalize.StockPriceNormalizer;
import com.briefly.de.serve.InMemoryMarketContextService;
import com.briefly.de.validate.StockPriceRowValidator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ingest → validate → normalize → DQ → serve.
 * Schedule wiring: PLANNED. Does not touch OLTP tables.
 */
public final class PipelineOrchestrator {
    private final ExternalDataIngestor ingestor;
    private final StockPriceRowValidator validator = new StockPriceRowValidator();
    private final StockPriceNormalizer normalizer = new StockPriceNormalizer();
    private final DataQualityGate dqGate = new DataQualityGate();
    private final InMemoryMarketContextService marketContextService;
    private final int activeProductCount;

    public PipelineOrchestrator(
            ExternalDataIngestor ingestor,
            InMemoryMarketContextService marketContextService,
            int activeProductCount
    ) {
        this.ingestor = ingestor;
        this.marketContextService = marketContextService;
        this.activeProductCount = activeProductCount;
    }

    public PipelineResult run(String baseDateYyyyMmDd, String pipelineRunId)
            throws ExternalDataIngestor.IngestException {
        ExternalDataIngestor.IngestResult ingested = ingestor.fetch(baseDateYyyyMmDd, pipelineRunId);

        List<Map<String, String>> quarantined = new ArrayList<>();
        Map<String, MarketSnapshotRecord> dedup = new LinkedHashMap<>();
        int normalizedBeforeDedup = 0;

        for (Map<String, String> row : ingested.rows()) {
            StockPriceRowValidator.ValidationResult vr = validator.validate(row);
            if (!vr.passed()) {
                quarantined.add(row);
                continue;
            }
            MarketSnapshotRecord record = normalizer.normalize(
                    row, ingested.sourceUrl(), pipelineRunId, ingested.fetchedAt()
            );
            normalizedBeforeDedup++;
            dedup.put(record.businessKey(), record);
        }
        List<MarketSnapshotRecord> normalized = List.copyOf(dedup.values());

        int validCount = ingested.rows().size() - quarantined.size();
        double schemaPass = ingested.rows().isEmpty()
                ? 0.0
                : (double) validCount / ingested.rows().size();
        double completeness = activeProductCount <= 0
                ? 0.0
                : Math.min(1.0, (double) normalized.size() / activeProductCount);
        // Reconcile valid raw → normalized (pre-dedup). Dedup is intentional, not loss.
        double reconcile = ingested.rows().isEmpty()
                ? 0.0
                : Math.abs(validCount - normalizedBeforeDedup)
                / (double) Math.max(ingested.rows().size(), 1);

        DataQualityGate.GateResult gate = dqGate.evaluate(DataQualityGate.DqMetrics.ofRates(
                completeness, 1.0, schemaPass, 0, reconcile
        ));

        if (gate.passed()) {
            for (MarketSnapshotRecord record : normalized) {
                marketContextService.put(record);
            }
        }

        return new PipelineResult(
                pipelineRunId,
                ingested.rows().size(),
                normalized.size(),
                quarantined.size(),
                gate.passed(),
                gate.failures(),
                BigDecimal.valueOf(schemaPass).setScale(6, RoundingMode.HALF_UP)
        );
    }

    public record PipelineResult(
            String pipelineRunId,
            int rowsIn,
            int rowsNormalized,
            int rowsQuarantine,
            boolean dqPassed,
            List<String> dqFailures,
            BigDecimal schemaPassRate
    ) {}
}
