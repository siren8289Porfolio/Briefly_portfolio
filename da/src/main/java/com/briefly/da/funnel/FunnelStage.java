package com.briefly.da.funnel;

/** Product engagement funnel stages. */
public enum FunnelStage {
    EXPLORE("explore"),
    INTEREST("interest"),
    MOCK_JOIN("mock_join"),
    BRIEF("brief");

    private final String code;

    FunnelStage(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}
