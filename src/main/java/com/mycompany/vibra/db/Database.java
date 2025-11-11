package com.mycompany.vibra.db;

import java.sql.*;

public final class Database {
    private static final String DB_URL = "jdbc:sqlite:app.db";

    // first run of the app, connects with the db_url and runs table creation
    static {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            try (Statement s = conn.createStatement()) {
                s.execute("PRAGMA foreign_keys = ON;");
            }
            runMigrations(conn);
        } catch (SQLException e) {
            throw new RuntimeException("Database init failed", e);
        }
    }

    // daily use of the app, returns fresh connection with foreign keys on
    public static Connection getConnection() throws SQLException {
        Connection c = DriverManager.getConnection(DB_URL);
        try (Statement s = c.createStatement()) {
            s.execute("PRAGMA foreign_keys = ON;");
        }
        return c;
    }

    // statements to create the tables
    private static void runMigrations(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            conn.setAutoCommit(false); // false so the statements get commited 2gether

            st.execute(
                "CREATE TABLE IF NOT EXISTS users (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  username TEXT NOT NULL UNIQUE," +
                "  password_hash TEXT NOT NULL," +
                "  created_at TEXT DEFAULT CURRENT_TIMESTAMP" +
                ");"
            );

            st.execute(
                "CREATE TABLE IF NOT EXISTS tracks (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  title TEXT NOT NULL," +
                "  artist TEXT NOT NULL," +
                " album TEXT NOT NULL, " +
                " file_path TEXT UNIQUE NOT NULL," +
                "  duration_sec INTEGER," +
                " track_number INTEGER," +
                " album_art BLOB" +
                ");"
            );

            st.execute(
                "CREATE TABLE IF NOT EXISTS liked_songs (" +
                "  user_id INTEGER NOT NULL," +
                "  song_id INTEGER NOT NULL," +
                "  created_at TEXT DEFAULT CURRENT_TIMESTAMP," +
                "  PRIMARY KEY (user_id, song_id)," +
                "  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE," +
                "  FOREIGN KEY (song_id) REFERENCES tracks(id) ON DELETE CASCADE" +
                ");"
            );

            st.execute(
                "CREATE TABLE IF NOT EXISTS playlists (" +
                " playlist_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " user_id INTEGER NOT NULL, " +
                "  name TEXT NOT NULL," +
                " bio TEXT," +
                " cover BLOB," +
                "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                " FOREIGN KEY (user_id) REFERENCES users(id)" +
                ");"
            );

            st.execute(
                "CREATE TABLE IF NOT EXISTS playlist_songs (" +
                "  playlist_id INTEGER NOT NULL," +
                "  song_id INTEGER NOT NULL," +
                "  FOREIGN KEY (song_id) REFERENCES tracks(id) ON DELETE CASCADE," +
                "  FOREIGN KEY (playlist_id) REFERENCES playlists(playlist_id) ON DELETE CASCADE," +
                "  PRIMARY KEY (playlist_id, song_id)" +
                ");"
            );

            conn.commit();
            conn.setAutoCommit(true); // goes back to default
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }

    private Database() {}
}
