package com.mycompany.vibra.musicUtilities;


import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Header;
import java.io.FileInputStream;

public class Mp3Utils {

    public static int getTrackLengthInSeconds(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            Bitstream bitstream = new Bitstream(fis);
            Header header = bitstream.readFrame();
            int size = fis.available();
            int totalMs = (int) header.total_ms(size);
            return totalMs / 1000; // convert ms → seconds
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // fallback if failed
        }
    }

    public static String formatTime(int seconds) {
        int mins = seconds / 60;
        int secs = seconds % 60;
        return String.format("%d:%02d", mins, secs);
    }
}