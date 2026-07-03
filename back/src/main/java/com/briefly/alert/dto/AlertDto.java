package com.briefly.alert.dto;

import com.briefly.alert.entity.RiskAlert;

import java.time.LocalDateTime;

public class AlertDto {
    private Long id;
    private Long fundId;
    private String title;
    private String message;
    private int previousGrade;
    private int newGrade;
    private LocalDateTime createdAt;

    public static AlertDto from(RiskAlert alert) {
        AlertDto dto = new AlertDto();
        dto.id = alert.getId();
        dto.fundId = alert.getFundId();
        dto.title = alert.getTitle();
        dto.message = alert.getMessage();
        dto.previousGrade = alert.getPreviousGrade();
        dto.newGrade = alert.getNewGrade();
        dto.createdAt = alert.getCreatedAt();
        return dto;
    }

    public Long getId() { return id; }
    public Long getFundId() { return fundId; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public int getPreviousGrade() { return previousGrade; }
    public int getNewGrade() { return newGrade; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
