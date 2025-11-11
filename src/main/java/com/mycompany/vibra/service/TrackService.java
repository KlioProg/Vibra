package com.mycompany.vibra.service;

import com.mycompany.vibra.db.Database; // Use YOUR database class
import com.mycompany.vibra.musicUtilities.Track;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TrackService {

    /**
     * Tries to add a new track to the database.
     * It checks the file path to avoid duplicates.
     */
    // REPLACE your entire addTrackIfMissing method with this
    public boolean addTrackIfMissing(Track track) {
        String sqlCheck = "SELECT id FROM tracks WHERE file_path = ?";
        String sqlInsert = "INSERT INTO tracks (title, artist, album, file_path, duration_sec, album_art) VALUES(?,?,?,?,?,?)";

        try (Connection conn = Database.getConnection()) {
            
            // 1. Check if track already exists
            try (PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheck)) {
                pstmtCheck.setString(1, track.getFilePath());
                ResultSet rs = pstmtCheck.executeQuery();
                if (rs.next()) {
                    // Track already exists. Update the object's ID and return.
                    track.setId(rs.getInt("id"));
                    return false; // It wasn't "newly added"
                }
            }

            // 2. If not, insert it AND get the new ID
            // Make sure you import java.sql.Statement
            try (PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                pstmtInsert.setString(1, track.getTitle());
                pstmtInsert.setString(2, track.getArtist());
                pstmtInsert.setString(3, track.getAlbum());
                pstmtInsert.setString(4, track.getFilePath());
                pstmtInsert.setInt(5, track.getDuration());
                pstmtInsert.setBytes(6, track.getAlbumArt());
                pstmtInsert.executeUpdate();

                // --- THIS IS THE FIX ---
                // Get the ID that the database just created
                try (ResultSet generatedKeys = pstmtInsert.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // Update the track object in memory with its real ID
                        track.setId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating track failed, no ID obtained.");
                    }
                }
                // --- END FIX ---

                return true; // New track was added
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Fetches all tracks currently registered in the database.
     */
    public List<Track> getAllTracks() {
        List<Track> allTracks = new ArrayList<>();
        String sql = "SELECT * FROM tracks";

        try (Connection conn = Database.getConnection(); // Uses your DB class
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Track track = new Track(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getString("album"),
                        rs.getString("file_path"),
                        rs.getInt("duration_sec"),
                        rs.getBytes("album_art")
                );
                allTracks.add(track);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allTracks;
    }
}