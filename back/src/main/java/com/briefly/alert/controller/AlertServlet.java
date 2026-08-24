package com.briefly.alert.controller;

import com.briefly.ai.service.AiAssistService;
import com.briefly.alert.dto.AlertDto;
import com.briefly.auth.entity.User;
import com.briefly.alert.service.AlertService;
import com.briefly.common.util.SessionUtil;
import com.briefly.common.util.WebUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/alerts")
public class AlertServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(AlertServlet.class.getName());

    private final AlertService alertService;
    private final AiAssistService aiAssistService;

    public AlertServlet() {
        this(new AlertService(), new AiAssistService());
    }

    public AlertServlet(AlertService alertService, AiAssistService aiAssistService) {
        this.alertService = alertService;
        this.aiAssistService = aiAssistService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            User user = SessionUtil.getLoginUser(req);
            List<AlertDto> alerts = alertService.getAlertsForUser(user.getId());
            req.setAttribute("alerts", alerts);
            if (!alerts.isEmpty()) {
                aiAssistService.explainRisk(alerts.get(0)).ifPresent(ai -> req.setAttribute("aiExplanation", ai));
            }
            WebUtil.forward(req, resp, "alert/list.jsp");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "위험 알림 조회 실패: " + req.getRequestURI(), e);
            WebUtil.setError(req, "위험 알림 조회 중 오류가 발생했습니다.");
            WebUtil.forward(req, resp, "error/error.jsp");
        }
    }
}
