package com.mycompany.vibra.Content.Main_Page.Main_Contents.Music_Player;

import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.ButtonIconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.DarkModeIconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.IconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.LightModeIconFactory;
import com.mycompany.vibra.Factories.ThemeFactory.DarkModeToggle;
import com.mycompany.vibra.Factories.ThemeFactory.ThemeManager;
import com.mycompany.vibra.musicUtilities.AudioPlayer;
import com.mycompany.vibra.musicUtilities.Track;
import com.mycompany.vibra.Factories.Music_UI.CustomSliderUI;
import com.mycompany.vibra.Factories.Common_UI.FontLoaderFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import static com.mycompany.vibra.musicUtilities.Mp3Utils.formatMinutes;

public class MusicPlayerPanel extends JPanel {

    private final AudioPlayer audioPlayer;
    private final IconFactory icons;
    private IconFactory themeIcons;

    private JLabel trackTitleLabel;
    private JLabel trackArtistLabel;
    private JLabel albumArtLabel;
    private JSlider progressSlider;
    private JSlider volumeSlider;
    private JLabel currentTimeLabel;
    private JLabel totalTimeLabel;

    private JButton playPauseButton;
    private JButton prevButton;
    private JButton nextButton;
    private JButton likeButton;

    private Track currentTrack;
    private Timer progressTimer;
    private boolean isPlaying = false;
    private boolean isLiked = false;


    // we only keep references, icons come from factory
    private ImageIcon playIcon, pauseIcon, heartIcon, likedIcon, defaultCover, themeButton;

    public MusicPlayerPanel(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        this.icons = new ButtonIconFactory();
        this.themeIcons = ThemeManager.getInstance().isDarkMode() ? new DarkModeIconFactory() : new LightModeIconFactory();

        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(30, 40, 30, 40));
        ThemeManager.getInstance().addThemeChangerListener(isDark -> applyTheme(isDark));


        initUI();
        initTimer();
        applyTheme(ThemeManager.getInstance().isDarkMode());
    }

    private void initUI() {
        // Loading icons from IconButtonFactory
        playIcon = icons.createIcon("play");
        pauseIcon = icons.createIcon("pause");
        ImageIcon backIcon = icons.createIcon("back");
        ImageIcon skipIcon = icons.createIcon("skip");
        ImageIcon soundDownIcon = icons.createIcon("sound_down");
        ImageIcon soundUpIcon = icons.createIcon("sound_up")    ;
        heartIcon = icons.createIcon("liked");
        likedIcon = icons.createIcon("liked_pressed");
        defaultCover = themeIcons.createIcon("default_cover");
        themeButton = themeIcons.createIcon("theme_button");

        // ===== Album art =====
        albumArtLabel = new JLabel(defaultCover);
        albumArtLabel.setHorizontalAlignment(SwingConstants.CENTER);
        albumArtLabel.setPreferredSize(new Dimension(375, 375));

        JPanel albumPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        albumPanel.setOpaque(false);
        albumPanel.add(albumArtLabel);

        // ===== Track Info Panel =====
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(25, 0, 15, 0)); // slightly adjusted padding

// Track Title Label
        trackTitleLabel = new JLabel("Track Title", SwingConstants.CENTER);
        trackTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        trackTitleLabel.setForeground(new Color(0xFFFFFF)); // pure white
        trackTitleLabel.setFont(FontLoaderFactory.loadFont("/fonts/DunbarTall-Bold.ttf", 16f));
        trackTitleLabel.setMaximumSize(new Dimension(300, 30)); // keep it neat
        trackTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        trackTitleLabel.setVerticalAlignment(SwingConstants.CENTER);

// Artist Label
        trackArtistLabel = new JLabel("Artist", SwingConstants.CENTER);
        trackArtistLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        trackArtistLabel.setForeground(new Color(0xC0C0C0)); // softer gray tone
        trackArtistLabel.setFont(FontLoaderFactory.loadFont("/fonts/DunbarTall-Bold.ttf", 13f));
        trackArtistLabel.setMaximumSize(new Dimension(250, 25));
        trackArtistLabel.setHorizontalAlignment(SwingConstants.CENTER);
        trackArtistLabel.setVerticalAlignment(SwingConstants.CENTER);

// Add components to panel with spacing
        infoPanel.add(trackTitleLabel);
        infoPanel.add(Box.createVerticalStrut(8)); // more breathing space
        infoPanel.add(trackArtistLabel);


        // ===== Controls Panel =====
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
        controlsPanel.setOpaque(false);
        controlsPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        // --- Progress slider ---
        JPanel progressPanel = new JPanel(new BorderLayout(10, 0));
        progressPanel.setOpaque(false);
        progressPanel.setBorder(new EmptyBorder(0, 20, 15, 20));

        currentTimeLabel = new JLabel("0:00");
        currentTimeLabel.setForeground(new Color(0x9D4EDD));
        currentTimeLabel.setFont(FontLoaderFactory.loadFont("/fonts/DunbarTall-Bold.ttf", 14f));

        totalTimeLabel = new JLabel("0:00");
        totalTimeLabel.setForeground(new Color(0xB0B0B0));
        totalTimeLabel.setFont(FontLoaderFactory.loadFont("/fonts/DunbarTall-Bold.ttf", 14f));

        progressSlider = new JSlider(0, 1000, 0);
        progressSlider.setUI(new CustomSliderUI(progressSlider, new Color(0x9D4EDD)));
        progressSlider.setOpaque(false);

        progressPanel.add(currentTimeLabel, BorderLayout.WEST);
        progressPanel.add(progressSlider, BorderLayout.CENTER);
        progressPanel.add(totalTimeLabel, BorderLayout.EAST);

        // --- Main buttons ---
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setBorder(new EmptyBorder(10, 0, 20, 0));

        prevButton = new JButton(backIcon);
        playPauseButton = new JButton(pauseIcon); // Start with play
        nextButton = new JButton(skipIcon);

        for (JButton btn : new JButton[]{prevButton, playPauseButton, nextButton}) {
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setOpaque(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        playPauseButton.setPreferredSize(new Dimension(140, 140));

        buttonsPanel.add(prevButton);
        buttonsPanel.add(playPauseButton);
        buttonsPanel.add(nextButton);

        // --- Volume row ---
        JPanel volumePanel = new JPanel(new BorderLayout(15, 0));
        volumePanel.setOpaque(false);
        volumePanel.setBorder(new EmptyBorder(0, 50, 0, 50));
        volumePanel.setMaximumSize(new Dimension(500, 40));

        JLabel soundDownLabel = new JLabel(soundDownIcon);
        JLabel soundUpLabel = new JLabel(soundUpIcon);

        volumeSlider = new JSlider(0, 100, 50);
        volumeSlider.setUI(new CustomSliderUI(volumeSlider, new Color(0x9D4EDD)));
        volumeSlider.setOpaque(false);
        volumeSlider.setPreferredSize(new Dimension(427, 36));

        volumePanel.add(soundDownLabel, BorderLayout.WEST);
        volumePanel.add(volumeSlider, BorderLayout.CENTER);
        volumePanel.add(soundUpLabel, BorderLayout.EAST);

        // --- Like + Dark Mode Toggle ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        likeButton = new JButton(heartIcon);
        likeButton.setBorderPainted(false);
        likeButton.setContentAreaFilled(false);
        likeButton.setFocusPainted(false);
        likeButton.setOpaque(false);
        likeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        DarkModeToggle darkModeToggle = new DarkModeToggle();

        rightPanel.add(likeButton);
        rightPanel.add(darkModeToggle);

        topPanel.add(rightPanel, BorderLayout.EAST);

        // "Now playing"
        JLabel nowPlayingLabel = new JLabel("Now playing");
        nowPlayingLabel.setForeground(new Color(0xB0B0B0));
        nowPlayingLabel.setFont(FontLoaderFactory.loadFont("/fonts/DunbarTall-Bold.ttf", 16f));
        nowPlayingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nowPlayingLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        nowPlayingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ===== Assemble =====
        controlsPanel.add(progressPanel);
        controlsPanel.add(buttonsPanel);
        controlsPanel.add(Box.createVerticalStrut(10));
        controlsPanel.add(volumePanel);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

// Align everything at the center
        nowPlayingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        albumPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

// Slightly smaller album art to give more room
        albumArtLabel.setPreferredSize(new Dimension(300, 300));

// Add components with tighter spacing
        centerPanel.add(Box.createVerticalStrut(0));       // small top margin
        centerPanel.add(nowPlayingLabel);
        centerPanel.add(Box.createVerticalStrut(-16));        // less space before album
        centerPanel.add(albumPanel);
        centerPanel.add(Box.createVerticalStrut(10));       // smaller gap
        centerPanel.add(infoPanel);
        centerPanel.add(Box.createVerticalGlue());          // flexible bottom spacing

// Add panels to layout
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(controlsPanel, BorderLayout.SOUTH);


        // ===== Listeners =====
        playPauseButton.addActionListener(e -> {
            if (currentTrack == null) return;

            if (!isPlaying) {
                audioPlayer.play(currentTrack);
                progressTimer.start();
                playPauseButton.setIcon(playIcon);
                isPlaying = true;
            } else {
                audioPlayer.pause();
                progressTimer.stop();
                playPauseButton.setIcon(pauseIcon);
                isPlaying = false;
            }
        });

        likeButton.addActionListener(e -> {
            isLiked = !isLiked;
            likeButton.setIcon(isLiked ? likedIcon : heartIcon);
        });

        volumeSlider.addChangeListener(e -> {
            float value = volumeSlider.getValue() / 100f;
            audioPlayer.setVolume(value);
        });

        progressSlider.addChangeListener(e -> {
            if (progressSlider.getValueIsAdjusting() || currentTrack == null) return;
            long newPos = (long) (currentTrack.getDurationMs() *
                    (progressSlider.getValue() / 1000.0));
            audioPlayer.setPosition(newPos);
        });
    }

    private void initTimer() {
        progressTimer = new Timer(500, e -> {
            if (currentTrack != null) {
                long pos = audioPlayer.getCurrentPosition();
                long dur = currentTrack.getDurationMs();
                if (dur > 0) {
                    int value = (int) ((pos / (double) dur) * 1000);
                    progressSlider.setValue(value);

                    currentTimeLabel.setText(formatMinutes((int) pos));
                    totalTimeLabel.setText(formatMinutes((int) dur));
                }
                if (!audioPlayer.isPlaying() && pos >= dur) {
                    playPauseButton.setIcon(playIcon);
                    isPlaying = false;
                    progressTimer.stop();
                }
            }
        });
    }

    private void applyTheme(boolean isDark) {
        themeIcons = isDark ? new DarkModeIconFactory() : new LightModeIconFactory();


        defaultCover = themeIcons.createIcon("default_cover");
        themeButton = themeIcons.createIcon("theme_button");


        playIcon = icons.createIcon("play");
        pauseIcon = icons.createIcon("pause");
        heartIcon = icons.createIcon("liked");
        likedIcon = icons.createIcon("liked_pressed");


        setBackground(ThemeManager.getInstance().getMusicPanelColor());
        trackTitleLabel.setForeground(ThemeManager.getInstance().getForegroundColor());
        trackArtistLabel.setForeground(isDark ? new Color(0xB0B0B0) : new Color(80, 80, 80));
        currentTimeLabel.setForeground(ThemeManager.getInstance().getAccentColor());
        totalTimeLabel.setForeground(isDark ? new Color(0xB0B0B0) : new Color(80, 80, 80));

        setBackground(ThemeManager.getInstance().getMusicPanelColor());
        trackTitleLabel.setForeground(ThemeManager.getInstance().getForegroundColor());
        trackArtistLabel.setForeground(isDark ? new Color(0xB0B0B0) : new Color(80, 80, 80));
        currentTimeLabel.setForeground(ThemeManager.getInstance().getAccentColor());

        albumArtLabel.setIcon(defaultCover);
        likeButton.setIcon(isLiked ? likedIcon : heartIcon);
        playPauseButton.setIcon(isPlaying ? playIcon : pauseIcon);

        revalidate();
        repaint();
    }



    public void loadTrack(Track track) {
        this.currentTrack = track;
        trackTitleLabel.setText(track.getTitle());
        trackArtistLabel.setText(track.getArtist());

        // 🔥 Update album art
        Image albumArt = track.getAlbumArtImage();
        if (albumArt != null) {
            albumArtLabel.setIcon(new ImageIcon(
                    albumArt.getScaledInstance(450, 450, Image.SCALE_SMOOTH)
            ));
        } else {
            albumArtLabel.setIcon(defaultCover);
        }

        // Stop current playback and reset UI
        if (audioPlayer.isPlaying() || audioPlayer.isPaused()) {
            audioPlayer.stop();
            progressTimer.stop();
        }

        // Reset timers + UI
        currentTimeLabel.setText("0:00");
        totalTimeLabel.setText(formatMinutes((int) track.getDurationMs()));

        progressSlider.setValue(0);
        float value = volumeSlider.getValue() / 100f;
        audioPlayer.setVolume(value);

        playPauseButton.setIcon(pauseIcon);
        isPlaying = false;
        isLiked = false;
        likeButton.setIcon(heartIcon);

        revalidate();
        repaint();

    }

}


