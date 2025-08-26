package com.mycompany.vibra.musicUtilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TrackLoader {

    public static List<Track> loadTracks(String folderPath) {
        List<Track> tracks = new ArrayList<>();
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Invalid folder: " + folderPath);
            return tracks;
        }

        // accept both .mp3 and .MP3 etc.
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3"));

        if (files != null && files.length > 0) {
            for (File file : files) {
                String fileName = file.getName();
                String title = fileName.substring(0, fileName.lastIndexOf("."));
                String artist = "Unknown Artist";
                tracks.add(new Track(title, artist, file.getAbsolutePath()));
            }
        } else {
            System.out.println("⚠️ No MP3 files found in: " + folderPath);
        }

        return tracks;
    }
}