package com.briefly.fund.controller;

import com.briefly.fund.dto.FundDto;
import com.briefly.auth.entity.User;
import com.briefly.fund.service.FundService;
import com.briefly.watchlist.service.WatchlistService;
import com.briefly.common.util.SessionUtil;
import com.briefly.common.util.WebUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/funds", "/funds/detail"})
public class FundServlet extends HttpServlet {
    private final FundService fundService = new FundService();
    private final WatchlistService watchlistService = new WatchlistService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            if ("/funds/detail".equals(req.getServletPath())) {
                Long fundId = Long.parseLong(req.getParameter("id"));
                FundDto fund = FundDto.from(fundService.getFund(fundId));
                req.setAttribute("fund", fund);
                User user = SessionUtil.getLoginUser(req);
                if (user != null) {
                    req.setAttribute("watched", watchlistService.isWatched(user.getId(), fundId));
                }
                WebUtil.forward(req, resp, "fund/detail.jsp");
                return;
            }

            List<FundDto> funds = fundService.getActiveFunds().stream().map(FundDto::from).toList();
            req.setAttribute("funds", funds);
            WebUtil.forward(req, resp, "fund/list.jsp");
        } catch (IllegalArgumentException e) {
            WebUtil.setError(req, e.getMessage());
            WebUtil.forward(req, resp, "error/not-found.jsp");
        } catch (Exception e) {
            WebUtil.setError(req, "상품 조회 중 오류가 발생했습니다.");
            WebUtil.forward(req, resp, "error/error.jsp");
        }
    }
}
