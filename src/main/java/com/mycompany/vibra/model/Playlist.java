package com.mycompany.vibra.model;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.vibra.musicUtilities.Track;

public class Playlist implements Subject {
    private final int playlist_id;
    private final int user_id;
    private final String text;
    private List<Track> tracks = new ArrayList<>();
    private int currentTrackIndex = -1;
    private List<Observer> observers = new ArrayList<>();

    public Playlist(int playlist_id, String text, int user_id) {
        this.playlist_id = playlist_id;
        this.text = text;
        this.user_id = user_id;
    }
// new stuff added  {
    public void addTrack(Track track) {
        tracks.add(track);
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
            // STEP 3: Notify observers whenever the state (current track) changes.
            notifyObservers();
        }
    }
     // --Iterator
     public TrackIterator createIterator() {
         return new PlaylistIterator(this);
     }

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
        // Loop through all observers and call to update
        for (Observer observer : observers) {
            observer.update();
        }
    }
    public int getPlaylistId() { return playlist_id; }
    public String getText() { return text; }
    public int getUserId() { return user_id; }
    public List<Track> getTracks() { return tracks; }
}
