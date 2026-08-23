package com.briefly.de.pipeline;

import com.briefly.de.ingest.StubStockPriceIngestor;
import com.briefly.de.serve.InMemoryMarketContextService;
import com.briefly.de.serve.MarketContextService;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PipelineOrchestratorTest {
    @Test
    void run_dedupsAndPromotesWhenDqPasses() throws Exception {
        var rows = List.of(
                Map.of(
                        "basDt", "20260821",
                        "isinCd", "KR7005930003",
                        "srtnCd", "005930",
                        "clpr", "72000",
                        "mkp", "71500"
                ),
                Map.of(
                        "basDt", "20260821",
                        "isinCd", "KR7005930003",
                        "srtnCd", "005930",
                        "clpr", "72100",
                        "mkp", "71500"
                )
        );

        Clock clock = Clock.fixed(Instant.parse("2026-08-22T00:00:00Z"), ZoneOffset.UTC);
        InMemoryMarketContextService serving = new InMemoryMarketContextService(clock, 3);
        PipelineOrchestrator orchestrator = new PipelineOrchestrator(
                new StubStockPriceIngestor(rows),
                serving,
                1
        );

        PipelineOrchestrator.PipelineResult result = orchestrator.run("20260821", "run-1");

        assertEquals(2, result.rowsIn());
        assertEquals(1, result.rowsNormalized());
        assertEquals(0, result.rowsQuarantine());
        assertTrue(result.dqPassed());

        var ctx = serving.findLatest("KR7005930003");
        assertTrue(ctx.isPresent());
        assertEquals("72100.000000", ctx.get().closePricePlain());
        assertEquals(MarketContextService.FreshnessStatus.FRESH, ctx.get().freshness());
        assertTrue(ctx.get().sourceName().contains("금융위원회"));
    }

    @Test
    void run_quarantinesInvalidAndBlocksPromoteWhenSchemaPassLow() throws Exception {
        List<Map<String, String>> rows = new ArrayList<>();
        rows.add(Map.of(
                "basDt", "20260821",
                "isinCd", "KR7005930003",
                "clpr", "72000"
        ));
        rows.add(Map.of(
                "basDt", "bad-date",
                "srtnCd", "000660",
                "clpr", "x"
        ));

        InMemoryMarketContextService serving = new InMemoryMarketContextService(
                Clock.fixed(Instant.parse("2026-08-22T00:00:00Z"), ZoneOffset.UTC), 3
        );
        PipelineOrchestrator orchestrator = new PipelineOrchestrator(
                new StubStockPriceIngestor(rows),
                serving,
                1
        );

        PipelineOrchestrator.PipelineResult result = orchestrator.run("20260821", "run-2");
        assertEquals(1, result.rowsQuarantine());
        assertFalse(result.dqPassed());
        assertTrue(serving.findLatest("KR7005930003").isEmpty());
    }
}
