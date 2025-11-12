package com.mycompany.vibra.Content.Main_Page.Main_Contents.Music_Player;

import java.awt.*;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

// --- MERGED IMPORTS ---
import java.util.Map;
import java.util.List;

import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.*;
import com.mycompany.vibra.musicUtilities.Track;
import com.mycompany.vibra.musicUtilities.TrackLoader;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.DunbarFactory;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.FontFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundedIconButtonFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundedPanelFactory;
import com.mycompany.vibra.model.Observer;
import com.mycompany.vibra.model.Playlist;
import com.mycompany.vibra.Factories.Common_UI.RoundedButtonFactory;
import com.mycompany.vibra.Factories.ThemeFactory.ThemeManager;
import com.mycompany.vibra.dao.PlaylistDao;
import com.mycompany.vibra.dao.PlaylistSongDao;
import com.mycompany.vibra.Content.Main_Page.Main_Contents.Album_Panel.AlbumPanel;
import com.mycompany.vibra.Content.Main_Page.Main_Contents.TrackLists.TrackListPanel;
import com.mycompany.vibra.service.TrackService; // From Final-Vibraimport com.mycompany.vibra.dao.PlaylistDao;
import com.mycompany.vibra.Factories.Common_UI.ImageUtils;
import java.sql.SQLException;
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
    private IconFactory buttonIcons; // From HEAD
    private final PlaylistDao playlistDao;
    private final PlaylistSongDao playlistSongDao;
    private final int currentUserID;
    private JPanel libraryPanel;
    private JLabel albumLabel;
    private JLabel yourLibraryLabel;
    FontFactory fontFactory = new DunbarFactory();

    public MainLibraryPanel(MusicPlayerPanel musicPlayerPanel, TrackListPanel trackListPanel, AlbumPanel albumPanel, int currentUserID) {
        this.musicPlayerPanel = musicPlayerPanel;
        this.trackListPanel = trackListPanel;
        this.albumPanel = albumPanel;
        this.trackService = new TrackService();
        this.playlistDao = new PlaylistDao();
        this.playlistSongDao = new PlaylistSongDao();
        this.currentUserID = currentUserID;

        this.themeIcons = ThemeManager.getInstance().isDarkMode() ? new DarkModeIconFactory() : new LightModeIconFactory();
        this.buttonIcons = new CommonIconFactory();

        setLayout(new BorderLayout());
        initUI();

        loadUserPlaylists();

        ThemeManager.getInstance().addThemeChangerListener(this);
        applyTheme();

        String hardcodedScanPath = "/Users/eeeuweee/Music/vibramusic";// ‼️ CHANGE THIS PATH
        new ScanWorker(hardcodedScanPath).execute();
    }

    private void initUI() {

        libraryPanel = new JPanel();
        libraryPanel.setLayout(new BorderLayout(0, 4)); // BorderLayout with 12px vertical gap
        libraryPanel.setOpaque(false);
        libraryPanel.setBorder(BorderFactory.createEmptyBorder(32, 12, 0, 12));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.setOpaque(false);
        topPanel.setAlignmentX(LEFT_ALIGNMENT);
        topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        albumLabel = new JLabel("Album");
        albumLabel.setFont(fontFactory.createFont("dunbartall_bold", 36));

        libraryPanel.add(topPanel, BorderLayout.NORTH);

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

    private void addPlaylistToView(Playlist playlist) {
        JPanel newItem = createPlaylistItem(playlist);

        // Remove the glue from the bottom
        playlistItemsContainer.remove(playlistItemsContainer.getComponentCount() - 1);

        // Add the new item and spacing
        playlistItemsContainer.add(newItem);
        playlistItemsContainer.add(Box.createVerticalStrut(15));

        // Add the glue back to the bottom
        playlistItemsContainer.add(Box.createVerticalGlue());

        playlistItemsContainer.revalidate();
        playlistItemsContainer.repaint();
    }

    /**
     * Loads all playlists for the current user from the database and
     * adds them to the UI.
     */
    private void loadUserPlaylists() {
        try {
            // Use the DAO and the ID we saved
            List<Playlist> userPlaylists = playlistDao.getUserPlaylists(this.currentUserID);

            // Add each playlist to the view
            for (Playlist playlist : userPlaylists) {
                addPlaylistToView(playlist);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Show a friendly error to the user
            JOptionPane.showMessageDialog(this,
                "Error loading playlists from database.",
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

        private void removePlaylistFromView(JPanel playlistPanel) {
            Component toRemove = null;
            Component strutToRemove = null;

            // 1. Find the panel and the strut right after it
            Component[] components = playlistItemsContainer.getComponents();
            for (int i = 0; i < components.length; i++) {
                if (components[i] == playlistPanel) {
                    toRemove = components[i];
                    // Check if the next component is a vertical strut (Box.Filler)
                    if (i + 1 < components.length && components[i + 1] instanceof Box.Filler) {
                        strutToRemove = components[i + 1];
                    }
                    break;
                }
            }

            // 2. Remove them from the container
            if (toRemove != null) {
                playlistItemsContainer.remove(toRemove);
            }
            if (strutToRemove != null) {
                playlistItemsContainer.remove(strutToRemove);
            }

            // 3. Refresh the UI
            playlistItemsContainer.revalidate();
            playlistItemsContainer.repaint();
        }

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

        // --- COVER IMAGE ---
        Image scaledImg = playlist.getCover().getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
        final JLabel artLabel = new JLabel(new ImageIcon(scaledImg));
        artLabel.setOpaque(false);

        RoundedPanelFactory coverClipper = new RoundedPanelFactory(
                10, Color.BLACK, null, 0, 70, 70
        );
        coverClipper.setLayout(new BorderLayout());
        coverClipper.add(artLabel, BorderLayout.CENTER);
        itemPanel.add(coverClipper, BorderLayout.WEST);

        // --- TEXT INFO ---
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

        ImageIcon normalEditIcon = themeIcons.createIcon("edit");       // From Light or Dark factory
        ImageIcon hoverEditIcon = buttonIcons.createIcon("edit_hover"); // Always white hover

        JButton editButton = RoundedIconButtonFactory.createIconButton(
                normalEditIcon, null, 34, "Edit Playlist"
        );
        editButton.setName("EDIT_PLAYLIST_BUTTON");
        editButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        itemPanel.add(editButton, BorderLayout.EAST);

        // --- HOVER FOR EDIT BUTTON ---
        editButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                editButton.setIcon(hoverEditIcon); // Always white
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Fix: only reset if not still hovering the itemPanel
                SwingUtilities.invokeLater(() -> {
                    Point p = MouseInfo.getPointerInfo().getLocation();
                    SwingUtilities.convertPointFromScreen(p, itemPanel);
                    if (!itemPanel.contains(p)) {
                        editButton.setIcon(themeIcons.createIcon("edit")); // Reset to theme-based icon
                    }
                });
            }
        });

// --- ITEM HOVER EFFECT ---
        itemPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                itemPanel.setBackgroundColor(hoverColor);
                nameLabel.setForeground(Color.WHITE);
                bioLabel.setForeground(Color.WHITE);
                editButton.setIcon(hoverEditIcon); // Make icon white when hovering item
            }

            @Override
            public void mouseExited(MouseEvent e) {
                SwingUtilities.invokeLater(() -> {
                    Point p = MouseInfo.getPointerInfo().getLocation();
                    SwingUtilities.convertPointFromScreen(p, itemPanel);

                    // Only reset if mouse completely leaves the playlist item (not hovering button)
                    if (!itemPanel.contains(p)) {
                        ThemeManager tm = ThemeManager.getInstance();
                        itemPanel.setBackgroundColor(tm.getSidebarColor());
                        nameLabel.setForeground(tm.getForegroundColor());
                        bioLabel.setForeground(tm.isDarkMode() ? Color.LIGHT_GRAY : Color.DARK_GRAY);
                        editButton.setIcon(themeIcons.createIcon("edit")); // Reset to proper themed icon
                    }
                });
            }
        });

        // --- CLICK: LOAD PLAYLIST ---
        itemPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Ignore clicks on edit button
                if (e.getSource() == editButton || SwingUtilities.isDescendingFrom(e.getComponent(), editButton)) {
                    return;
                }

                System.out.println("Clicked to load playlist: " + playlist.getText());
                try {
                    List<Track> tracks = playlistSongDao.getSongsForPlaylist(playlist.getPlaylistId());
                    trackListPanel.loadTracksForPlaylist(playlist, tracks);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(itemPanel,
                            "Error loading songs for playlist.",
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // --- EDIT FUNCTIONALITY ---
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
                String newName = createPanel.getPlaylistName();
                String newBio = createPanel.getPlaylistBio();
                ImageIcon newCover = createPanel.getPlaylistCover();

                try {
                    byte[] newCoverBytes = ImageUtils.convertImageIconToBytes(newCover);
                    boolean success = playlistDao.updatePlaylist(
                            playlist.getPlaylistId(),
                            newName,
                            newBio,
                            newCoverBytes
                    );

                    if (success) {
                        playlist.setText(newName);
                        playlist.setBio(newBio);
                        playlist.setCover(newCover);
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Could not update playlist.", "Update Failed", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(dialog, "Error updating playlist in database.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } else if (createPanel.isPlaylistDeleted()) {
                // USER CLICKED "DELETE" (and confirmed inside the panel)
                try {
                    boolean success = playlistDao.deletePlaylist(playlist.getPlaylistId());

                    if (success) {
                        // Use the helper we already built!
                        removePlaylistFromView(itemPanel);
                    } else {
                        JOptionPane.showMessageDialog(itemPanel, "Could not delete playlist.", "Failed", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(itemPanel, "Error deleting playlist from database.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // --- OBSERVER (auto-refresh UI when playlist updates) ---
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

                // --- MODIFIED: Database Call ---
                try {
                    // 1. Convert ImageIcon to byte[] for the database
                    byte[] coverBytes = ImageUtils.convertImageIconToBytes(newCover);

                    // 2. Call the DAO to create the playlist.
                    //    It returns the fully-formed Playlist object with the new ID.
                    Playlist newPlaylist = playlistDao.createPlaylist(
                        this.currentUserID, // Use the class field
                        newName,
                        newBio,
                        coverBytes
                    );

                    // 3. If creation was successful, add it to the UI
                    if (newPlaylist != null) {
                        // Use our new helper method!
                        addPlaylistToView(newPlaylist);
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Could not create playlist.", "Creation Failed", JOptionPane.WARNING_MESSAGE);
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(dialog, "Error saving playlist to database.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
                // --- END MODIFIED ---
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