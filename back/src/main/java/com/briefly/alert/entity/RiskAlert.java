package com.briefly.alert.entity;

import java.time.LocalDateTime;

public class RiskAlert {
    private Long id;
    private Long fundId;
    private String title;
    private String message;
    private int previousGrade;
    private int newGrade;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFundId() { return fundId; }
    public void setFundId(Long fundId) { this.fundId = fundId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getPreviousGrade() { return previousGrade; }
    public void setPreviousGrade(int previousGrade) { this.previousGrade = previousGrade; }

    public int getNewGrade() { return newGrade; }
    public void setNewGrade(int newGrade) { this.newGrade = newGrade; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
