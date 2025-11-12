package com.mycompany.vibra.Content.Main_Page.Main_Contents.Album_Panel;

import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.DunbarFactory;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.FontFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundedBackdropFactory;
import com.mycompany.vibra.Content.Main_Page.Main_Contents.TrackLists.TrackListPanel;
import com.mycompany.vibra.Factories.Common_UI.GradientPainter;
import com.mycompany.vibra.Factories.ThemeFactory.ThemeManager;
import com.mycompany.vibra.Factories.Common_UI.CustomScrollBarUI;
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
    private JScrollPane scrollPane;

    // Design System
    private static final Color GRADIENT_COLOR_CENTER = new Color(0, 119, 255);
    private static final Color GRADIENT_COLOR_EDGE = new Color(25, 0, 87);
    private static final float[] GRADIENT_FRACTIONS = {0.0f, 1.0f};
    private static final Color[] GRADIENT_COLORS = {GRADIENT_COLOR_CENTER, GRADIENT_COLOR_EDGE};

    private final FontFactory fontFactory = new DunbarFactory();

    private JLabel header;
    private JLabel subheader;
    private RoundedBackdropFactory darkBackdrop;

    public void setTrackListPanel(TrackListPanel trackListPanel) {
        this.trackListPanel = trackListPanel;
    }

    // --- ⬇️ THIS IS THE MODIFIED METHOD ⬇️ ---
    public void displayRealAlbums(Map<String, List<Track>> albums) {
        // 1. Clear any old data
        gridPanel.removeAll();

        // --- 2. DEFINE THE HOVER COLOR ---
        Color hoverColor = new Color(0x535353); // The gray hover you want

        // 3. Loop through the real album map
        for (Map.Entry<String, List<Track>> entry : albums.entrySet()) {
            String albumTitle = entry.getKey();
            List<Track> tracksInAlbum = entry.getValue();

            // 4. Get album art
            ImageIcon albumArtIcon = null;
            if (!tracksInAlbum.isEmpty()) {
                Image art = tracksInAlbum.get(0).getAlbumArtImage();
                if (art != null) {
                    albumArtIcon = new ImageIcon(art.getScaledInstance(150, 150, Image.SCALE_SMOOTH));
                }
            }

            // 5. Create a new card
            AlbumCardPanel card = new AlbumCardPanel(albumArtIcon, albumTitle, tracksInAlbum);

            // Store the card's original state (we assume it's transparent)
            Color originalColor = card.getBackground();

            // 6. Add the click AND hover listener
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("Loading album: " + albumTitle);
                    if (trackListPanel != null) {
                        tracksInAlbum.sort(Comparator.comparingInt(Track::getTrackNumber));
                        trackListPanel.loadTracksIntoPanel(tracksInAlbum);
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    card.setOpaque(true); // Make it paint its background
                    card.setBackground(hoverColor); // Set the gray hover
                    card.repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    card.setOpaque(false); // Make it transparent again
                    card.setBackground(originalColor); // Reset to original color
                    card.repaint();
                }
            });
            // --- ⬆️ END OF MODIFIED LOGIC ⬆️ ---

            // 7. Add the new, real card to the grid
            gridPanel.add(card);
        }

        // 8. Refresh the UI
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    // Inside your AlbumPanel.java
    public AlbumPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        ThemeManager.getInstance().addThemeChangerListener(this);

        // --- Top Header Section (Unchanged) ---
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

        // --- Album Grid ---
        gridPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        gridPanel.setOpaque(false);
        gridPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Padding

        // --- Scroll Pane (Unchanged) ---
        scrollPane = new JScrollPane(gridPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        darkBackdrop.add(scrollPane, BorderLayout.CENTER);
        add(darkBackdrop, BorderLayout.CENTER);

        applyTheme(ThemeManager.getInstance().isDarkMode());
        applyScrollBarTheme();
    }

    private void applyScrollBarTheme() {
        if (scrollPane == null) return;
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUI(new CustomScrollBarUI());
        JPanel corner = new JPanel();
        corner.setOpaque(false); // Transparent corner
        scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, corner);
        scrollPane.getCorner(JScrollPane.UPPER_RIGHT_CORNER).setBackground(ThemeManager.getInstance().getContainerColor());
    }

    private void applyTheme(boolean isDark) {
        Color foreground = ThemeManager.getInstance().getForegroundColor();
        header.setForeground(foreground);
        subheader.setForeground(foreground);

        Color backdropColor = ThemeManager.getInstance().getContainerColor();
        darkBackdrop.setBackground(backdropColor);

        if (scrollPane != null && scrollPane.getViewport() != null) {
            scrollPane.getViewport().setBackground(backdropColor);
        }
        darkBackdrop.repaint();
    }

    @Override
    public void onThemeChanged(boolean isDarkMode) {
        applyTheme(isDarkMode);
        applyScrollBarTheme();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        GradientPainter.paintRadialGradient(g, this, GRADIENT_COLORS, GRADIENT_FRACTIONS);
    }
}