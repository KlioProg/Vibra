package com.mycompany.vibra.musicUtilities;
import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.File;
import java.io.FileInputStream;

public class AudioPlayer {
    private AdvancedPlayer player;
    private Thread playThread;
    private String currentPath;
    private int pausedFrame = 0;

    public void play(File file) {
        stop();
        currentPath = file.getAbsolutePath();
        playThread = new Thread(() -> {
            try (FileInputStream fis = new FileInputStream(file)) {
                player = new AdvancedPlayer(fis);
                player.play(pausedFrame, Integer.MAX_VALUE); // start at pausedFrame
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        playThread.start();
    }

    public void pause() {
        try {
            if (player != null) {
                player.close();
                pausedFrame += 200; // rough estimate (not perfect)
            }
        } catch (Exception ignored) {}
    }

    public void resume() {
        if (currentPath != null) {
            play(new File(currentPath));
        }
    }

    public void stop() {
        try {
            if (player != null) {
                player.close();
                player = null;
            }
        } catch (Exception ignored) {}
        pausedFrame = 0;
    }
}
