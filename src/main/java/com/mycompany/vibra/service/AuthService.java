package com.mycompany.vibra.service;

import com.mycompany.vibra.model.*;
import com.mycompany.vibra.dao.*;

import java.sql.SQLException;

public class AuthService {
    private final UserDao userDao = new UserDao();

    public User signup(String username, String password, String email) throws SQLException {
        return userDao.createUser(username, password, email);
    }

    public User login(String username, String password) throws SQLException {
        return userDao.login(username, password);
    }
}