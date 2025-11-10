package com.mycompany.vibra.dao;

import com.mycompany.vibra.db.Database;
import com.mycompany.vibra.musicUtilities.Track;

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
            boolean success = ps.executeUpdate() > 0;

            if(success) {
                System.out.println("DAO Log: Liked track ID " + songId + " for user ID " + userId);
            }

            return success;
        }
    }

    public boolean unlike(int userId, int songId) throws SQLException {
        final String sql = "DELETE FROM liked_songs WHERE user_id = ? AND song_id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, songId);
            boolean success = ps.executeUpdate() > 0;

            if(success) {
                System.out.println("DAO Log: Unliked track ID " + songId + " for user ID " + userId);
            }

            return success;
        }
    }

    // Add this method to LikedSongsDao.java

    public boolean unlikeAll(int userId) throws SQLException {
        final String sql = "DELETE FROM liked_songs WHERE user_id = ?";
        try (Connection c = Database.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Track> listLikedByUser(int userId) throws SQLException {
        final String sql = "SELECT t.id, t.title, t.artist, t.album, t.file_path, t.duration_sec, t.album_art " +
            "FROM liked_songs ls " +
            "JOIN tracks t ON t.id = ls.song_id " +
            "WHERE ls.user_id = ? " +
            "ORDER BY ls.created_at DESC";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                List<Track> out = new ArrayList<>();
                while (rs.next()) {

                    out.add(new Track(
                        rs.getInt("id"), 
                        rs.getString("title"), 
                        rs.getString("artist"), 
                        rs.getString("album"),
                        rs.getString("file_path"),
                        rs.getInt("duration_sec"), 
                        rs.getBytes("album_art")));
                }
                return out;
            }
        }
    }
}