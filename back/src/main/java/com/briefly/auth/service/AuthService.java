package com.briefly.auth.service;

import com.briefly.auth.dao.UserDao;
import com.briefly.auth.entity.User;
import com.briefly.common.util.PasswordUtil;

import java.sql.SQLException;
import java.util.Optional;

public class AuthService {
    private final UserDao userDao = new UserDao();

    public User login(String email, String password) throws SQLException {
        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));
        if (!PasswordUtil.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        return sanitize(user);
    }

    public User signup(String email, String password, String name) throws SQLException {
        if (userDao.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(PasswordUtil.hash(password));
        user.setName(name);
        user.setRole(User.Role.USER);
        Long id = userDao.insert(user);
        user.setId(id);
        return sanitize(user);
    }

    public Optional<User> findById(Long id) throws SQLException {
        return userDao.findById(id).map(this::sanitize);
    }

    private User sanitize(User user) {
        user.setPasswordHash(null);
        return user;
    }
}
