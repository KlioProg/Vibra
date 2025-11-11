package com.mycompany.vibra.Content.Main_Page.Main_Contents.Music_Player;

import java.awt.*;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

// --- MERGED IMPORTS ---
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
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.ButtonIconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.DarkModeIconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.IconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.LightModeIconFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundedIconButtonFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundedPanelFactory;
import com.mycompany.vibra.model.Observer;
import com.mycompany.vibra.model.Playlist;
import com.mycompany.vibra.Factories.Common_UI.RoundedButtonFactory;
import com.mycompany.vibra.Factories.ThemeFactory.ThemeManager;
import com.mycompany.vibra.Content.Main_Page.Main_Contents.Album_Panel.AlbumPanel;
import com.mycompany.vibra.Content.Main_Page.Main_Contents.TrackLists.TrackListPanel;
import com.mycompany.vibra.service.TrackService; // From Final-Vibra
import java.util.ArrayList;
import java.util.stream.Collectors;

public class MainLibraryPanel extends JPanel implements ThemeManager.ThemeChangerListener {

    // --- MERGED FIELDS ---
    private final MusicPlayerPanel musicPlayerPanel;
    private final TrackListPanel trackListPanel;
    private final AlbumPanel albumPanel; // From Final-Vibra
    private final TrackService trackService; // From Final-Vibra
    private JPanel playlistItemsContainer;
    private IconFactory themeIcons; // From HEAD

    // Keep refs so we can update them on theme change
    private JPanel libraryPanel;
    private JLabel albumLabel;
    private JLabel yourLibraryLabel;
    FontFactory fontFactory = new DunbarFactory();

    // --- MERGED CONSTRUCTOR ---
    public MainLibraryPanel(MusicPlayerPanel musicPlayerPanel, TrackListPanel trackListPanel, AlbumPanel albumPanel) {
        this.musicPlayerPanel = musicPlayerPanel;
        this.trackListPanel = trackListPanel;
        this.albumPanel = albumPanel;
        this.trackService = new TrackService();
        
        // Initialize theme icons (from HEAD)
        this.themeIcons = ThemeManager.getInstance().isDarkMode() ? new DarkModeIconFactory() : new LightModeIconFactory();

        setLayout(new BorderLayout());
        initUI();

        ThemeManager.getInstance().addThemeChangerListener(this);
        applyTheme(); // Apply theme after initUI

        // Start hardcoded scan (from Final-Vibra)
        String hardcodedScanPath = "/Users/eeeuweee/Music/vibramusic"; // ‼️ CHANGE THIS PATH
        new ScanWorker(hardcodedScanPath).execute();
    }

    // --- MERGED initUI (uses friend's 'createCreatePlaylistButton') ---
    private void initUI() {
        // 1. This is the MAIN panel for this class
        libraryPanel = new JPanel();
        libraryPanel.setLayout(new BorderLayout(0, 12)); // BorderLayout with 12px vertical gap
        libraryPanel.setOpaque(false);
        libraryPanel.setBorder(BorderFactory.createEmptyBorder(32, 12, 0, 12));

        // 2. Top Panel (Header)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.setOpaque(false);
        topPanel.setAlignmentX(LEFT_ALIGNMENT);
        topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        albumLabel = new JLabel("Album");
        albumLabel.setFont(fontFactory.createFont("dunbartall_bold", 36));
        topPanel.add(albumLabel);

        libraryPanel.add(topPanel, BorderLayout.NORTH);

        // 3. This is your new container
        playlistItemsContainer = new JPanel();
        playlistItemsContainer.setLayout(new BoxLayout(playlistItemsContainer, BoxLayout.Y_AXIS));
        playlistItemsContainer.setOpaque(false);

        // 4. Add items to the scrollable container
        yourLibraryLabel = new JLabel("Your Library");
        yourLibraryLabel.setFont(fontFactory.createFont("dunbartall_bold", 20));
        yourLibraryLabel.setAlignmentX(LEFT_ALIGNMENT);
        playlistItemsContainer.add(yourLibraryLabel);

        playlistItemsContainer.add(Box.createVerticalStrut(12));
        
        // --- USING FRIEND'S "CREATE PLAYLIST" BUTTON ---
        playlistItemsContainer.add(createCreatePlaylistButton()); // From HEAD

        playlistItemsContainer.add(Box.createVerticalStrut(20));
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

    // --- USING FRIEND'S ADVANCED 'createPlaylistItem' (from HEAD) ---
    private JPanel createPlaylistItem(final Playlist playlist) {
        ThemeManager tm = ThemeManager.getInstance();
        Color baseColor = tm.getSidebarColor();
        Color hoverColor = new Color(0x535353); // The gray hover

        RoundedPanelFactory itemPanel = new RoundedPanelFactory(
                15, baseColor, null, 0, 0, 90
        );
        itemPanel.setLayout(new BorderLayout(12, 0));
        itemPanel.setAlignmentX(LEFT_ALIGNMENT);
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        itemPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        itemPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Image scaledImg = playlist.getCover().getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
        final JLabel artLabel = new JLabel(new ImageIcon(scaledImg));
        artLabel.setOpaque(false);

        RoundedPanelFactory coverClipper = new RoundedPanelFactory(
                10, Color.BLACK, null, 0, 70, 70
        );
        coverClipper.setLayout(new BorderLayout());
        coverClipper.add(artLabel, BorderLayout.CENTER);
        itemPanel.add(coverClipper, BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        final JLabel nameLabel = new JLabel(playlist.getText());
        nameLabel.setFont(fontFactory.createFont("dunbartall_bold", 14));
        nameLabel.setForeground(tm.getForegroundColor());
        nameLabel.setName("PLAYLIST_NAME_LABEL"); 

        final JLabel bioLabel = new JLabel(playlist.getBio());
        bioLabel.setFont(fontFactory.createFont("dunbartall_book", 12));
        bioLabel.setForeground(tm.isDarkMode() ? Color.LIGHT_GRAY : Color.DARK_GRAY);
        bioLabel.setName("PLAYLIST_BIO_LABEL"); 

        textPanel.add(nameLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(bioLabel);
        textPanel.add(Box.createVerticalGlue());
        itemPanel.add(textPanel, BorderLayout.CENTER);

        ImageIcon editIcon = themeIcons.createIcon("edit");
        JButton editButton = RoundedIconButtonFactory.createIconButton(
                editIcon, null, 34, "Edit Playlist"
        );
        editButton.setName("EDIT_PLAYLIST_BUTTON");
        itemPanel.add(editButton, BorderLayout.EAST);

        itemPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                itemPanel.setBackgroundColor(hoverColor);
                nameLabel.setForeground(Color.WHITE);
                bioLabel.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                ThemeManager tm = ThemeManager.getInstance();
                itemPanel.setBackgroundColor(tm.getSidebarColor());
                nameLabel.setForeground(tm.getForegroundColor());
                bioLabel.setForeground(tm.isDarkMode() ? Color.LIGHT_GRAY : Color.DARK_GRAY);
            }
        });

        itemPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getSource() == editButton || SwingUtilities.isDescendingFrom(e.getComponent(), editButton)) {
                    return;
                }
                System.out.println("Clicked to play playlist: " + playlist.getText());
                // TODO: Re-enable this when playlists hold tracks
                // trackListPanel.loadTracksIntoPanel(playlist.getTracks());
            }
        });

        editButton.addActionListener(e -> {
            CreatePlaylistPanel createPanel = new CreatePlaylistPanel(
                    playlist.getText(), playlist.getBio(), playlist.getCover()
            );

            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(itemPanel), "Edit Playlist", true);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setUndecorated(true);
            dialog.setBackground(new Color(0, 0, 0, 0));
            dialog.setContentPane(createPanel);
            dialog.pack();
            dialog.setLocationRelativeTo(itemPanel);
            dialog.setVisible(true);

            if (createPanel.isPlaylistCreated()) {
                playlist.setText(createPanel.getPlaylistName());
                playlist.setBio(createPanel.getPlaylistBio());
                playlist.setCover(createPanel.getPlaylistCover());
            }
        });

        Observer uiUpdater = new Observer() {
            @Override
            public void update() {
                nameLabel.setText(playlist.getText());
                bioLabel.setText(playlist.getBio());
                Image newScaledImg = playlist.getCover().getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
                artLabel.setIcon(new ImageIcon(newScaledImg));
            }
        };
        playlist.addObserver(uiUpdater);

        return itemPanel;
    }

    // --- USING YOUR 'extractTrackFromFile' (from Final-Vibra, with trackNum) ---
    private Track extractTrackFromFile(File file) {
        try {
            Mp3File mp3 = new Mp3File(file);
            String title = file.getName();
            String artist = "Unknown Artist";
            String album = "Unknown Album";
            int duration = (int) mp3.getLengthInSeconds();
            int trackNum = 0; // Default
            byte[] albumArt = null;

            if (mp3.hasId3v2Tag()) {
                ID3v2 tag = mp3.getId3v2Tag();
                if (tag.getTitle() != null) title = tag.getTitle();
                if (tag.getArtist() != null) artist = tag.getArtist();
                if (tag.getAlbum() != null) album = tag.getAlbum();
                
                String trackStr = tag.getTrack(); // e.g., "1/12" or "1"
                if (trackStr != null && !trackStr.isEmpty()) {
                    try {
                        String numberOnly = trackStr.split("/")[0];
                        trackNum = Integer.parseInt(numberOnly);
                    } catch (NumberFormatException e) {
                        // The tag was weird, just ignore it and use 0
                    }
                }
                if (tag.getAlbumImage() != null) albumArt = tag.getAlbumImage();
            }

            // Using the 7-argument constructor
            return new Track(title, artist, album, file.getAbsolutePath(), duration, trackNum, albumArt);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // (createStyledButton is unchanged, keeping it)
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

    // --- USING FRIEND'S 'createCreatePlaylistButton' (from HEAD) ---
    private RoundedButtonFactory createCreatePlaylistButton() {
        RoundedButtonFactory button = createStyledButton("Create Playlist");
        button.addActionListener(e -> {
            CreatePlaylistPanel createPanel = new CreatePlaylistPanel();

            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Create New Playlist", true);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setUndecorated(true);
            dialog.setBackground(new Color(0, 0, 0, 0));
            dialog.setContentPane(createPanel);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

            if (createPanel.isPlaylistCreated()) {
                String newName = createPanel.getPlaylistName();
                String newBio = createPanel.getPlaylistBio();
                ImageIcon newCover = createPanel.getPlaylistCover();
                
                // ‼️ NOTE: '1' and '0' are placeholders.
                // You will need to get the real currentUserID from your MainCardPanel.
                int currentUserId = 1; 
                int newPlaylistId = 0; 

                Playlist newPlaylist = new Playlist(
                        newPlaylistId,
                        newName,
                        newBio,
                        newCover,
                        currentUserId
                );

                JPanel newItem = createPlaylistItem(newPlaylist);

                playlistItemsContainer.remove(playlistItemsContainer.getComponentCount() - 1); // Remove glue
                playlistItemsContainer.add(newItem);
                playlistItemsContainer.add(Box.createVerticalStrut(15));
                playlistItemsContainer.add(Box.createVerticalGlue()); // Add glue back

                playlistItemsContainer.revalidate();
                playlistItemsContainer.repaint();
            }
        });
        return button;
    }

    // --- USING YOUR 'ScanWorker' (from Final-Vibra) ---
    private class ScanWorker extends SwingWorker<Map<String, List<Track>>, Void> {
        private final String scanPath;

        public ScanWorker(String path) {
            this.scanPath = path;
        }

        @Override
        protected Map<String, List<Track>> doInBackground() throws Exception {
            System.out.println("ScanWorker: Starting scan...");
            
            // 1. Use TrackLoader to get all tracks
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

    // --- (applyTheme is simple, unchanged) ---
    private void applyTheme() {
        ThemeManager tm = ThemeManager.getInstance();
        setBackground(tm.getSidebarColor());
        libraryPanel.setBackground(tm.getSidebarColor());
        albumLabel.setForeground(tm.getForegroundColor());
        yourLibraryLabel.setForeground(tm.getForegroundColor());
    }

    // --- USING FRIEND'S ADVANCED 'onThemeChanged' (from HEAD) ---
    @Override
    public void onThemeChanged(boolean isDarkMode) {
        ThemeManager tm = ThemeManager.getInstance();

        // 1. Get the new theme-specific icon factory
        themeIcons = isDarkMode ? new DarkModeIconFactory() : new LightModeIconFactory();
        Icon newEditIcon = themeIcons.createIcon("edit");

        // 2. Update all existing playlist item panels
        if (playlistItemsContainer != null) {
            for (Component item : playlistItemsContainer.getComponents()) {
                if (item instanceof RoundedPanelFactory) {
                    RoundedPanelFactory itemPanel = (RoundedPanelFactory) item;
                    itemPanel.setBackgroundColor(tm.getSidebarColor());

                    LayoutManager layout = itemPanel.getLayout();
                    if (layout instanceof BorderLayout) {
                        Component centerComponent = ((BorderLayout) layout).getLayoutComponent(BorderLayout.CENTER);
                        if (centerComponent instanceof JPanel) {
                            JPanel textPanel = (JPanel) centerComponent;
                            for (Component textComp : textPanel.getComponents()) {
                                String name = textComp.getName();
                                if (name != null) {
                                    switch (name) {
                                        case "PLAYLIST_NAME_LABEL":
                                            ((JLabel) textComp).setForeground(tm.getForegroundColor());
                                            break;
                                        case "PLAYLIST_BIO_LABEL":
                                            ((JLabel) textComp).setForeground(isDarkMode ? Color.LIGHT_GRAY : Color.DARK_GRAY);
                                            break;
                                    }
                                }
                            }
                        }

                        Component eastComponent = ((BorderLayout) layout).getLayoutComponent(BorderLayout.EAST);
                        if (eastComponent instanceof JButton && "EDIT_PLAYLIST_BUTTON".equals(eastComponent.getName())) {
                            ((JButton) eastComponent).setIcon(newEditIcon);
                        }
                    }
                }
            }
        }

        // 3. Apply the theme to the parent panel itself
        applyTheme(); 

        // 4. Repaint everything
        revalidate();
        repaint();
    }
}