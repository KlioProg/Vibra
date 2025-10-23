package com.mycompany.vibra.model;

public class Playlist {
    private final int playlist_id;
    private final int user_id;
    private final String text;

    public Playlist(int playlist_id, String text, int user_id) {
        this.playlist_id = playlist_id;
        this.text = text;
        this.user_id = user_id;
    }

    public int getPlaylistId() { return playlist_id; }
    public String getText() { return text; }
    public int getUserId() { return user_id; }
}
