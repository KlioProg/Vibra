package com.mycompany.vibra.dao;

import com.mycompany.vibra.db.Database;
import com.mycompany.vibra.model.Song;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LikedSongsDao {

    public boolean like(int userId, int songId) throws SQLException {
        final String sql = "INSERT OR IGNORE INTO liked_songs(user_id, song_id) VALUES(?, ?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, songId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean unlike(int userId, int songId) throws SQLException {
        final String sql = "DELETE FROM liked_songs WHERE user_id = ? AND song_id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, songId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Song> listLikedByUser(int userId) throws SQLException {
        final String sql = "SELECT s.id, s.title, s.artist, s.duration, s.filePath " +
            "FROM liked_songs ls " +
            "JOIN songs s ON s.id = ls.song_id " +
            "WHERE ls.user_id = ? " +
            "ORDER BY ls.created_at DESC";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Song> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(new Song(rs.getInt("id"), rs.getString("title"), rs.getString("artist"), rs.getInt("duration"), rs.getString("filePath")));
                }
                return out;
            }
        }
    }
}