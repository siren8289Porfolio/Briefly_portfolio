package com.briefly.watchlist;

import com.briefly.watchlist.WatchlistDao;
import com.briefly.watchlist.Watchlist;

import java.sql.SQLException;
import java.util.List;

public class WatchlistService {
    private final WatchlistDao watchlistDao = new WatchlistDao();

    public boolean toggle(Long userId, Long fundId) throws SQLException {
        return watchlistDao.findByUserAndFund(userId, fundId)
                .map(existing -> {
                    try {
                        watchlistDao.delete(userId, fundId);
                        return false;
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElseGet(() -> {
                    try {
                        watchlistDao.insert(userId, fundId);
                        return true;
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public List<Watchlist> getByUser(Long userId) throws SQLException {
        return watchlistDao.findByUserId(userId);
    }

    public List<Long> getFundIdsByUser(Long userId) throws SQLException {
        return watchlistDao.findByUserId(userId).stream()
                .map(Watchlist::getFundId)
                .toList();
    }

    public boolean isWatched(Long userId, Long fundId) throws SQLException {
        return watchlistDao.findByUserAndFund(userId, fundId).isPresent();
    }
}
