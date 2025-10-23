package com.mycompany.vibra.musicUtilities;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

import javax.sound.sampled.*;
import java.io.FileInputStream;
import java.io.InputStream;

public class AudioPlayer {
    private Thread playThread;
    private volatile boolean isPlaying = false;
    private volatile boolean isPaused = false;
    private volatile boolean stopRequested = false;

    private Track currentTrack;
    private volatile long currentPositionMs = 0;

    private SourceDataLine line;

    // audio info for progress
    private volatile int sampleRate = 44100;
    private volatile long playedSamples = 0;
    private volatile int samplesPerFrame = 1152;

    // 🔊 Real volume control
    public void setVolume(float volume) {
        if (line != null && line.isOpen()) {
            try {
                FloatControl volCtrl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
                float min = volCtrl.getMinimum();
                float max = volCtrl.getMaximum();
                float gain = min + (max - min) * volume; // map 0.0–1.0
                volCtrl.setValue(gain);
            } catch (Exception e) {
                System.out.println("Volume control not supported: " + e.getMessage());
            }
        }
    }

    public void play(Track track) {
        System.out.println("=== PLAY CALLED ===");
        // Stop any current playback
        if (isPlaying || isPaused) {
        if (this.currentTrack == null || !this.currentTrack.equals(track)) {
            stop(); // full reset for new track
        } else {
            stopRequested = true; // stop thread but keep position
        }
    }

        if (this.currentTrack == null || !this.currentTrack.equals(track)) {
        this.currentTrack = track;
        this.currentPositionMs = 0;
        this.playedSamples = 0;
    }
        startSimplePlayback();
    }

    public void pause() {
        System.out.println("=== PAUSE CALLED ===");
        if (!isPlaying || isPaused) return;

        isPaused = true;
        stopWithoutReset();

        currentPositionMs = getCurrentPosition();
        System.out.println("Paused at: " + currentPositionMs + "ms");
    }

    public void resume() {
        System.out.println("=== RESUME CALLED ===");
        if (!isPaused || currentTrack == null) return;

        isPaused = false;
        // Resume from current position - but for now, just restart
        startSimplePlayback();
    }

    public void stop() {
        stopInternal(true); // full reset
    }

    private void stopWithoutReset() {
        stopInternal(false); // keep position
    }


    private void stopInternal(boolean resetPosition) {
    System.out.println("=== STOP CALLED ===");
    stopRequested = true;
    isPaused = false;
    isPlaying = false;

    if (resetPosition) {
        currentPositionMs = 0;
        playedSamples = 0;
    }

    // Wait for thread to finish
    if (playThread != null) {
        try {
            playThread.join(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Close audio line
    if (line != null && line.isOpen()) {
        try {
            line.stop();
            line.close();
        } catch (Exception ignored) {}
    }
    line = null;
}


    // Simplified - no seeking for now, just return current position
    public void setPosition(long ms) {
        System.out.println("=== SEEK IGNORED (for now) === " + ms + "ms");
        // For now, ignore seeking to focus on basic playback
    }

    public long getCurrentPosition() {
        if (isPlaying && sampleRate > 0) {
            return (playedSamples * 1000L) / sampleRate;
        }
        return currentPositionMs;
    }

    public boolean isPlaying() { return isPlaying; }
    public boolean isPaused() { return isPaused; }
    public Track getCurrentTrack() { return currentTrack; }

    // ========== SIMPLE PLAYBACK - NO SEEKING ==========
    private void startSimplePlayback() {
        System.out.println("=== STARTING SIMPLE PLAYBACK ===");
        stopRequested = false;

        playThread = new Thread(() -> {
            InputStream in = null;
            Bitstream bitstream = null;
            Decoder decoder = null;

            try {
                System.out.println("Opening file: " + currentTrack.getFilePath());
                in = new FileInputStream(currentTrack.getFilePath());
                bitstream = new Bitstream(in);
                decoder = new Decoder();

                // Get the first header for format info
                Header firstHeader = bitstream.readFrame();
                if (firstHeader == null) {
                    System.err.println("Could not read first MP3 header!");
                    return;
                }

                sampleRate = firstHeader.frequency();
                int channels = firstHeader.mode() == Header.SINGLE_CHANNEL ? 1 : 2;

                // Calculate samples per frame
                switch (firstHeader.layer()) {
                    case 1: samplesPerFrame = 384; break;
                    case 2: samplesPerFrame = 1152; break;
                    case 3:
                    default: samplesPerFrame = 1152; break;
                }

                System.out.println("Audio format: " + sampleRate + "Hz, " + channels + " channels, " +
                        samplesPerFrame + " samples/frame");

                // Setup audio system
                AudioFormat format = new AudioFormat(sampleRate, 16, channels, true, false);
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

                if (!AudioSystem.isLineSupported(info)) {
                    System.err.println("Audio format not supported!");
                    return;
                }

                // Large buffer for smooth playback
                int bufferSize = sampleRate * channels * 2; // 1 second buffer
                line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(format, bufferSize);
                line.start();

                System.out.println("Audio line opened with buffer size: " + bufferSize);

                // Set volume
                setVolume(0.7f);

                // Mark as playing
                isPlaying = true;
                isPaused = false;

                // Calculate starting position
                long targetSamples = (currentPositionMs * sampleRate) / 1000L;
                long framesToSkip = targetSamples / samplesPerFrame;
                playedSamples = framesToSkip * samplesPerFrame;

                System.out.println("Seeking to " + currentPositionMs + "ms (skipping " + framesToSkip + " frames)");

                // Put the first frame back and start decoding
                bitstream.unreadFrame();

                int frameCount = 0;
                Header h;

                System.out.println("=== STARTING DECODE LOOP ===");

                while (!stopRequested && (h = bitstream.readFrame()) != null) {
                    try {
                        // Always decode to maintain sync
                        SampleBuffer output = (SampleBuffer) decoder.decodeFrame(h, bitstream);
                        if (output == null) {
                            System.out.println("Decoder returned null at frame " + frameCount);
                            break;
                        }

                        frameCount++;

                        // Skip frames if we haven't reached our target position yet
                        if (frameCount <= framesToSkip) {
                            bitstream.closeFrame();
                            continue;
                        }

                        // Get PCM data
                        short[] pcm = output.getBuffer();
                        if (pcm == null || pcm.length == 0) {
                            bitstream.closeFrame();
                            continue;
                        }

                        // Convert to bytes (little endian)
                        byte[] audioBytes = new byte[pcm.length * 2];
                        int byteIndex = 0;
                        for (short sample : pcm) {
                            audioBytes[byteIndex++] = (byte) (sample & 0xFF);
                            audioBytes[byteIndex++] = (byte) ((sample >> 8) & 0xFF);
                        }

                        // Write to audio line (this will block if buffer is full)
                        line.write(audioBytes, 0, audioBytes.length);

                        // Update position ONLY for played frames
                        playedSamples += samplesPerFrame;
                        currentPositionMs = (playedSamples * 1000L) / sampleRate;

                        // Log progress every 200 played frames
                        if ((frameCount - framesToSkip) % 200 == 0) {
                            System.out.println("Playing frame " + frameCount + " - Position: " +
                                    (currentPositionMs / 1000) + "s");
                        }

                    } catch (Exception e) {
                        System.err.println("Error processing frame " + frameCount + ": " + e.getMessage());
                        e.printStackTrace();
                    }

                    bitstream.closeFrame();
                }

                System.out.println("=== DECODE LOOP FINISHED ===");
                System.out.println("Total frames processed: " + frameCount);
                System.out.println("Stop requested: " + stopRequested);

                // Wait for all audio to finish playing
                if (!stopRequested) {
                    System.out.println("Draining audio buffer...");
                    line.drain();
                    currentPositionMs = currentTrack != null ? currentTrack.getDurationMs() : currentPositionMs;
                    System.out.println("=== PLAYBACK COMPLETED NATURALLY ===");
                }

            } catch (Exception e) {
                System.err.println("Playback error: " + e.getMessage());
                e.printStackTrace();
            } finally {
                // Cleanup everything
                try {
                    if (decoder != null) {
                        // JLayer doesn't have a close method for decoder
                    }
                    if (bitstream != null) bitstream.close();
                    if (in != null) in.close();
                } catch (Exception e) {
                    System.err.println("Error closing streams: " + e.getMessage());
                }

                if (line != null && line.isOpen()) {
                    try {
                        line.stop();
                        line.close();
                    } catch (Exception e) {
                        System.err.println("Error closing audio line: " + e.getMessage());
                    }
                }

                isPlaying = false;
                System.out.println("=== PLAYBACK THREAD FINISHED ===");
            }
        });

        playThread.setName("AudioPlayer-" + currentTrack.getTitle());
        playThread.setDaemon(true);
        playThread.start();
    }
}