package com.mycompany.vibra.dao;

import com.mycompany.vibra.db.Database;
import com.mycompany.vibra.musicUtilities.Track;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlaylistSongDao {

    /**
     * Adds a song to a playlist.
     * Uses "INSERT OR IGNORE" so it won't fail if the song is already in the playlist.
     *
     * @param playlistId The ID of the playlist
     * @param songId The ID of the song
     * @return true if the song was added, false if it was already there
     */
    public boolean addSongToPlaylist(int playlistId, int songId) throws SQLException {
        // "INSERT OR IGNORE" is SQLite-specific syntax that prevents duplicates
        final String sql = "INSERT OR IGNORE INTO playlist_songs (playlist_id, song_id) VALUES (?, ?)";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, playlistId);
            ps.setInt(2, songId);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Removes a song from a playlist.
     *
     * @param playlistId The ID of the playlist
     * @param songId The ID of the song
     * @return true if the song was successfully removed
     */
    public boolean removeSongFromPlaylist(int playlistId, int songId) throws SQLException {
        final String sql = "DELETE FROM playlist_songs WHERE playlist_id = ? AND song_id = ?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, playlistId);
            ps.setInt(2, songId);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Gets all Track objects for a specific playlist.
     * This method JOINS the playlist_songs table with the tracks table.
     *
     * @param playlistId The ID of the playlist
     * @return A List of Track objects
     */
    public List<Track> getSongsForPlaylist(int playlistId) throws SQLException {
        final String sql = "SELECT t.id, t.title, t.artist, t.album, t.file_path, " +
                           "t.duration_sec, t.track_number, t.album_art " +
                           "FROM tracks t JOIN playlist_songs ps ON t.id = ps.song_id " +
                           "WHERE ps.playlist_id = ? " +
                           "ORDER BY t.artist, t.album, t.track_number"; // Order them logically

        List<Track> tracks = new ArrayList<>();

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, playlistId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // ⚠️ See the note below about this constructor
                    tracks.add(new Track(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("artist"),
                            rs.getString("album"),
                            rs.getString("file_path"),
                            rs.getInt("duration_sec"),
                            rs.getInt("track_number"),
                            rs.getBytes("album_art")
                    ));
                }
            }
        }
        return tracks;
    }
}