package com.briefly.report.dao;

import com.briefly.report.entity.FundReport;
import com.briefly.common.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ReportDao {
    public List<FundReport> findByFundId(Long fundId) throws SQLException {
        String sql = "SELECT * FROM fund_reports WHERE fund_id = ? ORDER BY report_date DESC";
        List<FundReport> list = new ArrayList<>();
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, fundId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public Long insert(FundReport report) throws SQLException {
        String sql = "INSERT INTO fund_reports (fund_id, title, content, report_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, report.getFundId());
            ps.setString(2, report.getTitle());
            ps.setString(3, report.getContent());
            ps.setDate(4, Date.valueOf(report.getReportDate()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("Failed to insert report");
    }

    private FundReport mapRow(ResultSet rs) throws SQLException {
        FundReport report = new FundReport();
        report.setId(rs.getLong("id"));
        report.setFundId(rs.getLong("fund_id"));
        report.setTitle(rs.getString("title"));
        report.setContent(rs.getString("content"));
        report.setReportDate(rs.getDate("report_date").toLocalDate());
        report.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return report;
    }
}
