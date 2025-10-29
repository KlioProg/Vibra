 package com.mycompany.vibra.Content.Main_Page.Main_Contents.Library_Panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import com.mycompany.vibra.Content.Main_Page.Main_Contents.Music_Player.MusicPlayerPanel;
import com.mycompany.vibra.musicUtilities.Track;
import com.mycompany.vibra.Factories.Common_UI.RoundedButtonFactory;
import com.mycompany.vibra.Factories.Common_UI.FontLoaderFactory;
import com.mycompany.vibra.Factories.ThemeFactory.ThemeManager;

import com.mycompany.vibra.Content.Main_Page.Main_Contents.TrackLists.TrackListPanel;

import java.util.ArrayList;
import java.util.List;

public class MainLibraryPanel extends JPanel implements ThemeManager.ThemeChangerListener {

    private final MusicPlayerPanel musicPlayerPanel;
    private final TrackListPanel trackListPanel;

    private JPanel libraryPanel; // This will now be the main container with BorderLayout
    private JLabel albumLabel;
    private JLabel yourLibraryLabel;

    // This panel will hold the playlist items and be scrollable
    private JPanel playlistItemsContainer;

    public MainLibraryPanel(MusicPlayerPanel musicPlayerPanel, TrackListPanel trackListPanel) {
        this.musicPlayerPanel = musicPlayerPanel;
        this.trackListPanel = trackListPanel;

        // We set the layout for this MainLibraryPanel itself
        setLayout(new BorderLayout());
        initUI();

        ThemeManager.getInstance().addThemeChangerListener(this);
        applyTheme();
    }

    /**
     * ✅ YOUR UI METHOD, NOW WITH A SCROLLPANE
     * (based on the code you just sent)
     */
    private void initUI() {
        // 1. This is the MAIN panel for this class
        libraryPanel = new JPanel();
        libraryPanel.setLayout(new BorderLayout(0, 12)); // BorderLayout with 12px vertical gap
        libraryPanel.setOpaque(false);
        // Add padding to the main panel
        libraryPanel.setBorder(BorderFactory.createEmptyBorder(32, 12, 0, 12));

        // 2. Top Panel (Header) - Unchanged
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.setOpaque(false);
        topPanel.setAlignmentX(LEFT_ALIGNMENT);
        topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        albumLabel = new JLabel("Album");
        albumLabel.setFont(FontLoaderFactory.loadFont("/fonts/DunbarTall-Bold.ttf", 36));
        topPanel.add(albumLabel);
        topPanel.add(Box.createHorizontalStrut(10));
        topPanel.add(createUploadButton());
        topPanel.add(Box.createHorizontalStrut(6));
        topPanel.add(createOpenFolderButton());

        // Add topPanel to the NORTH of libraryPanel
        libraryPanel.add(topPanel, BorderLayout.NORTH);

        // 3. This is your new container, just like 'trackListContainer'
        playlistItemsContainer = new JPanel();
        playlistItemsContainer.setLayout(new BoxLayout(playlistItemsContainer, BoxLayout.Y_AXIS));
        playlistItemsContainer.setOpaque(false);

        // 4. Add items to the scrollable container
        yourLibraryLabel = new JLabel("Your Library");
        yourLibraryLabel.setFont(FontLoaderFactory.loadFont("/fonts/DunbarTall-Bold.ttf", 20f));
        yourLibraryLabel.setAlignmentX(LEFT_ALIGNMENT);
        playlistItemsContainer.add(yourLibraryLabel);

        playlistItemsContainer.add(Box.createVerticalStrut(12));
        playlistItemsContainer.add(createPlaylistButton());

        playlistItemsContainer.add(Box.createVerticalStrut(20));

        JPanel item1 = createPlaylistItem(
                new ImageIcon(getClass().getResource("/placeholders/car.png")),
                "I'll be better for me.."
        );
        playlistItemsContainer.add(item1);
        playlistItemsContainer.add(Box.createVerticalStrut(15));

        JPanel item2 = createPlaylistItem(
                new ImageIcon(getClass().getResource("/placeholders/cd.png")),
                "best rnb playlist"
        );
        playlistItemsContainer.add(item2);
        playlistItemsContainer.add(Box.createVerticalStrut(15));

        JPanel item3 = createPlaylistItem(
                new ImageIcon(getClass().getResource("/placeholders/lion.png")),
                "OG Post Malone"
        );
        playlistItemsContainer.add(item3);
        playlistItemsContainer.add(Box.createVerticalStrut(15));

        JPanel item4 = createPlaylistItem(
                new ImageIcon(getClass().getResource("/placeholders/dk.png")),
                "Drake"
        );
        playlistItemsContainer.add(item4);

        playlistItemsContainer.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(playlistItemsContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        libraryPanel.add(scrollPane, BorderLayout.CENTER);

        add(libraryPanel, BorderLayout.CENTER);
    }

    /**
     * HELPER METHOD (from last time, unchanged)
     * Creates a simple playlist row with an image and text.
     */
    private JPanel createPlaylistItem(ImageIcon cover, String name) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.X_AXIS));
        itemPanel.setOpaque(false);
        itemPanel.setAlignmentX(LEFT_ALIGNMENT);
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // Image
        Image scaledImg = cover.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
        JLabel artLabel = new JLabel(new ImageIcon(scaledImg));
        artLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 12));

        // Text
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(FontLoaderFactory.loadFont("/fonts/DunbarTall-Bold.ttf", 14f));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentY(Component.CENTER_ALIGNMENT);

        itemPanel.add(artLabel);
        itemPanel.add(nameLabel);

        return itemPanel;
    }

    // --- (Rest of your methods are unchanged) ---

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

                if (!loadedTracks.isEmpty()) {
                    if (trackListPanel != null) {
                        trackListPanel.loadTracksIntoPanel(loadedTracks);
                    }
                    if (musicPlayerPanel != null) {
                        musicPlayerPanel.loadTrack(loadedTracks.get(0));
                    }
                }
            }
        });
        return button;
    }

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

                    if (!tracks.isEmpty()) {
                        if (trackListPanel != null) {
                            trackListPanel.loadTracksIntoPanel(tracks);
                        }
                        if (musicPlayerPanel != null) {
                            musicPlayerPanel.loadTrack(tracks.get(0));
                        }
                    }
                }
            }
        });
        return button;
    }

    private RoundedButtonFactory createPlaylistButton() {
        RoundedButtonFactory button = createStyledButton("Playlist");
        button.addActionListener(e -> {
            System.out.println("Playlist button clicked!");
        });
        return button;
    }

    private void applyTheme() {
        ThemeManager tm = ThemeManager.getInstance();
        Color bgColor = tm.getSidebarColor();
        setBackground(bgColor); // Set this panel's background

        if (libraryPanel != null) {
            libraryPanel.setBackground(bgColor); // Set the main container's background
        }
        if (playlistItemsContainer != null) {
            playlistItemsContainer.setOpaque(false); // Make sure this is see-through
        }

        if (albumLabel != null) {
            albumLabel.setForeground(tm.getForegroundColor());
        }
        if (yourLibraryLabel != null) {
            yourLibraryLabel.setForeground(tm.getForegroundColor());
        }

        // TODO: You will need to iterate through playlistItemsContainer
        // and update the text color on your JLabels (e.g., "best rnb playlist")
        // when the theme changes.
    }

    @Override
    public void onThemeChanged(boolean isDarkMode) {
        applyTheme();
        repaint();
        revalidate();
    }
}
