package com.mycompany.vibra.Content.Main_Page.Main_Contents.Music_Player;

import java.awt.*;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.DunbarFactory;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.FontFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundPadderFactory;
import com.mycompany.vibra.service.TrackService;
import com.mycompany.vibra.Factories.Common_UI.RoundedButtonFactory;
import com.mycompany.vibra.Factories.ThemeFactory.ThemeManager;
import com.mycompany.vibra.musicUtilities.Track;
// This is the CRUCIAL import
import com.mycompany.vibra.Content.Main_Page.Main_Contents.TrackLists.TrackListPanel;

import java.util.ArrayList;
import java.util.List;

public class MainLibraryPanel extends JPanel implements ThemeManager.ThemeChangerListener {

    private final MusicPlayerPanel musicPlayerPanel;
    private final TrackListPanel trackListPanel;
    private final TrackService trackService;
    private JPanel playlistItemsContainer;

    // Keep refs so we can update them on theme change
    private JPanel libraryPanel;
    private JLabel albumLabel;
    private JLabel yourLibraryLabel;
    FontFactory fontFactory = new DunbarFactory();

    public MainLibraryPanel(MusicPlayerPanel musicPlayerPanel, TrackListPanel trackListPanel) {
        this.musicPlayerPanel = musicPlayerPanel;
        this.trackListPanel = trackListPanel; // Store the reference
        this.trackService = new TrackService();

        setLayout(new BorderLayout());
        initUI();

        // register for theme updates
        ThemeManager.getInstance().addThemeChangerListener(this);
        applyTheme();
    }

    // Your UI code, unchanged
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
//        albumLabel.setFont(FontLoaderFactory.loadFont("/fonts/DunbarTall-Bold.ttf", 36));
        albumLabel.setFont(fontFactory.createFont("dunbartall_bold", 36));
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
//        yourLibraryLabel.setFont(FontLoaderFactory.loadFont("/fonts/DunbarTall-Bold.ttf", 20f));
        yourLibraryLabel.setFont(fontFactory.createFont("dunbartall_bold", 20));
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

    private JPanel createPlaylistItem(ImageIcon cover, String name) {
        // Create the round padder container (acts as the background)
        RoundPadderFactory itemPanel = new RoundPadderFactory(15, 5, 10);
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.X_AXIS));
        itemPanel.setOpaque(false);
        itemPanel.setAlignmentX(LEFT_ALIGNMENT);
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        itemPanel.setBackground(new Color(0x53, 0x53, 0x53)); // Hover color: #535353

        // Image (scaled)
        Image scaledImg = cover.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
        JLabel artLabel = new JLabel(new ImageIcon(scaledImg));
        artLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 12));

        // Text
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(fontFactory.createFont("dunbartall_bold", 14));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentY(Component.CENTER_ALIGNMENT);

        itemPanel.add(artLabel);
        itemPanel.add(nameLabel);

        // ✅ Hover effect: highlight background on mouse enter/exit
        itemPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                itemPanel.setPaintBackground(true);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                itemPanel.setPaintBackground(false);
            }
        });

        return itemPanel;
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

    // UPDATED Upload Button
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
                        trackService.addTrackIfMissing(track);
                        System.out.println("Uploaded track: " + track.getTitle() + " | New ID: " + track.getId());
                        loadedTracks.add(track);
                    }
                }

                // Make sure we actually loaded tracks
                if (!loadedTracks.isEmpty()) {

                    //It loads the list into the panel
                    if (trackListPanel != null) {
                        trackListPanel.loadTracksIntoPanel(loadedTracks);
                    }

                    // It plays the FIRST track
                    if (musicPlayerPanel != null) {
                        // We get the first track from the list we just made
                        musicPlayerPanel.loadTrack(loadedTracks.get(0));
                    }
                }
            }
        });

        return button;
    }

    // Open Folder Button
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
                        if (track != null) {
                            trackService.addTrackIfMissing(track);
                            System.out.println("Opened track: " + track.getTitle() + " | New ID: " + track.getId());
                            tracks.add(track);
                        }
                    }

                    // Send the new list to the TrackListPanel
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