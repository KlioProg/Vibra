package com.mycompany.vibra.dao;

import com.mycompany.vibra.db.Database;
import com.mycompany.vibra.model.Playlist;
import com.mycompany.vibra.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDao {

    public Playlist createPlaylist(User user, String name) throws SQLException {
    final String sql = "INSERT INTO playlists (user_id, name) VALUES (?, ?)";

    try (Connection c = Database.getConnection();
         PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        ps.setInt(1, user.getId());   
        ps.setString(2, name);
        ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int playlistId = rs.getInt(1);
                    return new Playlist(playlistId, name, user.getId());
                }
            }
        }
        return null;
    }

    public List<Playlist> search(String searchText) throws SQLException {
        final String sql = "SELECT playlist_id, name, user_id FROM playlists WHERE name LIKE ? ORDER BY playlist_id";
        try(Connection c = Database.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)) {
                String pattern = "%" + searchText + "%";
                ps.setString(1, pattern);

                try(ResultSet rs = ps.executeQuery()) {
                    List<Playlist> out = new ArrayList<>();
                    while(rs.next()) {
                        out.add(new Playlist(rs.getInt("playlist_id"), rs.getString("name"), rs.getInt("user_id")));
                    }

                    return out;
                }
            }
    }

    public boolean updatePlaylistName(int playlistId, String newName) throws SQLException {
        final String sql = "UPDATE playlists SET name = ? WHERE playlist_id = ?";
        try(Connection c = Database.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)) {
                
                ps.setString(1, newName);
                ps.setInt(2, playlistId);

                return ps.executeUpdate() > 0;
            }
    }

    public boolean deletePlaylist(int playlistId) throws SQLException {
        final String sql = "DELETE FROM playlists WHERE playlist_id = ?";
        try(Connection c = Database.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, playlistId);

                return ps.executeUpdate() > 0;
            }
    }
}