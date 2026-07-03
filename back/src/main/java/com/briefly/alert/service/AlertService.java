package com.briefly.alert.service;

import com.briefly.alert.dao.AlertDao;
import com.briefly.watchlist.service.WatchlistService;
import com.briefly.alert.dto.AlertDto;
import com.briefly.alert.entity.RiskAlert;

import java.sql.SQLException;
import java.util.List;

public class AlertService {
    private final AlertDao alertDao = new AlertDao();
    private final WatchlistService watchlistService = new WatchlistService();

    public List<AlertDto> getAlertsForUser(Long userId) throws SQLException {
        List<Long> fundIds = watchlistService.getFundIdsByUser(userId);
        return alertDao.findByFundIds(fundIds).stream().map(AlertDto::from).toList();
    }

    public Long createAlert(RiskAlert alert) throws SQLException {
        if (alert.getPreviousGrade() < 1 || alert.getPreviousGrade() > 5
                || alert.getNewGrade() < 1 || alert.getNewGrade() > 5) {
            throw new IllegalArgumentException("위험등급은 1~5 사이여야 합니다.");
        }
        return alertDao.insert(alert);
    }
}
