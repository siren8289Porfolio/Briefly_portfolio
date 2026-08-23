package com.briefly.da.event;

import java.util.ArrayList;
import java.util.List;

/** Validates events before mart load. Invalid → quarantine (DESIGNED). */
public final class EventValidator {

    public ValidationResult validate(AnalyticsEvent event) {
        List<String> errors = new ArrayList<>();
        if (event.eventId() == null || event.eventId().isBlank()) {
            errors.add("event_id required");
        }
        if (event.userIdHash() == null || event.userIdHash().length() != 64) {
            errors.add("user_id_hash must be SHA-256 hex (64 chars)");
        }

        switch (event.type()) {
            case PRODUCT_INTEREST_ADD, INTEREST_REMOVE, BRIEF_VIEW, RISK_ALERT_VIEW ->
                    requireProductId(event, errors);
            case MOCK_JOIN_SUBMIT -> {
                requireProductId(event, errors);
                if (event.amount() == null || event.amount().signum() <= 0) {
                    errors.add("amount must be > 0 for mock_join_submit");
                }
            }
            case MARKET_CONTEXT_VIEW -> {
                if (event.securityId() == null || event.securityId().isBlank()) {
                    errors.add("security_id required for market_context_view");
                }
            }
        }
        return errors.isEmpty() ? ValidationResult.ok() : ValidationResult.fail(errors);
    }

    private static void requireProductId(AnalyticsEvent event, List<String> errors) {
        if (event.productId() == null) {
            errors.add("product_id required for " + event.type().eventName());
        }
    }

    public record ValidationResult(boolean passed, List<String> errors) {
        public static ValidationResult ok() {
            return new ValidationResult(true, List.of());
        }

        public static ValidationResult fail(List<String> errors) {
            return new ValidationResult(false, List.copyOf(errors));
        }
    }
}
