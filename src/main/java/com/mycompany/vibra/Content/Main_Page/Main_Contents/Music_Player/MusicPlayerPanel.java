package com.mycompany.vibra.Content.Main_Page.Main_Contents.Music_Player;

import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.DunbarFactory;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.FontFactory;
import com.mycompany.vibra.Factories.Common_UI.HoverPopUpMessageFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.ButtonIconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.DarkModeIconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.IconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.LightModeIconFactory;
import com.mycompany.vibra.Factories.ThemeFactory.DarkModeToggle;
import com.mycompany.vibra.Factories.ThemeFactory.ThemeManager;
import com.mycompany.vibra.musicUtilities.AudioPlayer;
import com.mycompany.vibra.Factories.Music_UI.CustomSliderUI;
import com.mycompany.vibra.model.Playlist; //playlist import
import com.mycompany.vibra.model.Observer; //observer import
import com.mycompany.vibra.model.TrackIterator; //iterator import
import com.mycompany.vibra.Content.Main_Page.Main_Contents.Like_Panel.LikedPanel; // ADD THIS IMPORT

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicSliderUI;
import com.mycompany.vibra.dao.LikedSongsDao;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import java.awt.*;

import com.mycompany.vibra.musicUtilities.Mp3Utils;
import com.mycompany.vibra.musicUtilities.Track;

import static com.mycompany.vibra.musicUtilities.Mp3Utils.formatMinutes;

public class MusicPlayerPanel extends JPanel implements Observer{

    private final AudioPlayer audioPlayer;
    private final IconFactory icons;
    private final int currentUserID;
    private IconFactory themeIcons;
    private final LikedSongsDao likedSongsDao;
    private List<Track> likedTracksList;

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
    private volatile boolean isPlaying = false; // Use volatile
    public boolean isLiked = false; // boolean for likedpanel.

    public Playlist currentPlaylist; //new added
    private TrackIterator playlistIterator;
    FontFactory fontFactory = new DunbarFactory();


    // we only keep references, icons come from factory
    private ImageIcon playIcon, pauseIcon, heartIcon, likedIcon, defaultCover, themeButton;
    private final LikedPanel likedPanel;

    public MusicPlayerPanel(AudioPlayer audioPlayer, Playlist playlist, LikedPanel likedPanel, int userId) {
        this.audioPlayer = audioPlayer;
        this.likedPanel = likedPanel; // Store the reference
        this.currentUserID = userId;
        this.likedSongsDao = new LikedSongsDao();
        this.icons = new ButtonIconFactory();

        loadLikedTracksCache();

        this.themeIcons = ThemeManager.getInstance().isDarkMode() ? new DarkModeIconFactory() : new LightModeIconFactory();

         //for the observer
        this.currentPlaylist = playlist;
        this.currentPlaylist.addObserver(this); // Register as an observer
        this.playlistIterator = currentPlaylist.createIterator();
        // end for this new added

        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(30, 40, 30, 40));
        ThemeManager.getInstance().addThemeChangerListener(isDark -> applyTheme(isDark));

        initUI();
        initTimer();
        applyTheme(ThemeManager.getInstance().isDarkMode());
    }

    private void loadLikedTracksCache() {
        // Loads all liked songs into a list for fast checking
        try {
            this.likedTracksList = likedSongsDao.listLikedByUser(currentUserID);

            // Also, tell the LikedPanel to update its UI
            likedPanel.setSongs(this.likedTracksList);

        } catch (SQLException e) {
            e.printStackTrace();
            // Failed to load liked songs
        }
    }

    private void initUI() {
        HoverPopUpMessageFactory hoverPopup = new HoverPopUpMessageFactory();
        playIcon = icons.createIcon("play");
        pauseIcon = icons.createIcon("pause");
        ImageIcon backIcon = icons.createIcon("back");
        ImageIcon skipIcon = icons.createIcon("skip");
        ImageIcon soundDownIcon = icons.createIcon("sound_down");
        ImageIcon soundUpIcon = icons.createIcon("sound_up");
        heartIcon = icons.createIcon("liked");
        likedIcon = icons.createIcon("liked_pressed");
        defaultCover = themeIcons.createIcon("default_cover");
        themeButton = themeIcons.createIcon("theme_button");

        // ===== Album art =====
        albumArtLabel = new JLabel(defaultCover);
        albumArtLabel.setHorizontalAlignment(SwingConstants.CENTER);
        albumArtLabel.setPreferredSize(new Dimension(300, 300));
        JPanel albumPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        albumPanel.setOpaque(false);
        albumPanel.add(albumArtLabel);

        // ===== Track Info Panel =====
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(20, 0, 15, 0));
        trackTitleLabel = new JLabel("Track Title", SwingConstants.CENTER);
        trackTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        trackTitleLabel.setForeground(new Color(0xFFFFFF));
        trackTitleLabel.setFont(fontFactory.createFont("dunbartall_bold", 16));
        trackTitleLabel.setMaximumSize(new Dimension(300, 30));
        trackTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        trackTitleLabel.setVerticalAlignment(SwingConstants.CENTER);
        trackArtistLabel = new JLabel("Artist", SwingConstants.CENTER);
        trackArtistLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        trackArtistLabel.setForeground(new Color(0xC0C0C0));
        trackArtistLabel.setFont(fontFactory.createFont("dunbartall_bold", 13));
        trackArtistLabel.setMaximumSize(new Dimension(250, 25));
        trackArtistLabel.setHorizontalAlignment(SwingConstants.CENTER);
        trackArtistLabel.setVerticalAlignment(SwingConstants.CENTER);
        infoPanel.add(trackTitleLabel);
        infoPanel.add(Box.createVerticalStrut(4));
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
        currentTimeLabel.setFont(fontFactory.createFont("dunbartall_bold", 14));
        totalTimeLabel = new JLabel("0:00");
        totalTimeLabel.setForeground(new Color(0xB0B0B0));
        totalTimeLabel.setFont(fontFactory.createFont("dunbartall_bold", 14));
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
        playPauseButton = new JButton(playIcon);
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
        hoverPopup.attachHoverPopup(likeButton, "Like to add music to your liked playlist!");
        likeButton.setBorderPainted(false);
        likeButton.setContentAreaFilled(false);
        likeButton.setFocusPainted(false);
        likeButton.setOpaque(false);
        likeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        DarkModeToggle darkModeToggle = new DarkModeToggle();
        rightPanel.add(likeButton);
        rightPanel.add(darkModeToggle);
        topPanel.add(rightPanel, BorderLayout.EAST);

        JLabel nowPlayingLabel = new JLabel("Now playing");
        nowPlayingLabel.setForeground(new Color(0xB0B0B0));
        nowPlayingLabel.setFont(fontFactory.createFont("dunbartall_bold", 16));
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

        nowPlayingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        albumPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        albumArtLabel.setPreferredSize(new Dimension(300, 300));
        centerPanel.add(Box.createVerticalStrut(0));
        centerPanel.add(nowPlayingLabel);

        centerPanel.add(Box.createVerticalStrut(-16));
        centerPanel.add(albumPanel);

        centerPanel.add(Box.createVerticalStrut(0));
        centerPanel.add(infoPanel);

        centerPanel.add(Box.createVerticalGlue());
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(controlsPanel, BorderLayout.SOUTH);

        playPauseButton.addActionListener(e -> {
            if (currentTrack == null) return;

            if (audioPlayer.isPlaying()) {
                audioPlayer.pause();
                progressTimer.stop();
                isPlaying = false;
            } else {
                if (audioPlayer.isPaused()) {
                    audioPlayer.resume();
                } else {
                    audioPlayer.play(currentTrack);
                }
                progressTimer.start();
                isPlaying = true;
            }

            updatePlayPauseIcon();
        });

        // listeners for iterator
        prevButton.addActionListener(e -> {
            if (currentTrack == null) return;
            int currentIndex = currentPlaylist.getTracks().indexOf(currentTrack);
            if (currentIndex > 0) {
                // This will trigger the update() method, which calls loadTrack()
                currentPlaylist.setCurrentTrackIndex(currentIndex - 1);
            }
        });

        nextButton.addActionListener(e -> {
            if (currentTrack == null) return;
            int currentIndex = currentPlaylist.getTracks().indexOf(currentTrack);
            // Check if it's not the last track
            if (currentIndex != -1 && currentIndex < currentPlaylist.getTracks().size() - 1) {
                // This will trigger the update() method, which calls loadTrack()
                currentPlaylist.setCurrentTrackIndex(currentIndex + 1);
            }
        });

//        // listeners for iterator
//        prevButton.addActionListener(e -> {
//            if (playlistIterator.hasPrevious()) {
//                Track prevTrack = playlistIterator.previous();
//                currentPlaylist.setCurrentTrackIndex(currentPlaylist.getTracks().indexOf(prevTrack));
//            }
//        });
//
//        nextButton.addActionListener(e -> {
//            if (playlistIterator.hasNext()) {
//                Track nextTrack = playlistIterator.next();
//                currentPlaylist.setCurrentTrackIndex(currentPlaylist.getTracks().indexOf(nextTrack));
//            }
//        });

//--liked button listener logic
       likeButton.addActionListener(e -> {
            if (currentTrack == null || currentTrack.getId() == -1) {
                // Not a valid, saved track
                return;
            }

            boolean currentlyLiked = isTrackInCache(currentTrack);

            try {
                if (currentlyLiked) {
                    // --- UNLIKE IT ---

                    // First, try to unlike in the DB
                    if (likedSongsDao.unlike(currentUserID, currentTrack.getId())) {
                        // --- Success! --- (The DAO will print the console log)
                        // Now, update the UI and cache
                        likedTracksList.removeIf(t -> t.getId() == currentTrack.getId());
                        likedPanel.removeSong(currentTrack);
                        likeButton.setIcon(heartIcon);
                        isLiked = false;
                    }

                } else {
                    // --- LIKE IT ---

                    // First, try to like in the DB
                    if (likedSongsDao.like(currentUserID, currentTrack.getId())) {
                        // --- Success! --- (The DAO will print the console log)
                        // Now, update the UI and cache
                        likedTracksList.add(currentTrack);
                        likedPanel.addSong(currentTrack);
                        likeButton.setIcon(likedIcon);
                        isLiked = true;
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        volumeSlider.addChangeListener(e -> {
            float value = volumeSlider.getValue() / 100f;
            audioPlayer.setVolume(value);
        });

        // REPLACE the progressSlider.addChangeListener with this:
        progressSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (currentTrack == null) return;

                // Stop the timer while we're interacting
                if (isPlaying) {
                    progressTimer.stop();
                }

                // --- THIS IS THE NEW "TAP-TO-SEEK" LOGIC ---

                // Get the UI component that handles the slider's appearance
                javax.swing.plaf.SliderUI sliderUI = progressSlider.getUI();

                // Ask the UI to calculate the slider's value based on the mouse's X position
                int value = ((BasicSliderUI) sliderUI).valueForXPosition(e.getX());

                // Manually set the slider's value to where the user clicked
                progressSlider.setValue(value);

                // --- END OF NEW LOGIC ---
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                if (currentTrack == null) return;

                // Calculate new position in milliseconds
                long newPos = (long) (currentTrack.getDurationMs() *
                        (progressSlider.getValue() / 1000.0));

                // Call our new seek method!
                audioPlayer.seek(newPos);

                // Restart timer and set UI to "playing"
                progressTimer.start();
                playPauseButton.setIcon(pauseIcon);
                isPlaying = true;
            }
        });
    }

    public void syncLikeStatus(Track track, boolean isNowLiked) {

        // Remove from cache if unliked

        if (!isNowLiked) {

            likedTracksList.removeIf(t -> t.getId() == track.getId());

        } else {

            // Add to cache if liked (if not already there)

            if (likedTracksList.stream().noneMatch(t -> t.getId() == track.getId())) {

                likedTracksList.add(track);

            }

        }



        // If this is the track currently playing, update the icon

        if (currentTrack != null && currentTrack.getId() == track.getId()) {

            this.isLiked = isNowLiked;

            likeButton.setIcon(isNowLiked ? likedIcon : heartIcon);

        }

    }

    public void clearLikedCache() {

        likedTracksList.clear();



        // If a liked song is currently playing, update its icon

        if (isLiked) {

            isLiked = false;

            likeButton.setIcon(heartIcon);

        }

    }

    // ADD THIS METHOD BACK (or ensure it's correct):
    private void initTimer() {
        progressTimer = new Timer(500, e -> {
            if (currentTrack != null && (audioPlayer.isPlaying() || audioPlayer.isPaused())) {
                long pos = audioPlayer.getCurrentPosition();
                long dur = currentTrack.getDurationMs();

                if (dur > 0) {
                    int value = (int) ((pos / (double) dur) * 1000);
                    progressSlider.setValue(value);
                    currentTimeLabel.setText(formatMinutes((int) pos));
                }
            }

            // Check if song finished naturally
            if (currentTrack != null && !audioPlayer.isPlaying() && !audioPlayer.isPaused() && isPlaying) {
                // It finished.
                long pos = audioPlayer.getCurrentPosition();
                long dur = currentTrack.getDurationMs();

                // Check if we are at the end (within 1.5 seconds)
                if (pos >= dur - 1500) {
                    isPlaying = false;
                    updatePlayPauseIcon();
                    progressTimer.stop();
                    progressSlider.setValue(1000); // Set to end
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

        // Only replace album art if there is NO album cover
        if (albumArtLabel.getIcon() == null ||
                (currentTrack == null || currentTrack.getAlbumArtImage() == null)) {
            albumArtLabel.setIcon(defaultCover);
        }

        likeButton.setIcon(isLiked ? likedIcon : heartIcon);
        updatePlayPauseIcon();



        revalidate();
        repaint();
    }

    // --- ADD THIS NEW HELPER METHOD ---
    private boolean isTrackInCache(Track track) {
        if (track == null || track.getId() == -1) return false;

        // Check if any track in our list has the same ID
        return likedTracksList.stream().anyMatch(t -> t.getId() == track.getId());
    }

    // REPLACE your loadTrack method with this:
    public void loadTrack(Track track) {
        this.currentTrack = track;

        // Find the track in the main playlist and update the index
        currentPlaylist.setCurrentTrackIndex(currentPlaylist.getTracks().indexOf(track));

        trackTitleLabel.setText(track.getTitle());
        trackArtistLabel.setText(track.getArtist());

        Image albumArt = track.getAlbumArtImage();
        if (albumArt != null) {
            albumArtLabel.setIcon(new ImageIcon(
                    albumArt.getScaledInstance(265, 265, Image.SCALE_SMOOTH)
            ));
        } else {
            albumArtLabel.setIcon(defaultCover);
        }

        audioPlayer.stop();
        progressTimer.stop();

        currentTimeLabel.setText("0:00");
        totalTimeLabel.setText(formatMinutes((int) track.getDurationMs()));
        progressSlider.setValue(0);

        float value = volumeSlider.getValue() / 100f;
        audioPlayer.setVolume(value);

        audioPlayer.play(track);
        progressTimer.start();
        isPlaying = true;
        updatePlayPauseIcon();



        if (isTrackInCache(currentTrack)) {
            isLiked = true;
            likeButton.setIcon(likedIcon);
        } else {
            isLiked = false;
            likeButton.setIcon(heartIcon);
        }
        // --- END NEW CHECK ---

        revalidate();
        repaint();
    }

    @Override //for update method
    public void update() {
        Track newTrack = currentPlaylist.getCurrentTrack();
        // Only load if the track is different from the one currently playing
        // This prevents a recursive loop where loadTrack -> setCurrentTrackIndex -> update -> loadTrack
        if (newTrack != null && !newTrack.equals(this.currentTrack)) {
            loadTrack(newTrack);
        }
    }

    private void updatePlayPauseIcon() {
        if (isPlaying) {
            playPauseButton.setIcon(pauseIcon);
        } else {
            playPauseButton.setIcon(playIcon);
        }
    }


}



