package com.mycompany.vibra.Content.Main_Page.Main_Contents.TrackLists;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.DunbarFactory;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.FontFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundedButtonFactory;
import com.mycompany.vibra.Factories.ThemeFactory.ThemeManager;
import com.mycompany.vibra.musicUtilities.Track;
// --- IMPORT THE MUSIC PLAYER PANEL ---
import com.mycompany.vibra.Content.Main_Page.Main_Contents.Music_Player.MusicPlayerPanel;


public class TrackListPanel extends JPanel implements ThemeManager.ThemeChangerListener {

    // --- Fields ---
    private ArrayList<TrackList> trackListComponents;
    private ArrayList<Track> tracks;
    
    // --- NO LONGER NEEDS AUDIO PLAYER, NEEDS THE MAIN PLAYER ---
    private MusicPlayerPanel musicPlayerPanel; // Reference to the main player

    private JPanel trackListContainer;
    private JScrollPane scrollPane;

    private JLabel trackLabel;
    private JLabel playLabel;
    FontFactory fontFactory = new DunbarFactory();

    // --- CONSTRUCTOR IS NOW EMPTY ---
    public TrackListPanel() {
        setLayout(new BorderLayout());
        ThemeManager.getInstance().addThemeChangerListener(this);

        tracks = new ArrayList<>();
        trackListComponents = new ArrayList<>();

        // --- 2. Build Top Panel (Header) ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(32, 12, 10, 12));
        topPanel.setOpaque(false);
        topPanel.setAlignmentX(LEFT_ALIGNMENT);
        topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        trackLabel = new JLabel("Track List");
        trackLabel.setFont(fontFactory.createFont("dunbartall_bold", 36));
        topPanel.add(trackLabel);

        topPanel.add(Box.createHorizontalStrut(16));

        RoundedButtonFactory saveButton = createSavePlaylistButton();
        topPanel.add(saveButton);

        // --- 3. Build Track Container (Scrollable) ---
        trackListContainer = new JPanel();
        trackListContainer.setLayout(new BoxLayout(trackListContainer, BoxLayout.Y_AXIS));
        trackListContainer.setOpaque(false);
        trackListContainer.setBorder(BorderFactory.createEmptyBorder(10, 12, 0, 12));

        playLabel = new JLabel("What’s Playing:");
        playLabel.setFont(fontFactory.createFont("dunbartall_bold", 24));
        playLabel.setAlignmentX(LEFT_ALIGNMENT);
        trackListContainer.add(playLabel);
        trackListContainer.add(Box.createVerticalStrut(12));

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

        applyTheme(ThemeManager.getInstance().isDarkMode());
    }

    // --- ADD THIS METHOD ---
    /**
     * Allows MainCardPanel to inject the central MusicPlayerPanel.
     * This enables click-to-play.
     */
    public void setMusicPlayerPanel(MusicPlayerPanel musicPlayerPanel) {
        this.musicPlayerPanel = musicPlayerPanel;
    }

    /**
     * Clears the current list and loads new tracks into the UI.
     */
    public void loadTracksIntoPanel(List<Track> newTracks) {

        while (trackListContainer.getComponentCount() > 3) {
            trackListContainer.remove(2);
        }

        trackListComponents.clear();
        tracks.clear();
        tracks.addAll(newTracks);

        trackListContainer.remove(trackListContainer.getComponentCount() - 1); // Remove VerticalGlue

        int trackNum = 1;
        for (Track track : tracks) {
            
            // --- THIS IS THE FIX ---
            // This now calls the 3-argument constructor for TrackList
            TrackList trackComponent = new TrackList(track, trackNum, this.musicPlayerPanel);

            trackListComponents.add(trackComponent);
            trackListContainer.add(trackComponent);
            trackListContainer.add(Box.createVerticalStrut(8));
            trackNum++;
        }

        trackListContainer.add(Box.createVerticalGlue());

        trackListContainer.revalidate();
        trackListContainer.repaint();
    }

    /**
     * Your original save button code.
     */
    private RoundedButtonFactory createSavePlaylistButton() {
        RoundedButtonFactory button = new RoundedButtonFactory("Save Playlist", 35);
        button.setPreferredSize(new Dimension(133, 29));
        button.setBackground(new Color(0x9D4EDD));
        button.setForeground(new Color(0xF9F6EE));
        button.setFont(fontFactory.createFont("dunbartall_bold", 16));

        button.addActionListener(e -> System.out.println("Save Playlist button clicked!"));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0x7B2CBF));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(0x9D4EDD));
                button.setForeground(new Color(0xF9F6EE));
            }
            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(new Color(0x5A189A));
                button.setForeground(new Color(0x9D4EDD));
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(new Color(0x7B2CBF));
                button.setForeground(new Color(0xF9F6EE));
            }
        });
        return button;
    }

    private void applyTheme(boolean isDarkMode) {
        Color bgColor = ThemeManager.getInstance().getTrackAlbumColor();
        setBackground(bgColor);

        trackListContainer.setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        Color fg = ThemeManager.getInstance().getForegroundColor();
        trackLabel.setForeground(fg);
        playLabel.setForeground(fg);
    }

    @Override
    public void onThemeChanged(boolean isDarkMode) {
        applyTheme(isDarkMode);
        revalidate();
        repaint();
    }
}