package com.mycompany.vibra.Content.Main_Page.Main_Contents.Album_Panel;

import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.DunbarFactory;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.FontFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundedBackdropFactory;
import com.mycompany.vibra.Content.Main_Page.Main_Contents.TrackLists.TrackListPanel;
import com.mycompany.vibra.Factories.Common_UI.GradientPainter;
import com.mycompany.vibra.Factories.ThemeFactory.ThemeManager;
import com.mycompany.vibra.Factories.Common_UI.CustomScrollBarUI; // ✅ NEW IMPORT
import com.mycompany.vibra.musicUtilities.Track;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.Comparator;

import java.util.List;
import java.util.Map;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;


public class AlbumPanel extends JPanel implements ThemeManager.ThemeChangerListener {

    private TrackListPanel trackListPanel;
    private JPanel gridPanel;
    private JScrollPane scrollPane; // ✅ MADE FIELD TO ACCESS SCROLLBAR

    // Design System for this panel
    private static final Color GRADIENT_COLOR_CENTER = new Color(0, 119, 255); // Bright Blue
    private static final Color GRADIENT_COLOR_EDGE = new Color(25, 0, 87);     // Dark Blue/Purple

    // Gradient properties
    private static final float[] GRADIENT_FRACTIONS = {0.0f, 1.0f};
    private static final Color[] GRADIENT_COLORS = {GRADIENT_COLOR_CENTER, GRADIENT_COLOR_EDGE};

    private final FontFactory fontFactory = new DunbarFactory();

    private JLabel header;
    private JLabel subheader;
    private RoundedBackdropFactory darkBackdrop;

    public void setTrackListPanel(TrackListPanel trackListPanel) {
        this.trackListPanel = trackListPanel;
    }

    public void displayRealAlbums(Map<String, List<Track>> albums) {
        // 1. Clear any old data
        gridPanel.removeAll();

        // 2. Loop through the real album map
        for (Map.Entry<String, List<Track>> entry : albums.entrySet()) {
            String albumTitle = entry.getKey();
            List<Track> tracksInAlbum = entry.getValue();

            // 3. Get album art from the first track
            ImageIcon albumArtIcon = null;
            if (!tracksInAlbum.isEmpty()) {
                Image art = tracksInAlbum.get(0).getAlbumArtImage();
                if (art != null) {
                    // Use the existing scaling logic from AlbumCardPanel
                    albumArtIcon = new ImageIcon(art.getScaledInstance(150, 150, Image.SCALE_SMOOTH));
                }
            }

            // 4. Create a new card (using the new constructor)
            AlbumCardPanel card = new AlbumCardPanel(albumArtIcon, albumTitle, tracksInAlbum);

            // 5. Add the click listener
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("Loading album: " + albumTitle);
                    if (trackListPanel != null) {
                        tracksInAlbum.sort(Comparator.comparingInt(Track::getTrackNumber));
                        trackListPanel.loadTracksIntoPanel(tracksInAlbum);
                    }
                }
            });

            // 6. Add the new, real card to the grid
            gridPanel.add(card);
        }

        // 7. Refresh the UI
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    public AlbumPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        ThemeManager.getInstance().addThemeChangerListener(this);

        // --- Top Header Section ---
        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setOpaque(false);

        header = new JLabel("Library");
        header.setFont(fontFactory.createFont("dunbartall_bold", 50));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        topSection.add(header);

        topSection.add(Box.createVerticalStrut(5));

        subheader = new JLabel("Your Curated Selection:");
        subheader.setFont(fontFactory.createFont("dunbartall_bold", 20));
        subheader.setAlignmentX(Component.LEFT_ALIGNMENT);
        topSection.add(subheader);

        add(topSection, BorderLayout.NORTH);

        // --- Inner Dark Content Panel ---
        darkBackdrop = new RoundedBackdropFactory(new BorderLayout(), 20);
        darkBackdrop.setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- Album Grid ---
        gridPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        gridPanel.setOpaque(false);

        // Use a scroll pane for the grid
        scrollPane = new JScrollPane(gridPanel); // ✅ ASSIGNED TO FIELD
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        darkBackdrop.add(scrollPane, BorderLayout.CENTER);
        add(darkBackdrop, BorderLayout.CENTER);

        applyTheme(ThemeManager.getInstance().isDarkMode());
        applyScrollBarTheme(); // ✅ APPLY ON INITIAL LOAD
    }

    /**
     * Applies the custom UI to the scrollbar based on the current theme.
     */
    private void applyScrollBarTheme() {
        if (scrollPane == null) return;

        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUI(new CustomScrollBarUI());

        verticalScrollBar.revalidate();
        verticalScrollBar.repaint();

        // Match the corner color to the backdrop color
        scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, new JPanel());
        scrollPane.getCorner(JScrollPane.UPPER_RIGHT_CORNER).setBackground(ThemeManager.getInstance().getContainerColor());
    }

    private void applyTheme(boolean isDark) {
        Color foreground = ThemeManager.getInstance().getForegroundColor();
        header.setForeground(foreground);
        subheader.setForeground(foreground);

        Color backdropColor = ThemeManager.getInstance().getContainerColor();
        darkBackdrop.setBackground(backdropColor);

        // Ensure the scroll pane viewport matches the backdrop color
        if (scrollPane != null && scrollPane.getViewport() != null) {
            scrollPane.getViewport().setBackground(backdropColor);
        }

        // Repaint to reflect color changes
        darkBackdrop.repaint();
    }

    @Override
    public void onThemeChanged(boolean isDarkMode) {
        applyTheme(isDarkMode);
        applyScrollBarTheme(); // ✅ UPDATE SCROLLBAR ON THEME CHANGE
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Use the centralized GradientPainter utility to draw the background
        GradientPainter.paintRadialGradient(g, this, GRADIENT_COLORS, GRADIENT_FRACTIONS);
    }
}