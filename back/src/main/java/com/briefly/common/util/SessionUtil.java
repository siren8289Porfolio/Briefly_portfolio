package com.briefly.common.util;

import com.briefly.auth.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public final class SessionUtil {
    public static final String SESSION_USER = "loginUser";

    private SessionUtil() {}

    public static User getLoginUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object user = session.getAttribute(SESSION_USER);
        return user instanceof User ? (User) user : null;
    }

    public static void setLoginUser(HttpServletRequest request, User user) {
        HttpSession session = request.getSession(true);
        session.setAttribute(SESSION_USER, user);
    }

    public static void clearSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    public static boolean isAdmin(User user) {
        return user != null && user.getRole() == User.Role.ADMIN;
    }
}
