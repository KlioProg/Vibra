/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vibra.model;
import java.util.List;

import com.mycompany.vibra.musicUtilities.Track;
/**
 *
 * @author robbiebelen
 */
public class PlaylistIterator implements TrackIterator{

    private List<Track> tracks;
    private int position = 0; //keeps track of current position in the list

    public PlaylistIterator(Playlist playlist){
        this.tracks = playlist.getTracks();
    }
    @Override
    public boolean hasNext() {
        // There is a next track if our position is not at the end of the list.
        return position < tracks.size();
    }

    @Override
    public Track next() {
        // Get the track at the current position
        Track track = tracks.get(position);
        position++; // Move the position forward for the next call
        return track;
    }

    @Override
    public boolean hasPrevious() {
        // There is a previous track if our position is not at the very beginning.
        return position > 0;
    }

    @Override
    public Track previous() {
        position--;  // Move the position back first
        return tracks.get(position); // Get the track at the new position

    }
}

