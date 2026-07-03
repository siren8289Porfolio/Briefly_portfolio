package com.briefly.fund.dao;

import com.briefly.fund.entity.Fund;
import com.briefly.common.util.DBConnectionUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FundDao {
    public List<Fund> findActiveAll() throws SQLException {
        String sql = "SELECT * FROM funds WHERE status = 'ACTIVE' ORDER BY id DESC";
        return queryList(sql);
    }

    public List<Fund> findAll() throws SQLException {
        String sql = "SELECT * FROM funds ORDER BY id DESC";
        return queryList(sql);
    }

    public Optional<Fund> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM funds WHERE id = ?";
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

    public Long insert(Fund fund) throws SQLException {
        String sql = """
                INSERT INTO funds (name, description, risk_grade, expected_return, status)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindFund(ps, fund);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("Failed to insert fund");
    }

    public void update(Fund fund) throws SQLException {
        String sql = """
                UPDATE funds
                SET name = ?, description = ?, risk_grade = ?, expected_return = ?, status = ?
                WHERE id = ?
                """;
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bindFund(ps, fund);
            ps.setLong(6, fund.getId());
            ps.executeUpdate();
        }
    }

    private List<Fund> queryList(String sql) throws SQLException {
        List<Fund> funds = new ArrayList<>();
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                funds.add(mapRow(rs));
            }
        }
        return funds;
    }

    private void bindFund(PreparedStatement ps, Fund fund) throws SQLException {
        ps.setString(1, fund.getName());
        ps.setString(2, fund.getDescription());
        ps.setInt(3, fund.getRiskGrade());
        ps.setBigDecimal(4, fund.getExpectedReturn());
        ps.setString(5, fund.getStatus().name());
    }

    private Fund mapRow(ResultSet rs) throws SQLException {
        Fund fund = new Fund();
        fund.setId(rs.getLong("id"));
        fund.setName(rs.getString("name"));
        fund.setDescription(rs.getString("description"));
        fund.setRiskGrade(rs.getInt("risk_grade"));
        fund.setExpectedReturn(rs.getBigDecimal("expected_return"));
        fund.setStatus(Fund.Status.valueOf(rs.getString("status")));
        fund.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        fund.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return fund;
    }
}
