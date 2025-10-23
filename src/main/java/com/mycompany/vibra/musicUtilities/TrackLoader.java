package com.mycompany.vibra.musicUtilities;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

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
                try {
                    Mp3File mp3 = new Mp3File(file);

                    String title = file.getName().substring(0, file.getName().lastIndexOf("."));
                    String artist = "Unknown Artist";
                    String album = "Unknown Album";
                    int duration = (int) mp3.getLengthInSeconds();
                    byte[] albumArt = null;

                    if (mp3.hasId3v2Tag()) {
                        ID3v2 id3v2Tag = mp3.getId3v2Tag();
                        if (id3v2Tag.getTitle() != null && !id3v2Tag.getTitle().isEmpty()) {
                            title = id3v2Tag.getTitle();
                        }
                        if (id3v2Tag.getArtist() != null && !id3v2Tag.getArtist().isEmpty()) {
                            artist = id3v2Tag.getArtist();
                        }
                        if (id3v2Tag.getAlbum() != null && !id3v2Tag.getAlbum().isEmpty()) {
                            album = id3v2Tag.getAlbum();
                        }
                        albumArt = id3v2Tag.getAlbumImage();
                    }

                    tracks.add(new Track(title, artist, album, file.getAbsolutePath(), duration, albumArt));

                } catch (Exception e) {
                    System.out.println("⚠️ Error reading file: " + file.getName() + " -> " + e.getMessage());
                    // fallback Track with minimal info
                    String fallbackTitle = file.getName().substring(0, file.getName().lastIndexOf("."));
                    tracks.add(new Track(fallbackTitle, "Unknown Artist", "Unknown Album", file.getAbsolutePath(), 0, null));
                }
            }
        } else {
            System.out.println("⚠️ No MP3 files found in: " + folderPath);
        }

        return tracks;
    }
}
