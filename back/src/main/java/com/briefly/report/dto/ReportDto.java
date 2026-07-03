package com.briefly.report.dto;

import com.briefly.report.entity.FundReport;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReportDto {
    private Long id;
    private Long fundId;
    private String title;
    private String content;
    private LocalDate reportDate;
    private LocalDateTime createdAt;

    public static ReportDto from(FundReport report) {
        ReportDto dto = new ReportDto();
        dto.id = report.getId();
        dto.fundId = report.getFundId();
        dto.title = report.getTitle();
        dto.content = report.getContent();
        dto.reportDate = report.getReportDate();
        dto.createdAt = report.getCreatedAt();
        return dto;
    }

    public Long getId() { return id; }
    public Long getFundId() { return fundId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public LocalDate getReportDate() { return reportDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
