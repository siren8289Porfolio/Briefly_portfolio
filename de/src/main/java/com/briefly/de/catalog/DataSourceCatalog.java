package com.briefly.de.catalog;

/** Public financial data catalog. Evidence: DESIGNED — live API NOT TESTED. */
public enum DataSourceCatalog {
    FSC_STOCK_PRICE(
            "금융위원회_주식시세정보",
            "https://www.data.go.kr/data/15094808/openapi.do",
            "GetStockSecuritiesInfoService"
    ),
    FSC_KRX_LISTED(
            "금융위원회_KRX상장종목정보",
            "https://www.data.go.kr/data/15094775/openapi.do",
            "KRX listed master"
    ),
    FSC_COMPANY_INFO(
            "금융위원회_기업기본정보",
            "https://www.data.go.kr",
            "company-master"
    ),
    OPENDART_DISCLOSURE(
            "OPEN DART 공시검색",
            "https://opendart.fss.or.kr/",
            "/api/list.json"
    ),
    KRX_OPENAPI_AUX(
            "KRX Data Marketplace OPEN API",
            "https://openapi.krx.co.kr/",
            "auxiliary"
    );

    private final String sourceName;
    private final String sourceUrl;
    private final String endpointHint;

    DataSourceCatalog(String sourceName, String sourceUrl, String endpointHint) {
        this.sourceName = sourceName;
        this.sourceUrl = sourceUrl;
        this.endpointHint = endpointHint;
    }

    public String sourceName() { return sourceName; }
    public String sourceUrl() { return sourceUrl; }
    public String endpointHint() { return endpointHint; }
    public boolean isContextOnly() { return true; }
}
