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
import com.mycompany.vibra.musicUtilities.Track;
import com.mycompany.vibra.service.TrackService;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.ButtonIconFactory; // Import
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.DarkModeIconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.IconFactory; // Import
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.LightModeIconFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundedIconButtonFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundedPanelFactory;
import com.mycompany.vibra.model.Track;
import com.mycompany.vibra.Factories.Common_UI.RoundedButtonFactory;
import com.mycompany.vibra.Factories.ThemeFactory.ThemeManager;

// This is the CRUCIAL import
import com.mycompany.vibra.Content.Main_Page.Main_Contents.TrackLists.TrackListPanel;

import java.util.ArrayList;
import java.util.List;

public class MainLibraryPanel extends JPanel implements ThemeManager.ThemeChangerListener {

    private final MusicPlayerPanel musicPlayerPanel;
    private final TrackListPanel trackListPanel;
    private final TrackService trackService;
    private JPanel playlistItemsContainer;
    private IconFactory themeIcons;

    // Keep refs so we can update them on theme change
    private JPanel libraryPanel;
    private JLabel albumLabel;
    private JLabel yourLibraryLabel;
    FontFactory fontFactory = new DunbarFactory();
    private IconFactory icons = new ButtonIconFactory(); // ✅ ADD ICON FACTORY

    // REMOVED editIcon field, it's loaded on demand

    public MainLibraryPanel(MusicPlayerPanel musicPlayerPanel, TrackListPanel trackListPanel) {
        this.musicPlayerPanel = musicPlayerPanel;
        this.trackListPanel = trackListPanel; // Store the reference
        this.trackService = new TrackService();

        // Initialize theme icons right away
        this.themeIcons = ThemeManager.getInstance().isDarkMode() ? new DarkModeIconFactory() : new LightModeIconFactory();

        setLayout(new BorderLayout());
        initUI();

        ThemeManager.getInstance().addThemeChangerListener(this);
        applyTheme();
    }

    private void initUI() {
        // ... (Your initUI code is perfect, no changes needed) ...
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
//
//        playlistItemsContainer.add(createCreatePlaylistButton());

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

//
//    private JPanel createPlaylistItem(ImageIcon cover, String name, String bio) {
//        // This "data holder" class is needed so the edit button's
//        // listener can access and change the item's data.
//        final class PlaylistData {
//            String name;
//            String bio;
//            ImageIcon cover;
//            PlaylistData(String n, String b, ImageIcon c) {
//                this.name = n; this.bio = b; this.cover = c;
//            }
//        }
//        // Now we create the 'data' variable
//        final PlaylistData data = new PlaylistData(name, bio, cover);

//        Color baseColor = ThemeManager.getInstance().getSidebarColor();
//        Color hoverColor = new Color(0x53, 53, 53); // The gray hover
//
//        // 1. The Main Container
//        RoundedPanelFactory itemPanel = new RoundedPanelFactory(
//                15, baseColor, null, 0, 0, 90
//        );
//        itemPanel.setLayout(new BorderLayout(12, 0));
//        itemPanel.setAlignmentX(LEFT_ALIGNMENT);
//        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
//        itemPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//        itemPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//
//        // 2. The Rounded Cover Art
//        Image scaledImg = data.cover.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
//        JLabel artLabel = new JLabel(new ImageIcon(scaledImg));
//        artLabel.setOpaque(false); // Make transparent
//
//        RoundedPanelFactory coverClipper = new RoundedPanelFactory(
//                10, Color.BLACK, null, 0, 70, 70
//        );
//        coverClipper.setLayout(new BorderLayout());
//        coverClipper.add(artLabel, BorderLayout.CENTER);
//        itemPanel.add(coverClipper, BorderLayout.WEST);
//
//        // 3. The Text Panel (Name + Bio)
//        JPanel textPanel = new JPanel();
//        textPanel.setOpaque(false);
//        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
//
//        JLabel nameLabel = new JLabel(data.name);
//        nameLabel.setFont(fontFactory.createFont("dunbartall_bold", 14));
//        nameLabel.setForeground(Color.WHITE);
//
//        JLabel bioLabel = new JLabel(data.bio);
//        bioLabel.setFont(fontFactory.createFont("dunbartall_book", 12));
//        bioLabel.setForeground(Color.LIGHT_GRAY);
//
//        textPanel.add(nameLabel);
//        textPanel.add(Box.createVerticalStrut(4));
//        textPanel.add(bioLabel);
//        textPanel.add(Box.createVerticalGlue());
//        itemPanel.add(textPanel, BorderLayout.CENTER);
//
//        // 4. The "Edit" Button (Using your factory)
//        ImageIcon editIcon = icons.createIcon("edit"); // Using your icon case "edit"
//        JButton editButton = RoundedIconButtonFactory.createIconButton(
//                editIcon,
//                null,      // No hover icon
//                34,        // 34x34 size from your case
//                "Edit Playlist"
//        );
//        editButton.setName("EDIT_PLAYLIST_BUTTON"); // Tag the button
//
//        itemPanel.add(editButton, BorderLayout.EAST); // Add to the right
//
//        // 5. Hover Effect for the *main panel*
//        itemPanel.addMouseListener(new java.awt.event.MouseAdapter() {
//            @Override
//            public void mouseEntered(java.awt.event.MouseEvent e) {
//                itemPanel.setBackgroundColor(hoverColor);
//            }
//            @Override
//            public void mouseExited(java.awt.event.MouseEvent e) {
//                itemPanel.setBackgroundColor(baseColor);
//            }
//        });
//
//        // 6. Click Action for the *main panel* (to play the playlist)
//        itemPanel.addMouseListener(new java.awt.event.MouseAdapter() {
//            @Override
//            public void mouseClicked(java.awt.event.MouseEvent e) {
//                if (e.getSource() == editButton || SwingUtilities.isDescendingFrom(e.getComponent(), editButton)) {
//                    return;
//                }
//                System.out.println("Clicked to play playlist: " + data.name);
//                // TODO: Add logic here to load this playlist's tracks
//            }
//        });
//
//        // 7. This is the "Edit" logic
//        editButton.addActionListener(e -> {
//
//            // This line is no longer red, because 'data' now exists!
//            CreatePlaylistPanel createPanel = new CreatePlaylistPanel(data.name, data.bio, data.cover);
//
//            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(itemPanel), "Edit Playlist", true);
//            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//            dialog.setUndecorated(true);
//            dialog.setBackground(new Color(0, 0, 0, 0));
//            dialog.setContentPane(createPanel);
//            dialog.pack();
//            dialog.setLocationRelativeTo(itemPanel);
//            dialog.setVisible(true);
//
//            // After dialog closes, check if user saved
//            if (createPanel.isPlaylistCreated()) {
//                // Get new data and update the data object
//                data.name = createPanel.getPlaylistName();
//                data.bio = createPanel.getPlaylistBio();
//                data.cover = createPanel.getPlaylistCover();
//
//                // Update the UI labels with the new data
//                nameLabel.setText(data.name);
//                bioLabel.setText(data.bio);
//                Image newScaledImg = data.cover.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
//               artLabel.setIcon(new ImageIcon(newScaledImg));
//            }
//        });
//
//        return itemPanel;
//    }

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

    // ... (createStyledButton method is fine) ...
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

    // ... (createUploadButton method is fine) ...
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

    // ... (createOpenFolderButton method is fine) ...
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

//    private RoundedButtonFactory createCreatePlaylistButton() {
//        RoundedButtonFactory button = createStyledButton("Create Playlist");
//        button.addActionListener(e -> {
//            CreatePlaylistPanel createPanel = new CreatePlaylistPanel();
//
//            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Create New Playlist", true); // true = modal
//            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//            dialog.setUndecorated(true);
//            dialog.setBackground(new Color(0, 0, 0, 0));
//            dialog.setContentPane(createPanel);
//            dialog.pack();
//            dialog.setLocationRelativeTo(this);
//
//            dialog.setVisible(true);
//
//            if (createPanel.isPlaylistCreated()) {
//                String newName = createPanel.getPlaylistName();
//                String newBio = createPanel.getPlaylistBio(); // GET THE NEW BIO
//                ImageIcon newCover = createPanel.getPlaylistCover();
//
//                // Pass all three pieces of data to the creator method
//                JPanel newItem = createPlaylistItem(newCover, newName, newBio);
//
//                playlistItemsContainer.remove(playlistItemsContainer.getComponentCount() - 1); // Remove glue
//                playlistItemsContainer.add(newItem);
//                playlistItemsContainer.add(Box.createVerticalStrut(15));
//                playlistItemsContainer.add(Box.createVerticalGlue()); // Add glue back
//
//                playlistItemsContainer.revalidate();
//                playlistItemsContainer.repaint();
//            }
//        });
//        return button;
//    }

    // ⛔️ DELETED your unused applyTheme(boolean) stub

    // Your theme code, unchanged
    private void applyTheme() {
        ThemeManager tm = ThemeManager.getInstance();
        setBackground(tm.getSidebarColor());
        libraryPanel.setBackground(tm.getSidebarColor());

        albumLabel.setForeground(tm.getForegroundColor());
        yourLibraryLabel.setForeground(tm.getForegroundColor());
    }

    // ✅ REPLACED with the version that updates the icons
    @Override
    public void onThemeChanged(boolean isDarkMode) {
        // --- 1. Get the new theme-specific icon factory ---
        themeIcons = isDarkMode ? new DarkModeIconFactory() : new LightModeIconFactory();
        Icon newEditIcon = themeIcons.createIcon("edit"); // Get the new icon

        // --- 2. Update all existing playlist item icons ---
        if (playlistItemsContainer != null) {
            for (Component item : playlistItemsContainer.getComponents()) {
                // Check if it's a playlist item panel
                if (item instanceof RoundedPanelFactory) {
                    RoundedPanelFactory itemPanel = (RoundedPanelFactory) item;

                    // Get the button we stored in the "EAST" position
                    LayoutManager layout = itemPanel.getLayout();
                    if (layout instanceof BorderLayout) {
                        Component eastComponent = ((BorderLayout) layout).getLayoutComponent(BorderLayout.EAST);

                        // Check if it's a JButton and has the right name
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