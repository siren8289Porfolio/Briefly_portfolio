package com.briefly.application.dao;

import com.briefly.application.entity.FundApplication;
import com.briefly.common.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ApplicationDao {
    public Long insert(FundApplication application) throws SQLException {
        String sql = "INSERT INTO fund_applications (user_id, fund_id, amount, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, application.getUserId());
            ps.setLong(2, application.getFundId());
            ps.setBigDecimal(3, application.getAmount());
            ps.setString(4, application.getStatus().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("Failed to insert application");
    }

    public boolean existsPending(Long userId, Long fundId) throws SQLException {
        String sql = """
                SELECT COUNT(*) FROM fund_applications
                WHERE user_id = ? AND fund_id = ? AND status = 'PENDING'
                """;
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, fundId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public List<FundApplication> findByUserId(Long userId) throws SQLException {
        String sql = "SELECT * FROM fund_applications WHERE user_id = ? ORDER BY created_at DESC";
        return queryList(sql, userId);
    }

    public List<FundApplication> findAll() throws SQLException {
        String sql = "SELECT * FROM fund_applications ORDER BY created_at DESC";
        List<FundApplication> list = new ArrayList<>();
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public Optional<FundApplication> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM fund_applications WHERE id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapRow(rs));
            }
        }
    }

    public void updateStatus(Long id, FundApplication.Status status) throws SQLException {
        String sql = "UPDATE fund_applications SET status = ? WHERE id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    private List<FundApplication> queryList(String sql, Long userId) throws SQLException {
        List<FundApplication> list = new ArrayList<>();
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    private FundApplication mapRow(ResultSet rs) throws SQLException {
        FundApplication app = new FundApplication();
        app.setId(rs.getLong("id"));
        app.setUserId(rs.getLong("user_id"));
        app.setFundId(rs.getLong("fund_id"));
        app.setAmount(rs.getBigDecimal("amount"));
        app.setStatus(FundApplication.Status.valueOf(rs.getString("status")));
        app.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        app.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return app;
    }
}
