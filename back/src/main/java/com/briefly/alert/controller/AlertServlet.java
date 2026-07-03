package com.briefly.alert.controller;

import com.briefly.auth.entity.User;
import com.briefly.alert.service.AlertService;
import com.briefly.common.util.SessionUtil;
import com.briefly.common.util.WebUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/alerts")
public class AlertServlet extends HttpServlet {
    private final AlertService alertService = new AlertService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            User user = SessionUtil.getLoginUser(req);
            req.setAttribute("alerts", alertService.getAlertsForUser(user.getId()));
            WebUtil.forward(req, resp, "alert/list.jsp");
        } catch (Exception e) {
            WebUtil.setError(req, "위험 알림 조회 중 오류가 발생했습니다.");
            WebUtil.forward(req, resp, "error/error.jsp");
        }
    }
}
