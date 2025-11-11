package com.mycompany.vibra.dao;

import com.mycompany.vibra.Factories.Common_UI.ImageUtils;
import com.mycompany.vibra.db.Database;
import com.mycompany.vibra.model.Playlist;
import com.mycompany.vibra.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon; // <-- Import ImageIcon

public class PlaylistDao {

    /**
     * CHANGED: Now accepts bio and cover.
     * Note: You must convert your ImageIcon to byte[] *before* calling this.
     */
    public Playlist createPlaylist(int userId, String name, String bio, byte[] coverBytes) throws SQLException {
        // CHANGED: Added bio and cover columns
        final String sql = "INSERT INTO playlists (user_id, name, bio, cover) VALUES (?, ?, ?, ?)";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, userId);
            ps.setString(2, name);
            ps.setString(3, bio); // CHANGED: Added bio
            ps.setBytes(4, coverBytes); // CHANGED: Added cover

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int playlistId = rs.getInt(1);
                    // CHANGED: Return the full 5-argument Playlist object
                    ImageIcon coverIcon = ImageUtils.convertBytesToImageIcon(coverBytes);
                    return new Playlist(playlistId, name, bio, coverIcon, userId);
                }
            }
        }
        return null;
    }

    /**
     * CHANGED: Now searches for a user's playlists
     */
    public List<Playlist> getUserPlaylists(int userId) throws SQLException {
        // CHANGED: Selects all new fields
        final String sql = "SELECT playlist_id, name, user_id, bio, cover FROM playlists WHERE user_id = ? ORDER BY playlist_id";
        try(Connection c = Database.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try(ResultSet rs = ps.executeQuery()) {
                List<Playlist> out = new ArrayList<>();
                while(rs.next()) {
                    // CHANGED: Read all 5 fields
                    int playlistId = rs.getInt("playlist_id");
                    String name = rs.getString("name");
                    int user_id = rs.getInt("user_id");
                    String bio = rs.getString("bio");
                    byte[] coverBytes = rs.getBytes("cover");

                    // Use helper to convert bytes to a displayable image
                    ImageIcon coverIcon = ImageUtils.convertBytesToImageIcon(coverBytes);

                    // CHANGED: Call the 5-argument constructor
                    out.add(new Playlist(playlistId, name, bio, coverIcon, user_id));
                }
                return out;
            }
        }
    }

    /**
     * CHANGED: Renamed to update all fields.
     * Note: You must convert your ImageIcon to byte[] *before* calling this.
     */
    public boolean updatePlaylist(int playlistId, String newName, String newBio, byte[] newCoverBytes) throws SQLException {
        // CHANGED: Update all editable fields
        final String sql = "UPDATE playlists SET name = ?, bio = ?, cover = ? WHERE playlist_id = ?";
        try(Connection c = Database.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, newName);
            ps.setString(2, newBio);
            ps.setBytes(3, newCoverBytes);
            ps.setInt(4, playlistId);

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