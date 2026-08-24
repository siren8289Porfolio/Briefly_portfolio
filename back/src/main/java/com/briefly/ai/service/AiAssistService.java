package com.briefly.ai.service;

import com.briefly.ai.client.AiClient;
import com.briefly.ai.dto.AiExplanationDto;
import com.briefly.alert.dto.AlertDto;
import com.briefly.fund.entity.Fund;
import com.briefly.report.dto.ReportDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Optional assistive explanations. Never mutates product tables; never blocks core reads.
 */
public class AiAssistService {
    private final AiClient aiClient;

    public AiAssistService() {
        this(new AiClient());
    }

    public AiAssistService(AiClient aiClient) {
        this.aiClient = aiClient;
    }

    public Optional<AiExplanationDto> explainFund(Fund fund) {
        if (fund == null) {
            return Optional.empty();
        }
        return aiClient.explainFund(
                fund.getId(),
                fund.getName(),
                fund.getDescription(),
                fund.getRiskGrade(),
                fund.getExpectedReturn()
        );
    }

    public Optional<AiExplanationDto> explainBrief(ReportDto report) {
        if (report == null) {
            return Optional.empty();
        }
        String reportDate = report.getReportDate() == null ? null : report.getReportDate().toString();
        return aiClient.explainBrief(
                report.getFundId(),
                report.getTitle(),
                report.getContent(),
                reportDate
        );
    }

    public List<AiExplanationDto> explainBriefs(List<ReportDto> reports) {
        List<AiExplanationDto> out = new ArrayList<>();
        if (reports == null) {
            return out;
        }
        for (ReportDto report : reports) {
            explainBrief(report).ifPresent(out::add);
        }
        return out;
    }

    public Optional<AiExplanationDto> explainRisk(AlertDto alert) {
        if (alert == null) {
            return Optional.empty();
        }
        return aiClient.explainRisk(
                alert.getFundId(),
                alert.getTitle(),
                alert.getMessage(),
                alert.getPreviousGrade(),
                alert.getNewGrade()
        );
    }

    public List<AiExplanationDto> explainRisks(List<AlertDto> alerts) {
        List<AiExplanationDto> out = new ArrayList<>();
        if (alerts == null) {
            return out;
        }
        for (AlertDto alert : alerts) {
            explainRisk(alert).ifPresent(out::add);
        }
        return out;
    }
}
