package com.briefly.report.controller;

import com.briefly.report.service.ReportService;
import com.briefly.common.util.WebUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/reports")
public class ReportServlet extends HttpServlet {
    private final ReportService reportService = new ReportService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Long fundId = Long.parseLong(req.getParameter("fundId"));
            req.setAttribute("reports", reportService.getReportsByFund(fundId));
            req.setAttribute("fundId", fundId);
            WebUtil.forward(req, resp, "report/list.jsp");
        } catch (Exception e) {
            WebUtil.setError(req, "운용 브리프 조회 중 오류가 발생했습니다.");
            WebUtil.forward(req, resp, "error/error.jsp");
        }
    }
}
