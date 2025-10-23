package com.mycompany.vibra.dao;

import com.mycompany.vibra.db.Database;
import com.mycompany.vibra.model.Song;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SongDao {

    public Song insertSong(Song song) throws SQLException {
        final String sql = "INSERT INTO songs (title, artist, duration, filePath) VALUES (?, ?, ?, ?)";

        try(Connection c = Database.getConnection(); 
            PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, song.getTitle());
                ps.setString(2, song.getArtist());
                ps.setInt(3, song.getDuration());
                ps.setString(4, song.getFilePath());

                ps.executeUpdate();

                try(ResultSet rs = ps.getGeneratedKeys()) {
                    if(rs.next()) {
                        return new Song(rs.getInt(1), song.getTitle(), song.getArtist(), song.getDuration(), song.getFilePath());
                    } 
                }
            }

            throw new SQLException("Failed to insert song");
    }

    public List<Song> getAllSongs() throws SQLException {
        final String sql = "SELECT id, title, artist, duration, filePath FROM songs ORDER BY id";
        try(Connection c = Database.getConnection();
            PreparedStatement ps = c.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

                List<Song> out = new ArrayList<>();
                while(rs.next()) {
                    out.add(new Song(rs.getInt("id"), rs.getString("title"), rs.getString("artist"), rs.getInt("duration"), rs.getString("filePath")));
                }

                return out;
            }
    }

    public List<Song> search(String searchText) throws SQLException {
        final String sql = "SELECT id, title, artist, duration, filePath FROM songs WHERE title LIKE ? OR artist LIKE ? ORDER BY id";
        try(Connection c = Database.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)) {
                String pattern = "%" + searchText + "%";
                ps.setString(1, pattern);
                ps.setString(2, pattern);

                try(ResultSet rs = ps.executeQuery()) {
                    List<Song> out = new ArrayList<>();
                    while(rs.next()) {
                        out.add(new Song(rs.getInt("id"), rs.getString("title"), rs.getString("artist"), rs.getInt("duration"), rs.getString("filePath")));
                    }

                    return out;
                }
            }
    }
    
}