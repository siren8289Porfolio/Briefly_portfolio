package com.briefly.fund.dto;

import com.briefly.fund.entity.Fund;

import java.math.BigDecimal;

public class FundDto {
    private Long id;
    private String name;
    private String description;
    private int riskGrade;
    private BigDecimal expectedReturn;
    private String status;

    public static FundDto from(Fund fund) {
        FundDto dto = new FundDto();
        dto.id = fund.getId();
        dto.name = fund.getName();
        dto.description = fund.getDescription();
        dto.riskGrade = fund.getRiskGrade();
        dto.expectedReturn = fund.getExpectedReturn();
        dto.status = fund.getStatus().name();
        return dto;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getRiskGrade() { return riskGrade; }
    public BigDecimal getExpectedReturn() { return expectedReturn; }
    public String getStatus() { return status; }
}
