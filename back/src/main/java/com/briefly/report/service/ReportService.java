package com.briefly.report.service;

import com.briefly.report.dao.ReportDao;
import com.briefly.report.dto.ReportDto;
import com.briefly.report.entity.FundReport;

import java.sql.SQLException;
import java.util.List;

public class ReportService {
    private final ReportDao reportDao = new ReportDao();

    public List<ReportDto> getReportsByFund(Long fundId) throws SQLException {
        return reportDao.findByFundId(fundId).stream().map(ReportDto::from).toList();
    }

    public Long createReport(FundReport report) throws SQLException {
        return reportDao.insert(report);
    }
}
