package com.briefly.common.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public final class WebUtil {
    private WebUtil() {}

    public static void forward(HttpServletRequest req, HttpServletResponse resp, String view)
            throws IOException {
        try {
            req.getRequestDispatcher("/WEB-INF/views/" + view).forward(req, resp);
        } catch (Exception e) {
            throw new IOException("Failed to forward to view: " + view, e);
        }
    }

    public static void redirect(HttpServletRequest req, HttpServletResponse resp, String path)
            throws IOException {
        resp.sendRedirect(req.getContextPath() + path);
    }

    public static void setError(HttpServletRequest req, String message) {
        req.setAttribute("error", message);
    }
}
