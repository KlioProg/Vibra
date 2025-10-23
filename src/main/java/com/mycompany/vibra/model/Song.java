package com.mycompany.vibra.model;

public class Song {
    private final int id;
    private final String title;
    private final String artist;
    private final int duration;
    private final String filePath;

    public Song(int id, String title, String artist, int duration, String filePath) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.filePath = filePath;
    }
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public int getDuration() { return duration; }
    public String getFilePath() { return filePath; }

    @Override public String toString() {
        return title + " — " + artist;
    }
}
