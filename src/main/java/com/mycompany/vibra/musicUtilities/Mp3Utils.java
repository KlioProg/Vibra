package com.mycompany.vibra.musicUtilities;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Header;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;

public class Mp3Utils {

    // JLayer duration calculation (backup method)
    public static int getTrackLengthInSeconds(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            Bitstream bitstream = new Bitstream(fis);
            Header header = bitstream.readFrame();
            int size = fis.available();
            int totalMs = (int) header.total_ms(size);
            return totalMs / 1000; // ms → seconds
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Format time mm:ss
    public static String formatTime(int seconds) {
        int mins = seconds / 60;
        int secs = seconds % 60;
        return String.format("%d:%02d", mins, secs);
    }

    // Extract metadata from a single file
    public static Track extractMetadata(String filePath) {
        String title = "Unknown";
        String artist = "Unknown";
        String album = "Unknown";
        int duration = 0;
        int trackNum = 0;
        byte[] albumArt = null;

        try {
            Mp3File mp3file = new Mp3File(filePath);

            // Duration in seconds
            duration = (int) mp3file.getLengthInSeconds();

            if (mp3file.hasId3v2Tag()) {
                ID3v2 tag = mp3file.getId3v2Tag();
                if (tag.getTitle() != null && !tag.getTitle().isEmpty()) title = tag.getTitle();
                if (tag.getArtist() != null && !tag.getArtist().isEmpty()) artist = tag.getArtist();
                String trackStr = tag.getTrack(); // e.g., "1/12" or "1"
                if (trackStr != null && !trackStr.isEmpty()) {
                    try {
                        String numberOnly = trackStr.split("/")[0]; // Get "1" from "1/12"
                        trackNum = Integer.parseInt(numberOnly);
                    } catch (NumberFormatException e) {
                        // Tag was malformed, ignore it
                    }
                }
                if (tag.getAlbum() != null && !tag.getAlbum().isEmpty()) album = tag.getAlbum();

                // Album art
                albumArt = tag.getAlbumImage();
            } else if (mp3file.hasId3v1Tag()) {
                ID3v1 tag = mp3file.getId3v1Tag();
                if (tag.getTitle() != null && !tag.getTitle().isEmpty()) title = tag.getTitle();
                if (tag.getArtist() != null && !tag.getArtist().isEmpty()) artist = tag.getArtist();
                if (tag.getAlbum() != null && !tag.getAlbum().isEmpty()) album = tag.getAlbum();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Track(title, artist, album, filePath, duration, trackNum, albumArt);
    }

    // ✅ Extract metadata for multiple files
    public static List<Track> extractMultipleMetadata(List<String> filePaths) {
        List<Track> tracks = new ArrayList<>();
        for (String path : filePaths) {
            tracks.add(extractMetadata(path));
        }
        return tracks;
    }

    // ✅ Overload for File[] input (useful for JFileChooser multi-select)
    public static List<Track> extractMultipleMetadata(File[] files) {
        List<Track> tracks = new ArrayList<>();
        for (File file : files) {
            if (file.getName().toLowerCase().endsWith(".mp3")) {
                tracks.add(extractMetadata(file.getAbsolutePath()));
            }
        }
        return tracks;
    }

    // Format mm:ss for milliseconds
    public static String formatMinutes(int ms) {
        int totalSeconds = ms / 1000;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}
