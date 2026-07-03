package com.briefly.common.filter;

import com.briefly.common.util.SessionUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Set;

@WebFilter("/*")
public class LoginCheckFilter implements Filter {
    private static final Set<String> PUBLIC_EXACT = Set.of("/login", "/signup");
    private static final Set<String> PUBLIC_PREFIX = Set.of("/css/", "/js/", "/error/");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI().substring(req.getContextPath().length());
        if (isPublic(path, req.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        if (path.startsWith("/admin")) {
            chain.doFilter(request, response);
            return;
        }

        if (SessionUtil.getLoginUser(req) == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isPublic(String path, String method) {
        if (PUBLIC_EXACT.contains(path)) {
            return true;
        }
        if ("GET".equalsIgnoreCase(method) && ("/funds".equals(path) || path.startsWith("/funds/detail"))) {
            return true;
        }
        for (String prefix : PUBLIC_PREFIX) {
            if (path.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
