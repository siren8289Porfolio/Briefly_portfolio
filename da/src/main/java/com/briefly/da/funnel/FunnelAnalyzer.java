package com.briefly.da.funnel;

import com.briefly.da.event.AnalyticsEvent;
import com.briefly.da.event.AnalyticsEventType;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Funnel: explore → interest → mock_join → brief.
 * Correlation analysis only — no causal claims.
 */
public final class FunnelAnalyzer {

    private static final Map<AnalyticsEventType, FunnelStage> EVENT_STAGE = Map.of(
            AnalyticsEventType.PRODUCT_INTEREST_ADD, FunnelStage.INTEREST,
            AnalyticsEventType.MOCK_JOIN_SUBMIT, FunnelStage.MOCK_JOIN,
            AnalyticsEventType.BRIEF_VIEW, FunnelStage.BRIEF
    );

    public FunnelResult analyze(List<AnalyticsEvent> events) {
        Map<FunnelStage, Set<String>> usersByStage = new EnumMap<>(FunnelStage.class);
        for (FunnelStage stage : FunnelStage.values()) {
            usersByStage.put(stage, new HashSet<>());
        }

        for (AnalyticsEvent event : events) {
            if (event.productId() != null) {
                usersByStage.get(FunnelStage.EXPLORE).add(event.userIdHash());
            }
            FunnelStage stage = EVENT_STAGE.get(event.type());
            if (stage != null) {
                usersByStage.get(stage).add(event.userIdHash());
            }
        }

        return new FunnelResult(
                usersByStage.get(FunnelStage.EXPLORE).size(),
                usersByStage.get(FunnelStage.INTEREST).size(),
                usersByStage.get(FunnelStage.MOCK_JOIN).size(),
                usersByStage.get(FunnelStage.BRIEF).size()
        );
    }

    public record FunnelResult(long exploreUsers, long interestUsers, long mockJoinUsers, long briefUsers) {
        public double stageRate(FunnelStage from, FunnelStage to) {
            long fromCount = count(from);
            long toCount = count(to);
            if (fromCount == 0) {
                return Double.NaN;
            }
            return (double) toCount / fromCount;
        }

        private long count(FunnelStage stage) {
            return switch (stage) {
                case EXPLORE -> exploreUsers;
                case INTEREST -> interestUsers;
                case MOCK_JOIN -> mockJoinUsers;
                case BRIEF -> briefUsers;
            };
        }
    }
}
