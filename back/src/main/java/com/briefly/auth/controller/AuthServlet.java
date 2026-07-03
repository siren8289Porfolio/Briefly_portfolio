package com.briefly.auth.controller;

import com.briefly.auth.entity.User;
import com.briefly.auth.service.AuthService;
import com.briefly.common.util.SessionUtil;
import com.briefly.common.util.WebUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = {"/login", "/signup", "/logout"})
public class AuthServlet extends HttpServlet {
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getServletPath();
        if ("/login".equals(path)) {
            WebUtil.forward(req, resp, "auth/login.jsp");
            return;
        }
        if ("/signup".equals(path)) {
            WebUtil.forward(req, resp, "auth/signup.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getServletPath();
        if ("/logout".equals(path)) {
            SessionUtil.clearSession(req);
            WebUtil.redirect(req, resp, "/login");
            return;
        }
        if ("/login".equals(path)) {
            handleLogin(req, resp);
            return;
        }
        if ("/signup".equals(path)) {
            handleSignup(req, resp);
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String email = req.getParameter("email");
            String password = req.getParameter("password");
            User user = authService.login(email, password);
            SessionUtil.setLoginUser(req, user);
            if (SessionUtil.isAdmin(user)) {
                WebUtil.redirect(req, resp, "/admin/funds");
            } else {
                WebUtil.redirect(req, resp, "/funds");
            }
        } catch (IllegalArgumentException e) {
            WebUtil.setError(req, e.getMessage());
            WebUtil.forward(req, resp, "auth/login.jsp");
        } catch (Exception e) {
            WebUtil.setError(req, "로그인 처리 중 오류가 발생했습니다.");
            WebUtil.forward(req, resp, "auth/login.jsp");
        }
    }

    private void handleSignup(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String email = req.getParameter("email");
            String password = req.getParameter("password");
            String name = req.getParameter("name");
            authService.signup(email, password, name);
            WebUtil.redirect(req, resp, "/login");
        } catch (IllegalArgumentException e) {
            WebUtil.setError(req, e.getMessage());
            WebUtil.forward(req, resp, "auth/signup.jsp");
        } catch (Exception e) {
            WebUtil.setError(req, "회원가입 처리 중 오류가 발생했습니다.");
            WebUtil.forward(req, resp, "auth/signup.jsp");
        }
    }
}
