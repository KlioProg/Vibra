package com.mycompany.vibra.model;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class Track {
    private final String title;
    private final String artist;
    private final String album;
    private final String filePath;
    private final int duration; // in seconds
    private final byte[] albumArt; // optional cover art

    public Track(String title, String artist, String album, String filePath, int duration, byte[] albumArt) {
        this.title = cleanTitle(title);
        this.artist = artist;
        this.album = album;
        this.filePath = filePath;
        this.duration = duration;
        this.albumArt = albumArt;
    }

    // Getters
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getAlbum() { return album; }
    public String getFilePath() { return filePath; }
    public int getDuration() { return duration; } // in seconds
    public long getDurationMs() { return duration * 1000L; } // in milliseconds
    public byte[] getAlbumArt() { return albumArt; }

    // ✅ New helper
    public Image getAlbumArtImage() {
        if (albumArt == null) return null;
        try {
            return ImageIO.read(new ByteArrayInputStream(albumArt));
        } catch (IOException e) {
            return null;
        }
    }

    private String cleanTitle(String title) {
        if (title == null) return "";
        return title.replaceFirst("\\.[^.]+$", ""); // Removes the last dot and everything after
    }

    @Override
    public String toString() {
        return title + " - " + artist + " (" + album + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Track)) return false;
        Track other = (Track) o;
        return filePath != null && filePath.equals(other.filePath);
    }

    @Override
    public int hashCode() {
        return filePath != null ? filePath.hashCode() : 0;
    }
}
