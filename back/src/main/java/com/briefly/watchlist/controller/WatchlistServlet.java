package com.briefly.watchlist.controller;

import com.briefly.auth.entity.User;
import com.briefly.watchlist.service.WatchlistService;
import com.briefly.common.util.SessionUtil;
import com.briefly.common.util.WebUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/watchlist/toggle")
public class WatchlistServlet extends HttpServlet {
    private final WatchlistService watchlistService = new WatchlistService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            User user = SessionUtil.getLoginUser(req);
            Long fundId = Long.parseLong(req.getParameter("fundId"));
            watchlistService.toggle(user.getId(), fundId);
            WebUtil.redirect(req, resp, "/funds/detail?id=" + fundId);
        } catch (Exception e) {
            WebUtil.setError(req, "관심상품 처리 중 오류가 발생했습니다.");
            WebUtil.forward(req, resp, "error/error.jsp");
        }
    }
}
