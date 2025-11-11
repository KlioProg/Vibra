package com.mycompany.vibra.Content.Main_Page.Main_Contents.Music_Player;

import java.awt.*;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.util.Map;
import java.util.List;
import com.mycompany.vibra.musicUtilities.Track;
import com.mycompany.vibra.musicUtilities.TrackLoader;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.DunbarFactory;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.FontFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundPadderFactory;
import com.mycompany.vibra.service.TrackService;
import com.mycompany.vibra.Factories.Common_UI.RoundedButtonFactory;
import com.mycompany.vibra.Factories.ThemeFactory.ThemeManager;
import com.mycompany.vibra.musicUtilities.Track;
import com.mycompany.vibra.Content.Main_Page.Main_Contents.Album_Panel.AlbumPanel;
// This is the CRUCIAL import
import com.mycompany.vibra.Content.Main_Page.Main_Contents.TrackLists.TrackListPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainLibraryPanel extends JPanel implements ThemeManager.ThemeChangerListener {

    private final MusicPlayerPanel musicPlayerPanel;
    private final TrackListPanel trackListPanel;
    private final AlbumPanel albumPanel;
    private final TrackService trackService;
    private JPanel playlistItemsContainer;

    // Keep refs so we can update them on theme change
    private JPanel libraryPanel;
    private JLabel albumLabel;
    private JLabel yourLibraryLabel;
    FontFactory fontFactory = new DunbarFactory();

    public MainLibraryPanel(MusicPlayerPanel musicPlayerPanel, TrackListPanel trackListPanel, AlbumPanel albumPanel) {
        this.musicPlayerPanel = musicPlayerPanel;
        this.trackListPanel = trackListPanel; // Store the reference
        this.albumPanel = albumPanel;
        this.trackService = new TrackService();

        setLayout(new BorderLayout());
        initUI();

        // register for theme updates
        ThemeManager.getInstance().addThemeChangerListener(this);
        applyTheme();

        String hardcodedScanPath = "/Users/eeeuweee/Music/vibramusic";

        new ScanWorker(hardcodedScanPath).execute();
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
            int trackNum = 0;
            byte[] albumArt = null;

            if (mp3.hasId3v2Tag()) {
                ID3v2 tag = mp3.getId3v2Tag();
                if (tag.getTitle() != null) title = tag.getTitle();
                if (tag.getArtist() != null) artist = tag.getArtist();
                if (tag.getAlbum() != null) album = tag.getAlbum();
                String trackStr = tag.getTrack(); // e.g., "1/12" or "1"
                if (trackStr != null && !trackStr.isEmpty()) {
                    try {
                        // Get the part before any "/"
                        String numberOnly = trackStr.split("/")[0];
                        trackNum = Integer.parseInt(numberOnly);
                    } catch (NumberFormatException e) {
                        // The tag was weird, just ignore it and use 0
                    }
                }
                if (tag.getAlbumImage() != null) albumArt = tag.getAlbumImage();
            }

            return new Track(title, artist, album, file.getAbsolutePath(), duration, trackNum, albumArt);
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

    // Your playlist button code, unchanged
    private RoundedButtonFactory createPlaylistButton() {
        RoundedButtonFactory button = createStyledButton("Playlist");
        button.addActionListener(e -> {
            System.out.println("Playlist button clicked!");
        });
        return button;
    }

    // --- ADD THIS ENTIRE INNER CLASS ---
    
    /**
     * A background task to scan a directory for .mp3 files
     * without freezing the application UI.
     */
    private class ScanWorker extends SwingWorker<Map<String, List<Track>>, Void> {
        private final String scanPath;

        /**
         * Creates a new worker that will scan the specified path.
         * @param path The absolute file path to scan (e.g., "/Users/You/Music/MyFolder")
         */
        public ScanWorker(String path) {
            this.scanPath = path;
        }

        @Override
        protected Map<String, List<Track>> doInBackground() throws Exception {
            System.out.println("ScanWorker: Starting scan...");

            // 1. Use your new loader to get all tracks
            List<Track> foundTracks = TrackLoader.loadTracks(scanPath);

            // 2. Save each track to the database (this updates their IDs)
            for (Track track : foundTracks) {
                trackService.addTrackIfMissing(track);
            }

            // 3. Group the tracks by album
            Map<String, List<Track>> albums = foundTracks.stream()
                .collect(Collectors.groupingBy(Track::getAlbum));

            return albums;
        }

        @Override
        protected void done() {
            try {
                Map<String, List<Track>> albums = get();

                albumPanel.displayRealAlbums(albums);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    // --- END OF SCANWORKER CLASS ---

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