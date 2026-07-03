package com.briefly.admin.controller;

import com.briefly.fund.dto.FundDto;
import com.briefly.fund.entity.Fund;
import com.briefly.application.entity.FundApplication;
import com.briefly.report.entity.FundReport;
import com.briefly.alert.entity.RiskAlert;
import com.briefly.alert.service.AlertService;
import com.briefly.application.service.ApplicationService;
import com.briefly.fund.service.FundService;
import com.briefly.report.service.ReportService;
import com.briefly.common.util.WebUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@WebServlet(urlPatterns = {
        "/admin/funds",
        "/admin/reports",
        "/admin/alerts",
        "/admin/applications/status"
})
public class AdminServlet extends HttpServlet {
    private final FundService fundService = new FundService();
    private final ReportService reportService = new ReportService();
    private final AlertService alertService = new AlertService();
    private final ApplicationService applicationService = new ApplicationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String path = req.getServletPath();
            if ("/admin/funds".equals(path)) {
                List<FundDto> funds = fundService.getAllFunds().stream().map(FundDto::from).toList();
                req.setAttribute("funds", funds);
                WebUtil.forward(req, resp, "admin/funds.jsp");
                return;
            }
            if ("/admin/applications/status".equals(path)) {
                req.setAttribute("applications", applicationService.getAll());
                WebUtil.forward(req, resp, "admin/applications.jsp");
            }
        } catch (Exception e) {
            WebUtil.setError(req, "관리자 화면 조회 중 오류가 발생했습니다.");
            WebUtil.forward(req, resp, "error/error.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String path = req.getServletPath();
            switch (path) {
                case "/admin/funds" -> handleFundSave(req, resp);
                case "/admin/reports" -> handleReportSave(req, resp);
                case "/admin/alerts" -> handleAlertSave(req, resp);
                case "/admin/applications/status" -> handleStatusUpdate(req, resp);
                default -> WebUtil.forward(req, resp, "error/not-found.jsp");
            }
        } catch (IllegalArgumentException e) {
            WebUtil.setError(req, e.getMessage());
            WebUtil.forward(req, resp, "error/error.jsp");
        } catch (Exception e) {
            WebUtil.setError(req, "관리자 요청 처리 중 오류가 발생했습니다.");
            WebUtil.forward(req, resp, "error/error.jsp");
        }
    }

    private void handleFundSave(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Fund fund = new Fund();
        String idParam = req.getParameter("id");
        fund.setName(req.getParameter("name"));
        fund.setDescription(req.getParameter("description"));
        fund.setRiskGrade(Integer.parseInt(req.getParameter("riskGrade")));
        fund.setExpectedReturn(new BigDecimal(req.getParameter("expectedReturn")));
        fund.setStatus(Fund.Status.valueOf(req.getParameter("status").toUpperCase()));
        if (idParam != null && !idParam.isBlank()) {
            fund.setId(Long.parseLong(idParam));
            fundService.updateFund(fund);
        } else {
            fundService.createFund(fund);
        }
        WebUtil.redirect(req, resp, "/admin/funds");
    }

    private void handleReportSave(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        FundReport report = new FundReport();
        report.setFundId(Long.parseLong(req.getParameter("fundId")));
        report.setTitle(req.getParameter("title"));
        report.setContent(req.getParameter("content"));
        report.setReportDate(LocalDate.parse(req.getParameter("reportDate")));
        reportService.createReport(report);
        WebUtil.redirect(req, resp, "/admin/funds");
    }

    private void handleAlertSave(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        RiskAlert alert = new RiskAlert();
        alert.setFundId(Long.parseLong(req.getParameter("fundId")));
        alert.setTitle(req.getParameter("title"));
        alert.setMessage(req.getParameter("message"));
        alert.setPreviousGrade(Integer.parseInt(req.getParameter("previousGrade")));
        alert.setNewGrade(Integer.parseInt(req.getParameter("newGrade")));
        alertService.createAlert(alert);
        WebUtil.redirect(req, resp, "/admin/funds");
    }

    private void handleStatusUpdate(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Long applicationId = Long.parseLong(req.getParameter("applicationId"));
        FundApplication.Status status = FundApplication.Status.valueOf(
                req.getParameter("status").toUpperCase()
        );
        applicationService.updateStatus(applicationId, status);
        WebUtil.redirect(req, resp, "/admin/applications/status");
    }
}
