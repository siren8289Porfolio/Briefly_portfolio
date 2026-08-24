package com.briefly.report.controller;

import com.briefly.ai.service.AiAssistService;
import com.briefly.report.dto.ReportDto;
import com.briefly.report.service.ReportService;
import com.briefly.common.util.WebUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/reports")
public class ReportServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ReportServlet.class.getName());

    private final ReportService reportService;
    private final AiAssistService aiAssistService;

    public ReportServlet() {
        this(new ReportService(), new AiAssistService());
    }

    public ReportServlet(ReportService reportService, AiAssistService aiAssistService) {
        this.reportService = reportService;
        this.aiAssistService = aiAssistService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Long fundId = Long.parseLong(req.getParameter("fundId"));
            List<ReportDto> reports = reportService.getReportsByFund(fundId);
            req.setAttribute("reports", reports);
            req.setAttribute("fundId", fundId);
            if (!reports.isEmpty()) {
                aiAssistService.explainBrief(reports.get(0)).ifPresent(ai -> req.setAttribute("aiExplanation", ai));
            }
            WebUtil.forward(req, resp, "report/list.jsp");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "운용 브리프 조회 실패: " + req.getRequestURI(), e);
            WebUtil.setError(req, "운용 브리프 조회 중 오류가 발생했습니다.");
            WebUtil.forward(req, resp, "error/error.jsp");
        }
    }
}
