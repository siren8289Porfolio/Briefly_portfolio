package com.briefly.de.validate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class StockPriceRowValidator {
    private static final DateTimeFormatter BAS_DT = DateTimeFormatter.BASIC_ISO_DATE;

    public ValidationResult validate(Map<String, String> row) {
        List<String> errors = new ArrayList<>();
        require(row, "basDt", errors);
        requireAny(row, errors, "srtnCd", "isinCd");
        require(row, "clpr", errors);

        if (row.get("basDt") != null && !row.get("basDt").isBlank()) {
            try {
                LocalDate.parse(row.get("basDt").trim(), BAS_DT);
            } catch (DateTimeParseException e) {
                errors.add("basDt invalid format (expected yyyyMMdd)");
            }
        }
        if (row.get("clpr") != null && !row.get("clpr").isBlank()) {
            try {
                if (new BigDecimal(row.get("clpr").trim()).signum() < 0) {
                    errors.add("clpr must be >= 0");
                }
            } catch (NumberFormatException e) {
                errors.add("clpr not numeric");
            }
        }
        return errors.isEmpty() ? ValidationResult.ok() : ValidationResult.fail(errors);
    }

    private static void require(Map<String, String> row, String key, List<String> errors) {
        String v = row.get(key);
        if (v == null || v.isBlank()) {
            errors.add(key + " required");
        }
    }

    private static void requireAny(Map<String, String> row, List<String> errors, String... keys) {
        for (String key : keys) {
            String v = row.get(key);
            if (v != null && !v.isBlank()) {
                return;
            }
        }
        errors.add("one of [" + String.join(",", keys) + "] required");
    }

    public record ValidationResult(boolean passed, List<String> errors) {
        public static ValidationResult ok() { return new ValidationResult(true, List.of()); }
        public static ValidationResult fail(List<String> errors) {
            return new ValidationResult(false, List.copyOf(errors));
        }
    }
}
