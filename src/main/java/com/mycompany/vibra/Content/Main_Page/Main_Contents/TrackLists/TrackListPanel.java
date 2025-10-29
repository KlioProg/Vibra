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

import com.mycompany.vibra.Factories.Common_UI.RoundedButtonFactory;
import com.mycompany.vibra.Factories.Common_UI.FontLoaderFactory;
import com.mycompany.vibra.Factories.ThemeFactory.ThemeManager;
import com.mycompany.vibra.musicUtilities.AudioPlayer;
import com.mycompany.vibra.musicUtilities.Track;

public class TrackListPanel extends JPanel implements ThemeManager.ThemeChangerListener {

    // --- Fields ---
    private ArrayList<TrackList> trackListComponents; // Holds the UI components
    private ArrayList<Track> tracks;                  // Holds the track data
    private AudioPlayer audioPlayer;                  // The one player for the app
    private JPanel trackListContainer;                // The panel that holds the actual track JButtons
    private JScrollPane scrollPane;                   // To make the list scrollable

    private JLabel trackLabel;
    private JLabel playLabel;

    public TrackListPanel(AudioPlayer audioPlayer) {
        setLayout(new BorderLayout());
        ThemeManager.getInstance().addThemeChangerListener(this);

        this.audioPlayer = audioPlayer;
        tracks = new ArrayList<>();
        trackListComponents = new ArrayList<>();

        // Set the static AudioPlayer for all TrackList instances to use
        TrackList.setAudioPlayer(audioPlayer);

        // --- 2. Build Top Panel (Header) ---
        // This is your exact UI from your sample
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(32, 12, 10, 12));
        topPanel.setOpaque(false);
        topPanel.setAlignmentX(LEFT_ALIGNMENT);
        topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        trackLabel = new JLabel("Track List");
        trackLabel.setFont(FontLoaderFactory.loadFont("/fonts/DunbarTall-Bold.ttf", 36));
        topPanel.add(trackLabel);

        topPanel.add(Box.createHorizontalStrut(16)); // From your sample

        RoundedButtonFactory saveButton = createSavePlaylistButton(); // Your save button
        topPanel.add(saveButton);

        // --- 3. Build Track Container (Scrollable) ---
        trackListContainer = new JPanel();
        trackListContainer.setLayout(new BoxLayout(trackListContainer, BoxLayout.Y_AXIS));
        trackListContainer.setOpaque(false);
        trackListContainer.setBorder(BorderFactory.createEmptyBorder(10, 12, 0, 12));

        playLabel = new JLabel("What’s Playing:");
        playLabel.setFont(FontLoaderFactory.loadFont("/fonts/DunbarTall-Bold.ttf", 24f));
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
        add(topPanel, BorderLayout.NORTH);     // Add header to the top
        add(scrollPane, BorderLayout.CENTER);  // Add scrollable list to the center

        applyTheme(ThemeManager.getInstance().isDarkMode());
    }

    /**
     * ✅ PUBLIC method so other panels can load tracks.
     * Clears the current list and loads the new tracks into the UI.
     */
    public void loadTracksIntoPanel(List<Track> newTracks) {

        // A safe way to clear: remove all components *except* the first three (label, strut, glue)
        while (trackListContainer.getComponentCount() > 3) {
            trackListContainer.remove(2); // Repeatedly remove the component at index 2
        }

        // --- 2. Clear old data ---
        trackListComponents.clear();
        tracks.clear();

        // --- 3. Add new data ---
        tracks.addAll(newTracks);

        // We need to remove the glue, add tracks, then add glue back
        trackListContainer.remove(trackListContainer.getComponentCount() - 1); // Remove VerticalGlue

        int trackNum = 1;
        for (Track track : tracks) {
            // ✅ This is where it creates your button component
            TrackList trackComponent = new TrackList(track, trackNum);

            trackListComponents.add(trackComponent);   // Add to our internal list
            trackListContainer.add(trackComponent);    // Add to the UI panel
            trackListContainer.add(Box.createVerticalStrut(8)); // Add spacing
            trackNum++;
        }

        trackListContainer.add(Box.createVerticalGlue()); // Add glue back at the end

        // --- 4. Refresh the UI ---
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
        button.setFont(FontLoaderFactory.loadFont("/fonts/DunbarTall-Bold.ttf", 16f));

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