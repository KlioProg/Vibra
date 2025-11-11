package com.mycompany.vibra.Content.Main_Page.Main_Contents.TrackLists;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout; // Import FlowLayout
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon; // Import ImageIcon
import javax.swing.JButton; // Import JButton
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar; // ✅ Import JScrollBar
import javax.swing.JScrollPane;
import java.sql.SQLException;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JOptionPane;

// ✅ Import your new UI class
import com.mycompany.vibra.Factories.Common_UI.CustomScrollBarUI;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.DunbarFactory;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.FontFactory;
import com.mycompany.vibra.dao.PlaylistDao;
import com.mycompany.vibra.dao.PlaylistSongDao;
import com.mycompany.vibra.model.Playlist;


import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.ButtonIconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.IconFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundedButtonFactory; // Kept for RoundedIconOnlyButton
import com.mycompany.vibra.Factories.Common_UI.RoundedIconButtonFactory; // Kept for RoundedIconOnlyButton
import com.mycompany.vibra.Factories.Common_UI.RoundedIconOnlyButton; // Import new class
import com.mycompany.vibra.Factories.ThemeFactory.ThemeManager;
import com.mycompany.vibra.musicUtilities.Track;
// --- IMPORT THE MUSIC PLAYER PANEL ---
import com.mycompany.vibra.Content.Main_Page.Main_Contents.Music_Player.MusicPlayerPanel;


public class TrackListPanel extends JPanel implements ThemeManager.ThemeChangerListener {

    // --- Fields ---
    private ArrayList<TrackList> trackListComponents;
    private ArrayList<Track> tracks;

    private MusicPlayerPanel musicPlayerPanel; // Reference to the main player

    private JPanel trackListContainer;
    private JScrollPane scrollPane; // Kept as field

    private JLabel trackLabel;
    private JLabel playLabel;
    FontFactory fontFactory = new DunbarFactory();

    private IconFactory buttonIconFactory = new ButtonIconFactory();

    private final PlaylistDao playlistDao;
    private final PlaylistSongDao playlistSongDao;
    private Track selectedTrack;
    private final int currentUserID;

    private JButton saveButton;
    private JButton deleteButton;
    private Playlist currentlyLoadedPlaylist;


    public TrackListPanel(int currentUserID) {
        this.currentUserID = currentUserID;
        setLayout(new BorderLayout());
        ThemeManager.getInstance().addThemeChangerListener(this);

        tracks = new ArrayList<>();
        trackListComponents = new ArrayList<>();

        this.playlistDao = new PlaylistDao();
        this.playlistSongDao = new PlaylistSongDao();
        this.selectedTrack = null;

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(32, 12, 8, 12));
        topPanel.setOpaque(false);
        topPanel.setAlignmentX(LEFT_ALIGNMENT);
        topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        trackLabel = new JLabel("Track List");
        trackLabel.setFont(fontFactory.createFont("dunbartall_bold", 36));
        topPanel.add(trackLabel);
        topPanel.add(Box.createHorizontalStrut(8));

        trackListContainer = new JPanel();
        trackListContainer.setLayout(new BoxLayout(trackListContainer, BoxLayout.Y_AXIS));
        trackListContainer.setOpaque(false);
        trackListContainer.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0)); // 10px horizontal gap
        buttonPanel.setAlignmentX(LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        // Using the new icon button methods
        JButton saveButton = createAddButton();
        JButton deleteButton = createDeleteButton();

        this.saveButton = createAddButton();
        this.deleteButton = createDeleteButton();

        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);

        trackListContainer.add(buttonPanel); // Add the panel with buttons
        trackListContainer.add(Box.createVerticalStrut(8));

        playLabel = new JLabel("What’s Playing:");
        playLabel.setFont(fontFactory.createFont("dunbartall_bold", 16));
        playLabel.setAlignmentX(LEFT_ALIGNMENT);
        trackListContainer.add(playLabel);
        trackListContainer.add(Box.createVerticalStrut(12)); // Kept this at 12px for space

        trackListContainer.add(Box.createVerticalGlue()); // Keeps tracks at the top

        // --- 4. Create and configure the JScrollPane ---
        scrollPane = new JScrollPane(trackListContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // --- 5. Add Panels to the Main Layout ---
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // --- 6. Apply Themes ---
        applyTheme(ThemeManager.getInstance().isDarkMode());
        applyScrollBarTheme(); // ✅ Apply custom scrollbar UI
    }
    public void setMusicPlayerPanel(MusicPlayerPanel musicPlayerPanel) {
        this.musicPlayerPanel = musicPlayerPanel;
    }

    public void loadTracksForPlaylist(Playlist playlist, List<Track> newTracks) {
        this.currentlyLoadedPlaylist = playlist; // Store the playlist
        this.selectedTrack = null; // Clear selection
        this.saveButton.setVisible(false); // Hide 'Add'
        this.deleteButton.setVisible(true); // Show 'Delete'
        
        populateTrackList(newTracks); // Call the UI method
    }

    public void loadTracksIntoPanel(List<Track> newTracks) {
        this.currentlyLoadedPlaylist = null; // No playlist context
        this.selectedTrack = null; // Clear selection
        this.saveButton.setVisible(true); // Show 'Add'
        this.deleteButton.setVisible(false); // Hide 'Delete'
        
        populateTrackList(newTracks); // Call the UI method
    }

    private void populateTrackList(List<Track> newTracks) {
        // Keep the first 4 components (buttonPanel, strut, playLabel, strut)
        while (trackListContainer.getComponentCount() > 4) {
            trackListContainer.remove(4);
        }

        trackListComponents.clear();
        tracks.clear();
        tracks.addAll(newTracks); // Add the new tracks to the class list

        // Remove VerticalGlue (it's the last component)
        trackListContainer.remove(trackListContainer.getComponentCount() - 1);

        int trackNum = 1;
        // Use the 'newTracks' parameter here
        for (Track track : newTracks) { 

            TrackList trackComponent = new TrackList(track, trackNum, this.musicPlayerPanel);

            // Add listener to select track
            trackComponent.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectedTrack = track;
                    System.out.println("Selected track: " + selectedTrack.getTitle());
                    // You could add a visual highlight here
                }
            });

            trackListComponents.add(trackComponent);
            trackListContainer.add(trackComponent);
            trackListContainer.add(Box.createVerticalStrut(8));
            trackNum++;
        }

        trackListContainer.add(Box.createVerticalGlue()); // Add the glue back

        trackListContainer.revalidate();
        trackListContainer.repaint();
    }

    private void addSelectedSongToPlaylist(Playlist playlist) {
        if (selectedTrack == null) {
            System.err.println("Add song called, but no track was selected.");
            return;
        }

        try {
            boolean success = playlistSongDao.addSongToPlaylist(playlist.getPlaylistId(), selectedTrack.getId());

            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "'" + selectedTrack.getTitle() + "' added to '" + playlist.getText() + "'.",
                    "Song Added", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                // This happens if the "INSERT OR IGNORE" finds a duplicate
                JOptionPane.showMessageDialog(this, 
                    "'" + selectedTrack.getTitle() + "' is already in '" + playlist.getText() + "'.",
                    "Already Exists", 
                    JOptionPane.WARNING_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error adding song to playlist.", 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- ✅ UPDATED ADD BUTTON ---
    private JButton createAddButton() {
        ImageIcon addIcon = buttonIconFactory.createIcon("add");

        JButton button = new RoundedIconOnlyButton(addIcon, 34, 34);

        // --- Color Logic ---
        Color baseColor = new Color(0x9D4EDD);
        Color hoverColor = new Color(0x7B2CBF);
        Color pressColor = new Color(0x5A189A); // ✅ Fixed press color

        button.setBackground(baseColor);
        button.setToolTipText("Add to playlist");

        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { button.setBackground(hoverColor); }
            @Override public void mouseExited(MouseEvent e) { button.setBackground(baseColor); }
            @Override public void mousePressed(MouseEvent e) { button.setBackground(pressColor); }
            @Override public void mouseReleased(MouseEvent e) { button.setBackground(hoverColor); }
        });

        button.addActionListener(e -> {
            // 1. Check if a track is actually selected
            if (selectedTrack == null) {
                JOptionPane.showMessageDialog(button, "Please click on a track to select it first.", "No Track Selected", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // 2. Create the popup menu
            JPopupMenu playlistMenu = new JPopupMenu();

            try {
                // 3. Get all of the user's playlists using the stored ID
                List<Playlist> userPlaylists = playlistDao.getUserPlaylists(this.currentUserID);

                if (userPlaylists.isEmpty()) {
                    JMenuItem emptyItem = new JMenuItem("No playlists found. Create one first!");
                    emptyItem.setEnabled(false);
                    playlistMenu.add(emptyItem);
                } else {
                    // 4. Create a menu item for each playlist
                    for (Playlist playlist : userPlaylists) {
                        JMenuItem playlistItem = new JMenuItem(playlist.getText());
                        playlistItem.addActionListener(itemEvent -> {
                            // 5. When a playlist is clicked, add the song
                            addSelectedSongToPlaylist(playlist);
                        });
                        playlistMenu.add(playlistItem);
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JMenuItem errorItem = new JMenuItem("Error loading playlists");
                errorItem.setEnabled(false);
                playlistMenu.add(errorItem);
            }

            // 6. Show the popup menu right below the button
            playlistMenu.show(button, 0, button.getHeight());
        });
        return button;
    }

    // --- ✅ UPDATED DELETE BUTTON ---
    private JButton createDeleteButton() {
        ImageIcon deleteIcon = buttonIconFactory.createIcon("delete");

        JButton button = new RoundedIconOnlyButton(deleteIcon, 34, 34);

        // --- Color Logic (Same as Add button) ---
        Color baseColor = new Color(0x9D4EDD);
        Color hoverColor = new Color(0x7B2CBF);
        Color pressColor = new Color(0x5A189A); // ✅ Fixed press color

        button.setBackground(baseColor);
        button.setToolTipText("Delete from playlist");

        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { button.setBackground(hoverColor); }
            @Override public void mouseExited(MouseEvent e) { button.setBackground(baseColor); }
            @Override public void mousePressed(MouseEvent e) { button.setBackground(pressColor); }
            @Override public void mouseReleased(MouseEvent e) { button.setBackground(hoverColor); }
        });

        button.addActionListener(e -> {
            // 1. Check if a track is selected
            if (selectedTrack == null) {
                JOptionPane.showMessageDialog(button, "Please click on a track to select it first.", "No Track Selected", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // 2. Check that we are indeed viewing a playlist
            if (currentlyLoadedPlaylist == null) {
                JOptionPane.showMessageDialog(button, "This action is only available within a playlist.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 3. Confirm the deletion
            int choice = JOptionPane.showConfirmDialog(button,
                "Remove '" + selectedTrack.getTitle() + "' from '" + currentlyLoadedPlaylist.getText() + "'?",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                // 4. Call the DAO
                try {
                    boolean success = playlistSongDao.removeSongFromPlaylist(
                        currentlyLoadedPlaylist.getPlaylistId(),
                        selectedTrack.getId()
                    );

                    if (success) {
                        // 5. Remove the song from the UI *locally*
                        tracks.remove(selectedTrack); // Remove from the master list
                        populateTrackList(tracks); // Re-populate the panel with the modified list
                        selectedTrack = null; // De-select the track
                    } else {
                        JOptionPane.showMessageDialog(button, "Could not remove the song.", "Failed", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(button, "Error removing song from database.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        return button;
    }

    // --- ✅ NEW HELPER METHOD ---
    /**
     * Applies the custom UI to the scrollbar based on the current theme.
     */
    private void applyScrollBarTheme() {
        if (scrollPane == null) return;

        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUI(new CustomScrollBarUI());

        // This is important to ensure the track background repaints correctly
        verticalScrollBar.revalidate();
        verticalScrollBar.repaint();

        // This sets the color of the small square in the corner
        scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, new JPanel());
        scrollPane.getCorner(JScrollPane.UPPER_RIGHT_CORNER).setBackground(ThemeManager.getInstance().getSidebarColor());
    }

    // --- ✅ UPDATED THEME METHOD ---
    private void applyTheme(boolean isDarkMode) {
        ThemeManager tm = ThemeManager.getInstance();

        // Use getSidebarColor so the scrollbar track matches the panel
        Color bgColor = tm.getSidebarColor();
        setBackground(bgColor);

        // Also set the container and viewport backgrounds
        trackListContainer.setBackground(bgColor);
        scrollPane.setBackground(bgColor);
        scrollPane.getViewport().setBackground(bgColor);

        Color fg = tm.getForegroundColor();
        trackLabel.setForeground(fg);
        playLabel.setForeground(fg);
    }

    @Override
    public void onThemeChanged(boolean isDarkMode) {
        applyTheme(isDarkMode);
        applyScrollBarTheme(); // ✅ Re-apply scrollbar theme on toggle
        revalidate();
        repaint();
    }
}