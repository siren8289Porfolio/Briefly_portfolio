package com.briefly.application.service;

import com.briefly.application.dao.ApplicationDao;
import com.briefly.application.dto.ApplicationDto;
import com.briefly.application.entity.FundApplication;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ApplicationService {
    private final ApplicationDao applicationDao = new ApplicationDao();

    public Long apply(Long userId, Long fundId, BigDecimal amount) throws SQLException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("신청 금액은 0보다 커야 합니다.");
        }
        if (applicationDao.existsPending(userId, fundId)) {
            throw new IllegalArgumentException("이미 진행 중인 신청이 있습니다.");
        }
        FundApplication application = new FundApplication();
        application.setUserId(userId);
        application.setFundId(fundId);
        application.setAmount(amount);
        application.setStatus(FundApplication.Status.PENDING);
        return applicationDao.insert(application);
    }

    public List<ApplicationDto> getByUser(Long userId) throws SQLException {
        return applicationDao.findByUserId(userId).stream().map(ApplicationDto::from).toList();
    }

    public List<ApplicationDto> getAll() throws SQLException {
        return applicationDao.findAll().stream().map(ApplicationDto::from).toList();
    }

    public void updateStatus(Long applicationId, FundApplication.Status status) throws SQLException {
        applicationDao.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("신청 내역을 찾을 수 없습니다."));
        applicationDao.updateStatus(applicationId, status);
    }
}
