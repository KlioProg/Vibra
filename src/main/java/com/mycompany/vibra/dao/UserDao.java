package com.mycompany.vibra.dao;

import com.mycompany.vibra.db.Database;
import com.mycompany.vibra.model.User;
import org.mindrot.jbcrypt.BCrypt;


import java.sql.*;

public class UserDao {

    public User createUser(String username, String plainPassword) throws SQLException {
        final String sql = "INSERT INTO users(username, password_hash) VALUES(?, ?)";
        String hash = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));

        try (Connection c = Database.getConnection();
            PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, hash);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return new User(rs.getInt(1), username);
                }
            }
        }
        throw new SQLException("Failed to insert user");
    }

    public User findByUsername(String username) throws SQLException {
        final String sql = "SELECT id, username FROM users WHERE username = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"), rs.getString("username"));
                }
            }
        }
        return null;
    }

    public User login(String username, String plainPassword) throws SQLException {
        final String sql = "SELECT id, username, password_hash FROM users WHERE username = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hash = rs.getString("password_hash");
                    if (BCrypt.checkpw(plainPassword, hash)) {
                        return new User(rs.getInt("id"), rs.getString("username"));
                    }
                }
            }
        }
        return null; // invalid credentials
    }
}