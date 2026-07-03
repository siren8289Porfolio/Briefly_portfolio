package com.briefly.watchlist.dao;

import com.briefly.watchlist.entity.Watchlist;
import com.briefly.common.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WatchlistDao {
    public Optional<Watchlist> findByUserAndFund(Long userId, Long fundId) throws SQLException {
        String sql = "SELECT * FROM watchlists WHERE user_id = ? AND fund_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, fundId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapRow(rs));
            }
        }
    }

    public List<Watchlist> findByUserId(Long userId) throws SQLException {
        String sql = "SELECT * FROM watchlists WHERE user_id = ? ORDER BY created_at DESC";
        List<Watchlist> list = new ArrayList<>();
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

    public void insert(Long userId, Long fundId) throws SQLException {
        String sql = "INSERT INTO watchlists (user_id, fund_id) VALUES (?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, fundId);
            ps.executeUpdate();
        }
    }

    public void delete(Long userId, Long fundId) throws SQLException {
        String sql = "DELETE FROM watchlists WHERE user_id = ? AND fund_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, fundId);
            ps.executeUpdate();
        }
    }

    private Watchlist mapRow(ResultSet rs) throws SQLException {
        Watchlist item = new Watchlist();
        item.setId(rs.getLong("id"));
        item.setUserId(rs.getLong("user_id"));
        item.setFundId(rs.getLong("fund_id"));
        item.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return item;
    }
}
