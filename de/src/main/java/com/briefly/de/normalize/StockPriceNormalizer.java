package com.briefly.de.normalize;

import com.briefly.de.catalog.DataSourceCatalog;
import com.briefly.de.model.MarketSnapshotRecord;
import com.briefly.de.model.Money;
import com.briefly.de.model.Provenance;
import com.briefly.de.model.SecurityId;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.Map;

public final class StockPriceNormalizer {
    private static final DateTimeFormatter BAS_DT = DateTimeFormatter.BASIC_ISO_DATE;

    public MarketSnapshotRecord normalize(
            Map<String, String> row,
            String sourceUrl,
            String pipelineRunId,
            Instant fetchedAt
    ) {
        LocalDate baseDate = LocalDate.parse(row.get("basDt").trim(), BAS_DT);
        SecurityId securityId = SecurityId.fromIsinOrSrtn(row.get("isinCd"), row.get("srtnCd"));
        Money close = Money.ofPrice(row.get("clpr"), "KRW");
        Money open = row.get("mkp") == null || row.get("mkp").isBlank()
                ? null
                : Money.ofPrice(row.get("mkp"), "KRW");

        Provenance provenance = new Provenance(
                DataSourceCatalog.FSC_STOCK_PRICE,
                sourceUrl,
                baseDate,
                fetchedAt,
                pipelineRunId,
                sha256(row.toString()),
                "v1"
        );
        return new MarketSnapshotRecord(securityId, baseDate, close, open, provenance);
    }

    static String sha256(String payload) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
