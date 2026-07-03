package com.briefly.alert.dao;

import com.briefly.alert.entity.RiskAlert;
import com.briefly.common.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AlertDao {
    public List<RiskAlert> findByFundIds(List<Long> fundIds) throws SQLException {
        if (fundIds.isEmpty()) {
            return List.of();
        }
        String placeholders = String.join(",", fundIds.stream().map(id -> "?").toList());
        String sql = "SELECT * FROM risk_alerts WHERE fund_id IN (" + placeholders + ") ORDER BY created_at DESC";
        List<RiskAlert> list = new ArrayList<>();
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < fundIds.size(); i++) {
                ps.setLong(i + 1, fundIds.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public Long insert(RiskAlert alert) throws SQLException {
        String sql = """
                INSERT INTO risk_alerts (fund_id, title, message, previous_grade, new_grade)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, alert.getFundId());
            ps.setString(2, alert.getTitle());
            ps.setString(3, alert.getMessage());
            ps.setInt(4, alert.getPreviousGrade());
            ps.setInt(5, alert.getNewGrade());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("Failed to insert alert");
    }

    private RiskAlert mapRow(ResultSet rs) throws SQLException {
        RiskAlert alert = new RiskAlert();
        alert.setId(rs.getLong("id"));
        alert.setFundId(rs.getLong("fund_id"));
        alert.setTitle(rs.getString("title"));
        alert.setMessage(rs.getString("message"));
        alert.setPreviousGrade(rs.getInt("previous_grade"));
        alert.setNewGrade(rs.getInt("new_grade"));
        alert.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return alert;
    }
}
