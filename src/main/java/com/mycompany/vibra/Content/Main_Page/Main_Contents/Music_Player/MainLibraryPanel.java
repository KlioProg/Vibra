package com.mycompany.vibra.Content.Main_Page.Main_Contents.Music_Player;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import com.mycompany.vibra.musicUtilities.Track;
import com.mycompany.vibra.Factories.Common_UI.RoundedButtonFactory;
import com.mycompany.vibra.Factories.Common_UI.FontLoaderFactory;
import com.mycompany.vibra.Factories.ThemeFactory.ThemeManager;

import java.util.ArrayList;
import java.util.List;

public class MainLibraryPanel extends JPanel implements ThemeManager.ThemeChangerListener {

    private final MusicPlayerPanel musicPlayerPanel;

    // Keep refs so we can update them on theme change
    private JPanel libraryPanel;
    private JLabel albumLabel;
    private JLabel yourLibraryLabel;

    public MainLibraryPanel(MusicPlayerPanel musicPlayerPanel) {
        this.musicPlayerPanel = musicPlayerPanel;

        setLayout(new BorderLayout());
        initUI();

        // register for theme updates
        ThemeManager.getInstance().addThemeChangerListener(this);
        applyTheme();
    }

    private void initUI() {
        libraryPanel = new JPanel();
        libraryPanel.setLayout(new BoxLayout(libraryPanel, BoxLayout.Y_AXIS));
        libraryPanel.setOpaque(true);
        libraryPanel.setBorder(BorderFactory.createEmptyBorder(32, 12, 0, 0));
        libraryPanel.setPreferredSize(new Dimension(402, Integer.MAX_VALUE));

        // Top panel: "Album" + buttons
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.setOpaque(false);
        topPanel.setAlignmentX(LEFT_ALIGNMENT);
        topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        albumLabel = new JLabel("Album");
        albumLabel.setFont(FontLoaderFactory.loadFont("/fonts/DunbarTall-Bold.ttf", 36));
        topPanel.add(albumLabel);
        topPanel.add(Box.createHorizontalStrut(10));

        // Buttons
        topPanel.add(createUploadButton());
        topPanel.add(Box.createHorizontalStrut(6));
        topPanel.add(createOpenFolderButton());

        libraryPanel.add(topPanel);
        libraryPanel.add(Box.createVerticalStrut(8));

        yourLibraryLabel = new JLabel("Your Library");
        yourLibraryLabel.setFont(FontLoaderFactory.loadFont("/fonts/DunbarTall-Bold.ttf", 20f));
        yourLibraryLabel.setAlignmentX(LEFT_ALIGNMENT);
        libraryPanel.add(yourLibraryLabel);

        libraryPanel.add(Box.createVerticalStrut(12));
        libraryPanel.add(createPlaylistButton());

        add(libraryPanel, BorderLayout.WEST);
    }

    // 🔹 Build a Track from file
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

    //  Styled button helper
    private RoundedButtonFactory createStyledButton(String text) {
        RoundedButtonFactory button = new RoundedButtonFactory(text, 30);
        button.setBackground(new Color(0x9D4EDD));
        button.setForeground(new Color(0xF9F6EE));
        button.setFont(FontLoaderFactory.loadFont("/fonts/DunbarTall-Bold.ttf", 16f));

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

    //  Upload Button
    private RoundedButtonFactory createUploadButton() {
        RoundedButtonFactory button = createStyledButton("Upload");

        button.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select MP3 Files");
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.setFileFilter(new FileNameExtensionFilter("MP3 Files", "mp3"));

            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                for (File selectedFile : fileChooser.getSelectedFiles()) {
                    Track track = extractTrackFromFile(selectedFile);
                    if (track != null && musicPlayerPanel != null) {
                        musicPlayerPanel.loadTrack(track);
                    }
                }
            }
        });

        return button;
    }

    //  Open Folder Button
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

                    if (!tracks.isEmpty() && musicPlayerPanel != null) {
                        // For now just play the first one
                        musicPlayerPanel.loadTrack(tracks.get(0));
                    }
                }
            }
        });

        return button;
    }

    //  Playlist Button
    private RoundedButtonFactory createPlaylistButton() {
        RoundedButtonFactory button = createStyledButton("Playlist");
        button.addActionListener(e -> {
            System.out.println("Playlist button clicked!");
            // TODO: implement playlist logic
        });
        return button;
    }

    // === THEME HANDLING ===
    private void applyTheme() {
        ThemeManager tm = ThemeManager.getInstance();
        setBackground(tm.getSidebarColor());
        libraryPanel.setBackground(tm.getSidebarColor());

        albumLabel.setForeground(tm.getForegroundColor());
        yourLibraryLabel.setForeground(tm.getForegroundColor());
    }

    @Override
    public void onThemeChanged(boolean isDarkMode) {
        applyTheme();
        repaint();
        revalidate();
    }
}
