package com.mycompany.vibra.panels;

import com.mycompany.vibra.musicUtilities.AudioPlayer;
import com.mycompany.vibra.musicUtilities.Mp3Utils;
import com.mycompany.vibra.musicUtilities.Track;
import com.mycompany.vibra.musicUtilities.TrackLoader;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import javax.swing.Timer;

public class MusicPlayerPanel extends JPanel {
    private JLabel coverLabel;
    private JLabel titleLabel;
    private JLabel artistLabel;
    private JLabel elapsedLabel;
    private JLabel remainingLabel;
    private JSlider progressSlider;
    private JSlider volumeSlider;
    private JButton playBtn, stopBtn, nextBtn, prevBtn, uploadBtn;

    private AudioPlayer audioPlayer;
    private Timer progressTimer;
    private int elapsedSeconds;
    private int totalSeconds;

    public MusicPlayerPanel() {
        setBackground(new Color(0x2B2C28));
        setLayout(new BorderLayout(10, 10));

        audioPlayer = new AudioPlayer();

        // === Cover Art ===
        coverLabel = new JLabel();
        coverLabel.setHorizontalAlignment(SwingConstants.CENTER);
        coverLabel.setIcon(new ImageIcon("D:\\Academics\\Vibra\\src\\main\\resources\\images\\default_cover.png")); // fallback image
        add(coverLabel, BorderLayout.NORTH);

        // === Track Info ===
        JPanel infoPanel = new JPanel(new GridLayout(2,1));
        infoPanel.setBackground(new Color(0x2B2C28));

        titleLabel = new JLabel("No Track Loaded", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        artistLabel = new JLabel("Unknown Artist", SwingConstants.CENTER);
        artistLabel.setForeground(Color.LIGHT_GRAY);

        infoPanel.add(titleLabel);
        infoPanel.add(artistLabel);
        add(infoPanel, BorderLayout.CENTER);

        // === Controls ===
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(0x2B2C28));

        uploadBtn = new JButton("Upload");
        prevBtn = new JButton("⏮");
        playBtn = new JButton("▶");
        stopBtn = new JButton("⏹");
        nextBtn = new JButton("⏭");

        controlPanel.add(uploadBtn);
        controlPanel.add(prevBtn);
        controlPanel.add(playBtn);
        controlPanel.add(stopBtn);
        controlPanel.add(nextBtn);

        // === Progress + time labels ===
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setBackground(new Color(0x2B2C28));

        elapsedLabel = new JLabel("0:00");
        elapsedLabel.setForeground(Color.WHITE);

        remainingLabel = new JLabel("-0:00", SwingConstants.RIGHT);
        remainingLabel.setForeground(Color.WHITE);

        progressSlider = new JSlider(0, 100, 0);
        progressSlider.setBackground(new Color(0x2B2C28));
        progressSlider.setForeground(Color.MAGENTA);

        progressPanel.add(elapsedLabel, BorderLayout.WEST);
        progressPanel.add(progressSlider, BorderLayout.CENTER);
        progressPanel.add(remainingLabel, BorderLayout.EAST);

        // === Volume slider ===
        volumeSlider = new JSlider(0, 100, 50);
        volumeSlider.setBackground(new Color(0x2B2C28));
        volumeSlider.setForeground(new Color(0x9C27B0)); // purple progress
        volumeSlider.setUI(new BasicSliderUI(volumeSlider) {
            @Override
            public void paintThumb(Graphics g) {
                g.setColor(Color.WHITE);
                g.fillOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
            }
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(0x2B2C28));
        bottomPanel.add(progressPanel, BorderLayout.NORTH);
        bottomPanel.add(controlPanel, BorderLayout.CENTER);
        bottomPanel.add(volumeSlider, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        // === Button Actions ===
        uploadBtn.addActionListener(this::onUpload);
        playBtn.addActionListener(e -> onPlay());
        stopBtn.addActionListener(e -> onStop());
    }

    // 🔹 Upload button action
    private void onUpload(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose an MP3 File");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("MP3 Files", "mp3"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            // Get track duration
            totalSeconds = Mp3Utils.getTrackLengthInSeconds(file.getAbsolutePath());

            // Update UI
            updateTrackInfo(file.getName().replace(".mp3", ""), "Unknown Artist", null, file.getAbsolutePath());

            // Play immediately
            audioPlayer.play(file);
            startProgressTimer(totalSeconds);
        }
    }

    // 🔹 Play button
    private void onPlay() {
        // If already uploaded, just resume timer
        if (totalSeconds > 0) {
            audioPlayer.resume();
            startProgressTimer(totalSeconds - elapsedSeconds);
        }
    }

    // 🔹 Stop button
    private void onStop() {
        audioPlayer.stop();
        if (progressTimer != null) progressTimer.stop();
        progressSlider.setValue(0);
        elapsedLabel.setText("0:00");
        remainingLabel.setText("-" + Mp3Utils.formatTime(totalSeconds));
    }

    // 🔹 Update UI with track info
    public void updateTrackInfo(String title, String artist, ImageIcon coverArt, String filePath) {
        titleLabel.setText(title);
        artistLabel.setText(artist);
        coverLabel.setIcon(coverArt != null ? coverArt : new ImageIcon("default_cover.png"));

        elapsedLabel.setText("0:00");
        remainingLabel.setText("-" + Mp3Utils.formatTime(totalSeconds));
        progressSlider.setMaximum(totalSeconds);
    }

    // 🔹 Timer to update slider + labels
    private void startProgressTimer(int durationSecs) {
        elapsedSeconds = 0;
        if (progressTimer != null && progressTimer.isRunning()) {
            progressTimer.stop();
        }

        progressTimer = new Timer(1000, e -> {
            elapsedSeconds++;
            if (elapsedSeconds <= totalSeconds) {
                elapsedLabel.setText(Mp3Utils.formatTime(elapsedSeconds));
                remainingLabel.setText("-" + Mp3Utils.formatTime(totalSeconds - elapsedSeconds));
                progressSlider.setValue(elapsedSeconds);
            } else {
                ((Timer)e.getSource()).stop();
            }
        });
        progressTimer.start();
    }
}





