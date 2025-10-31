package com.mycompany.vibra.Content.Main_Page.Main_Contents.Music_Player;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.DunbarFactory;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.FontFactory;
import com.mycompany.vibra.musicUtilities.Track;
import com.mycompany.vibra.Factories.Common_UI.RoundedButtonFactory;
import com.mycompany.vibra.Factories.ThemeFactory.ThemeManager;

// This is the CRUCIAL import
import com.mycompany.vibra.Content.Main_Page.Main_Contents.TrackLists.TrackListPanel;

import java.util.ArrayList;
import java.util.List;

public class MainLibraryPanel extends JPanel implements ThemeManager.ThemeChangerListener {

    private final MusicPlayerPanel musicPlayerPanel;
    private final TrackListPanel trackListPanel; // ✅ Reference to the list panel

    // Keep refs so we can update them on theme change
    private JPanel libraryPanel;
    private JLabel albumLabel;
    private JLabel yourLibraryLabel;
    FontFactory fontFactory = new DunbarFactory();

    // ✅ UPDATED CONSTRUCTOR
    public MainLibraryPanel(MusicPlayerPanel musicPlayerPanel, TrackListPanel trackListPanel) {
        this.musicPlayerPanel = musicPlayerPanel;
        this.trackListPanel = trackListPanel; // Store the reference

        setLayout(new BorderLayout());
        initUI();

        // register for theme updates
        ThemeManager.getInstance().addThemeChangerListener(this);
        applyTheme();
    }

    // Your UI code, unchanged
    private void initUI() {
        libraryPanel = new JPanel();
        libraryPanel.setLayout(new BoxLayout(libraryPanel, BoxLayout.Y_AXIS));
        libraryPanel.setOpaque(true);
        libraryPanel.setBorder(BorderFactory.createEmptyBorder(32, 12, 0, 0));
        libraryPanel.setPreferredSize(new Dimension(402, Integer.MAX_VALUE));
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.setOpaque(false);
        topPanel.setAlignmentX(LEFT_ALIGNMENT);
        topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        albumLabel = new JLabel("Album");
        albumLabel.setFont(fontFactory.createFont("dunbartall_bold", 32));
        topPanel.add(albumLabel);
        topPanel.add(Box.createHorizontalStrut(10));
        topPanel.add(createUploadButton());
        topPanel.add(Box.createHorizontalStrut(6));
        topPanel.add(createOpenFolderButton());
        libraryPanel.add(topPanel);
        libraryPanel.add(Box.createVerticalStrut(8));
        yourLibraryLabel = new JLabel("Your Library");
        yourLibraryLabel.setFont(fontFactory.createFont("dunbartall_bold", 20));
        yourLibraryLabel.setAlignmentX(LEFT_ALIGNMENT);
        libraryPanel.add(yourLibraryLabel);
        libraryPanel.add(Box.createVerticalStrut(12));
        libraryPanel.add(createPlaylistButton());
        add(libraryPanel, BorderLayout.WEST);
    }

    // Your file extraction code, unchanged
    private Track extractTrackFromFile(File file) {
        try {
            Mp3File mp3 = new Mp3File(file);
            String title = file.getName();
            String artist = "Unknown Artist";
            String album = "Unknown Album";
            int duration = (int) mp3.getLengthInSeconds();
            byte[] albumArt = null;

            if (mp3.hasId3v2Tag()) {
                ID3v2 tag = mp3.getId3v2Tag();
                if (tag.getTitle() != null) title = tag.getTitle();
                if (tag.getArtist() != null) artist = tag.getArtist();
                if (tag.getAlbum() != null) album = tag.getAlbum();
                if (tag.getAlbumImage() != null) albumArt = tag.getAlbumImage();
            }

            return new Track(title, artist, album, file.getAbsolutePath(), duration, albumArt);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // Your button styling code, unchanged
    private RoundedButtonFactory createStyledButton(String text) {
        RoundedButtonFactory button = new RoundedButtonFactory(text, 30);
        button.setBackground(new Color(0x9D4EDD));
        button.setForeground(new Color(0xF9F6EE));
        button.setFont(fontFactory.createFont("dunbartall_bold", 16));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(0x7B2CBF));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(0x9D4EDD));
                button.setForeground(new Color(0xF9F6EE));
            }
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(0x5A189A));
                button.setForeground(new Color(0x9D4EDD));
            }
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(0x7B2CBF));
                button.setForeground(new Color(0xF9F6EE));
            }
        });
        return button;
    }

    // ✅ UPDATED Upload Button
    private RoundedButtonFactory createUploadButton() {
        RoundedButtonFactory button = createStyledButton("Upload");

        button.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select MP3 Files");
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.setFileFilter(new FileNameExtensionFilter("MP3 Files", "mp3"));

            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                List<Track> loadedTracks = new ArrayList<>();
                for (File selectedFile : fileChooser.getSelectedFiles()) {
                    Track track = extractTrackFromFile(selectedFile);
                    if (track != null) {
                        loadedTracks.add(track);
                    }
                }

                // Make sure we actually loaded tracks
                if (!loadedTracks.isEmpty()) {

                    // ✅ 1. This is your new code: It loads the list into the panel
                    if (trackListPanel != null) {
                        trackListPanel.loadTracksIntoPanel(loadedTracks);
                    }

                    // ✅ 2. This is your old code: It plays the FIRST track
                    if (musicPlayerPanel != null) {
                        // We get the first track from the list we just made
                        musicPlayerPanel.loadTrack(loadedTracks.get(0));
                    }
                }
            }
        });

        return button;
    }

    // ✅ UPDATED Open Folder Button
    private RoundedButtonFactory createOpenFolderButton() {
        RoundedButtonFactory button = createStyledButton("Open Folder");

        button.addActionListener(e -> {
            JFileChooser folderChooser = new JFileChooser();
            folderChooser.setDialogTitle("Select Music Folder");
            folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int result = folderChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File folder = folderChooser.getSelectedFile();
                File[] mp3Files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3"));

                if (mp3Files != null) {
                    List<Track> tracks = new ArrayList<>();
                    for (File file : mp3Files) {
                        Track track = extractTrackFromFile(file);
                        if (track != null) tracks.add(track);
                    }

                    // ✅ Send the new list to the TrackListPanel
                    if (!tracks.isEmpty() && trackListPanel != null) {
                        trackListPanel.loadTracksIntoPanel(tracks);
                    }
                }
            }
        });

        return button;
    }

    // Your playlist button code, unchanged
    private RoundedButtonFactory createPlaylistButton() {
        RoundedButtonFactory button = createStyledButton("Playlist");
        button.addActionListener(e -> {
            System.out.println("Playlist button clicked!");
        });
        return button;
    }

    // Your theme code, unchanged
    private void applyTheme() {
        ThemeManager tm = ThemeManager.getInstance();
        setBackground(tm.getSidebarColor());
        libraryPanel.setBackground(tm.getSidebarColor());

        albumLabel.setForeground(tm.getForegroundColor());
        yourLibraryLabel.setForeground(tm.getForegroundColor());
    }

    // Your theme code, unchanged
    @Override
    public void onThemeChanged(boolean isDarkMode) {
        applyTheme();
        repaint();
        revalidate();
    }
}