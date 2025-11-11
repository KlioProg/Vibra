package com.mycompany.vibra.model;

import com.mycompany.vibra.musicUtilities.Track;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon; // <-- Import ImageIcon

/**
 * This is the MODEL for a Playlist.
 * It implements Subject so the UI can "observe" it for changes.
 */
public class Playlist implements Subject {

    // --- Database Fields ---
    private final int playlist_id;
    private final int user_id;

    // --- Editable UI Fields ---
    private String text; // <-- Removed 'final' so it can be edited
    private String bio; // <-- ADDED
    private ImageIcon cover; // <-- ADDED

    // --- Player State Fields ---
    private List<Track> tracks = new ArrayList<>();
    private int currentTrackIndex = -1;

    // --- Observer Pattern ---
    private List<Observer> observers = new ArrayList<>();

    /**
     * Updated constructor to include bio and cover.
     */
    public Playlist(int playlist_id, String text, String bio, ImageIcon cover, int user_id) {
        this.playlist_id = playlist_id;
        this.text = text;
        this.bio = bio; // <-- ADDED
        this.cover = cover; // <-- ADDED
        this.user_id = user_id;
    }

    // --- Getters for all fields ---
    public int getPlaylistId() { return playlist_id; }
    public int getUserId() { return user_id; }
    public String getText() { return text; }
    public String getBio() { return bio; } // <-- ADDED
    public ImageIcon getCover() { return cover; } // <-- ADDED
    public List<Track> getTracks() { return tracks; }


    // --- Setters for Editable Fields ---
    // These methods update the model's state AND notify the UI.

    public void setText(String text) {
        this.text = text;
        notifyObservers(); // Tell the UI to update
    }

    public void setBio(String bio) {
        this.bio = bio;
        notifyObservers(); // Tell the UI to update
    }

    public void setCover(ImageIcon cover) {
        this.cover = cover;
        notifyObservers(); // Tell the UI to update
    }

    // --- Your Existing Track & Player Logic (Unchanged) ---

    public void addTrack(Track track) {
        tracks.add(track);
        // You might want to notifyObservers() here too if the track count is displayed
    }

    public Track getCurrentTrack() {
        if (currentTrackIndex >= 0 && currentTrackIndex < tracks.size()) {
            return tracks.get(currentTrackIndex);
        }
        return null;
    }

    public void setCurrentTrackIndex(int index) {
        if (index >= 0 && index < tracks.size()) {
            this.currentTrackIndex = index;
            notifyObservers();
        }
    }

    // --Iterator (Unchanged)
    public TrackIterator createIterator() {
        return new PlaylistIterator(this);
    }

    // --- Observer Pattern Methods (Unchanged) ---
    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }
}