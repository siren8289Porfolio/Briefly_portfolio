package com.briefly.application.dto;

import com.briefly.application.entity.FundApplication;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ApplicationDto {
    private Long id;
    private Long userId;
    private Long fundId;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;

    public static ApplicationDto from(FundApplication application) {
        ApplicationDto dto = new ApplicationDto();
        dto.id = application.getId();
        dto.userId = application.getUserId();
        dto.fundId = application.getFundId();
        dto.amount = application.getAmount();
        dto.status = application.getStatus().name();
        dto.createdAt = application.getCreatedAt();
        return dto;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getFundId() { return fundId; }
    public BigDecimal getAmount() { return amount; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
