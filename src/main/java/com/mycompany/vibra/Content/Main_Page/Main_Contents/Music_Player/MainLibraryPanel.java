package com.mycompany.vibra.Content.Main_Page.Main_Contents.Music_Player;

import java.awt.*;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.DunbarFactory;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.FontFactory;
// Import your IconFactories
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.ButtonIconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.DarkModeIconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.IconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.LightModeIconFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundedIconButtonFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundedPanelFactory;
import com.mycompany.vibra.model.Observer;
import com.mycompany.vibra.model.Playlist;
import com.mycompany.vibra.musicUtilities.Track;
import com.mycompany.vibra.Factories.Common_UI.RoundedButtonFactory;
import com.mycompany.vibra.Factories.ThemeFactory.ThemeManager;

import com.mycompany.vibra.Content.Main_Page.Main_Contents.TrackLists.TrackListPanel;




import java.util.ArrayList;
import java.util.List;

public class MainLibraryPanel extends JPanel implements ThemeManager.ThemeChangerListener {

    private final MusicPlayerPanel musicPlayerPanel;
    private final TrackListPanel trackListPanel;
    private JPanel playlistItemsContainer;
    private IconFactory themeIcons; // This will store the correct (dark/light) icon factory

    // Keep refs so we can update them on theme change
    private JPanel libraryPanel;
    private JLabel albumLabel;
    private JLabel yourLibraryLabel;
    FontFactory fontFactory = new DunbarFactory();



    public MainLibraryPanel(MusicPlayerPanel musicPlayerPanel, TrackListPanel trackListPanel) {
        this.musicPlayerPanel = musicPlayerPanel;
        this.trackListPanel = trackListPanel; // Store the reference

        // Initialize theme icons right away
        this.themeIcons = ThemeManager.getInstance().isDarkMode() ? new DarkModeIconFactory() : new LightModeIconFactory();

        setLayout(new BorderLayout());
        initUI();

        ThemeManager.getInstance().addThemeChangerListener(this);
        applyTheme(); // Apply theme after initUI
    }

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
        yourLibraryLabel.setFont(fontFactory.createFont("dunbartall_bold", 20));
        yourLibraryLabel.setAlignmentX(LEFT_ALIGNMENT);
        playlistItemsContainer.add(yourLibraryLabel);

        playlistItemsContainer.add(Box.createVerticalStrut(12));

        playlistItemsContainer.add(createCreatePlaylistButton());

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

    private JPanel createPlaylistItem(final Playlist playlist) {
        ThemeManager tm = ThemeManager.getInstance();
        Color baseColor = tm.getSidebarColor();
        Color hoverColor = new Color(0x535353); // The gray hover

        // 1. The Main Container
        RoundedPanelFactory itemPanel = new RoundedPanelFactory(
                15, baseColor, null, 0, 0, 90
        );
        itemPanel.setLayout(new BorderLayout(12, 0));
        itemPanel.setAlignmentX(LEFT_ALIGNMENT);
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        itemPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        itemPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Image scaledImg = playlist.getCover().getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);

        // Make artLabel final so the Observer can access it
        final JLabel artLabel = new JLabel(new ImageIcon(scaledImg));
        artLabel.setOpaque(false); // Make transparent

        RoundedPanelFactory coverClipper = new RoundedPanelFactory(
                10, Color.BLACK, null, 0, 70, 70
        );
        coverClipper.setLayout(new BorderLayout());
        coverClipper.add(artLabel, BorderLayout.CENTER);
        itemPanel.add(coverClipper, BorderLayout.WEST);

        // 3. The Text Panel (Name + Bio)
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        // Read from model. Make labels final.
        final JLabel nameLabel = new JLabel(playlist.getText());
        nameLabel.setFont(fontFactory.createFont("dunbartall_bold", 14));
        nameLabel.setForeground(tm.getForegroundColor()); // ✅ FIX 1: Use theme color
        nameLabel.setName("PLAYLIST_NAME_LABEL"); // ✅ FIX 4: Add name

        final JLabel bioLabel = new JLabel(playlist.getBio());
        bioLabel.setFont(fontFactory.createFont("dunbartall_book", 12));
        // ✅ FIX 2: Use theme-aware gray
        bioLabel.setForeground(tm.isDarkMode() ? Color.LIGHT_GRAY : Color.DARK_GRAY);
        bioLabel.setName("PLAYLIST_BIO_LABEL"); // ✅ FIX 4: Add name

        textPanel.add(nameLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(bioLabel);
        textPanel.add(Box.createVerticalGlue());
        itemPanel.add(textPanel, BorderLayout.CENTER);

        // 4. The "Edit" Button (Your code was already correct here)
        ImageIcon editIcon = themeIcons.createIcon("edit");
        JButton editButton = RoundedIconButtonFactory.createIconButton(
                editIcon, null, 34, "Edit Playlist"
        );
        editButton.setName("EDIT_PLAYLIST_BUTTON");
        itemPanel.add(editButton, BorderLayout.EAST);

        // 5. Hover Effect
        itemPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                itemPanel.setBackgroundColor(hoverColor);
                // ✅ HOVER FIX: Change font colors
                nameLabel.setForeground(Color.WHITE);
                bioLabel.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                // ✅ HOVER FIX: Restore original theme colors
                ThemeManager tm = ThemeManager.getInstance(); // Get current theme
                itemPanel.setBackgroundColor(tm.getSidebarColor());
                nameLabel.setForeground(tm.getForegroundColor());
                bioLabel.setForeground(tm.isDarkMode() ? Color.LIGHT_GRAY : Color.DARK_GRAY);
            }
        });

        // 6. Click Action for "Play" (Unchanged)
        itemPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getSource() == editButton || SwingUtilities.isDescendingFrom(e.getComponent(), editButton)) {
                    return;
                }
                System.out.println("Clicked to play playlist: " + playlist.getText());
                // trackListPanel.loadTracksIntoPanel(playlist.getTracks());
            }
        });

        // 7. "Edit" logic (Unchanged)
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

        // 8. ADD THE OBSERVER (Unchanged)
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

    // (extractTrackFromFile is unchanged)
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

    // (createStyledButton is unchanged)
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

    // (createUploadButton is unchanged)
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

    // (createOpenFolderButton is unchanged)
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
                    if (!tracks.isEmpty() && trackListPanel != null) {
                        trackListPanel.loadTracksIntoPanel(tracks);
                    }
                }
            }
        });

        return button;
    }

    // ✅ **FIXED** This is the re-implemented "Create Playlist" button method
    private RoundedButtonFactory createCreatePlaylistButton() {
        RoundedButtonFactory button = createStyledButton("Create Playlist");
        button.addActionListener(e -> {
            // Use the "Create" constructor (no initial data)
            CreatePlaylistPanel createPanel = new CreatePlaylistPanel();

            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Create New Playlist", true); // true = modal
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setUndecorated(true);
            dialog.setBackground(new Color(0, 0, 0, 0));
            dialog.setContentPane(createPanel);
            dialog.pack();
            dialog.setLocationRelativeTo(this);

            dialog.setVisible(true);

            // After dialog closes, check if user saved
            if (createPanel.isPlaylistCreated()) {
                String newName = createPanel.getPlaylistName();
                String newBio = createPanel.getPlaylistBio();
                ImageIcon newCover = createPanel.getPlaylistCover();

                // 1. Create the new Playlist MODEL
                // -----------------------------------------------------------------
                // ‼️ NOTE: You must get the real user_id from your application's
                // session or authentication manager. "1" is a placeholder.
                int currentUserId = 1;

                // "0" is a placeholder for a new playlist not yet in the database.
                // Your database logic would later insert this and get a real ID.
                int newPlaylistId = 0;

                Playlist newPlaylist = new Playlist(
                        newPlaylistId,
                        newName,
                        newBio,
                        newCover,
                        currentUserId
                );
                // -----------------------------------------------------------------

                // 2. Create the VIEW for the new model
                JPanel newItem = createPlaylistItem(newPlaylist);

                // 3. Add the new view to the UI
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

    // (applyTheme is unchanged)
    private void applyTheme() {
        ThemeManager tm = ThemeManager.getInstance();
        setBackground(tm.getSidebarColor());
        libraryPanel.setBackground(tm.getSidebarColor());

        albumLabel.setForeground(tm.getForegroundColor());
        yourLibraryLabel.setForeground(tm.getForegroundColor());
    }

    // (onThemeChanged is correct and unchanged)
    // In MainLibraryPanel.java
    @Override
    public void onThemeChanged(boolean isDarkMode) {
        ThemeManager tm = ThemeManager.getInstance(); // Get instance once

        // --- 1. Get the new theme-specific icon factory ---
        themeIcons = isDarkMode ? new DarkModeIconFactory() : new LightModeIconFactory();
        Icon newEditIcon = themeIcons.createIcon("edit"); // Get the new icon

        // --- 2. Update all existing playlist item panels ---
        if (playlistItemsContainer != null) {
            for (Component item : playlistItemsContainer.getComponents()) {
                // Check if it's a playlist item panel
                if (item instanceof RoundedPanelFactory) {
                    RoundedPanelFactory itemPanel = (RoundedPanelFactory) item;

                    // ✅ FIX: Update the panel's base background color
                    itemPanel.setBackgroundColor(tm.getSidebarColor());

                    // Get the layout to find child components
                    LayoutManager layout = itemPanel.getLayout();
                    if (layout instanceof BorderLayout) {

                        // ✅ FIX: Update text labels
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

                        // --- This part was already correct ---
                        // Get the button we stored in the "EAST" position
                        Component eastComponent = ((BorderLayout) layout).getLayoutComponent(BorderLayout.EAST);
                        if (eastComponent instanceof JButton && "EDIT_PLAYLIST_BUTTON".equals(eastComponent.getName())) {
                            ((JButton) eastComponent).setIcon(newEditIcon);
                        }
                    }
                }
            }
        }

        // --- 3. Apply the theme to the parent panel itself ---
        applyTheme(); // This calls your other method to update the labels/background

        // --- 4. Repaint everything ---
        revalidate();
        repaint();
    }
}