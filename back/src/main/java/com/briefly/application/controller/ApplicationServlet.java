package com.briefly.application.controller;

import com.briefly.auth.entity.User;
import com.briefly.application.service.ApplicationService;
import com.briefly.common.util.SessionUtil;
import com.briefly.common.util.WebUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/applications")
public class ApplicationServlet extends HttpServlet {
    private final ApplicationService applicationService = new ApplicationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            User user = SessionUtil.getLoginUser(req);
            req.setAttribute("applications", applicationService.getByUser(user.getId()));
            WebUtil.forward(req, resp, "application/list.jsp");
        } catch (Exception e) {
            WebUtil.setError(req, "신청 내역 조회 중 오류가 발생했습니다.");
            WebUtil.forward(req, resp, "error/error.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            User user = SessionUtil.getLoginUser(req);
            Long fundId = Long.parseLong(req.getParameter("fundId"));
            BigDecimal amount = new BigDecimal(req.getParameter("amount"));
            applicationService.apply(user.getId(), fundId, amount);
            WebUtil.redirect(req, resp, "/applications");
        } catch (IllegalArgumentException e) {
            WebUtil.setError(req, e.getMessage());
            WebUtil.redirect(req, resp, "/funds/detail?id=" + req.getParameter("fundId"));
        } catch (Exception e) {
            WebUtil.setError(req, "모의가입 신청 중 오류가 발생했습니다.");
            WebUtil.forward(req, resp, "error/error.jsp");
        }
    }
}
