/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vibra.Content.Main_Page.Main_Contents.Like_Panel;

import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.DunbarFactory;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.FontFactory;
import com.mycompany.vibra.Factories.ThemeFactory.ThemeManager;
// ✅ Imports needed for custom scrollbar
import com.mycompany.vibra.Factories.Common_UI.CustomScrollBarUI;
import javax.swing.JScrollBar;


import java.awt.*;
import javax.swing.*;

/**
 *
 * @author robbi
 */
// ✅ Implement ThemeChangerListener to ensure theme updates
public class LikedSongsPanel extends JPanel implements ThemeManager.ThemeChangerListener {

    private JPanel songsListPanel;
    private JScrollPane scrollPane; // Make scrollPane a field to access it later

    FontFactory fontFactory = new DunbarFactory();

    public LikedSongsPanel() {
        // Register listener for theme updates
        ThemeManager.getInstance().addThemeChangerListener(this);

        setLayout(new BorderLayout());
        // Set background using ThemeManager for initial dark/light mode setup
        setBackground(ThemeManager.getInstance().getTrackAlbumColor());


        JLabel header = new JLabel("Liked Songs");
        header.setFont(fontFactory.createFont("dunbartall_bold", 60)); // Changed to bold for style
        header.setForeground(ThemeManager.getInstance().getForegroundColor());
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(header, BorderLayout.NORTH);


        songsListPanel = new JPanel();
        songsListPanel.setLayout(new BoxLayout(songsListPanel, BoxLayout.Y_AXIS));
        songsListPanel.setOpaque(false);

        scrollPane = new JScrollPane(songsListPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); // Typically tracks don't scroll horizontally

        add(scrollPane, BorderLayout.CENTER);

        // Apply initial theme and scrollbar look
        applyTheme();
        applyScrollBarTheme(); // ✅ Apply custom scrollbar UI

        addColumnHeaders();
        loadLikedSongs();
    }

    private void addColumnHeaders() {
        Color bgColor = ThemeManager.getInstance().getTrackAlbumColor().darker(); // Use a slightly darker color for contrast
        Color fgColor = ThemeManager.getInstance().getAccentColor(); // Use accent color for headers

        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBackground(bgColor);
        // Fixed dimension/layout issue: use setMaximumSize and alignment
        headerPanel.setAlignmentX(LEFT_ALIGNMENT);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        headerPanel.setMinimumSize(new Dimension(100, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; // Use HORIZONTAL fill for columns
        gbc.gridy = 0;
        gbc.weighty = 1;
        gbc.insets = new Insets(5, 10, 5, 10);

        double[] colWeights = {0.05, 0.5, 0.25, 0.15};

        JLabel indexHeader = createHeaderLabel("#", fgColor, SwingConstants.CENTER);
        JLabel titleHeader = createHeaderLabel("Title", fgColor, SwingConstants.LEFT);
        JLabel artistHeader = createHeaderLabel("Artist", fgColor, SwingConstants.LEFT);
        JLabel durationHeader = createHeaderLabel("Duration", fgColor, SwingConstants.CENTER);

        gbc.gridx = 0; gbc.weightx = colWeights[0]; headerPanel.add(indexHeader, gbc);
        gbc.gridx = 1; gbc.weightx = colWeights[1]; headerPanel.add(titleHeader, gbc);
        gbc.gridx = 2; gbc.weightx = colWeights[2]; headerPanel.add(artistHeader, gbc);
        gbc.gridx = 3; gbc.weightx = colWeights[3]; headerPanel.add(durationHeader, gbc);

        songsListPanel.add(headerPanel);
    }

    private void loadLikedSongs() {
        // Ensure songsListPanel is empty except for header
        songsListPanel.removeAll();
        addColumnHeaders(); // Re-add the header

        addSongEntry(1, "Pasilyo", "Sunkissed Lola", "4:30");
        addSongEntry(2, "Pagtingin", "Ben & Ben", "3:47");
        addSongEntry(3, "Maybe Maybe", "Lola Amour", "5:13");
        // Add glue to push content to the top
        songsListPanel.add(Box.createVerticalGlue());

        songsListPanel.revalidate();
        songsListPanel.repaint();
    }

    // Updated to accept index to avoid miscounting components
    private void addSongEntry(int index, String title, String artist, String duration) {
        Color bgColor = ThemeManager.getInstance().getTrackAlbumColor().darker();
        Color fgColor = ThemeManager.getInstance().getForegroundColor();

        JPanel songRow = new JPanel(new GridBagLayout());
        songRow.setBackground(bgColor);
        songRow.setAlignmentX(LEFT_ALIGNMENT);
        songRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        songRow.setMinimumSize(new Dimension(100, 70));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;
        gbc.weighty = 1;
        gbc.insets = new Insets(5, 10, 5, 10);

        double[] colWeights = {0.05, 0.5, 0.25, 0.15};

        JLabel indexLabel = createCellLabel(String.valueOf(index), Color.LIGHT_GRAY, false, SwingConstants.CENTER);

        // Column 1: Title + Cover
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        titlePanel.setOpaque(false);

        // Placeholder for album cover
        JPanel coverPlaceholder = new JPanel();
        coverPlaceholder.setPreferredSize(new Dimension(50, 50)); // Adjusted size
        coverPlaceholder.setBackground(ThemeManager.getInstance().getAccentColor());
        coverPlaceholder.setBorder(BorderFactory.createLineBorder(ThemeManager.getInstance().getAccentColor(), 1, true));

        JLabel titleLabel = createCellLabel(title, fgColor, true, SwingConstants.LEFT);

        // Use a vertical box layout to stack title/artist if needed, or just keep title
        // For simplicity, keeping title only next to cover
        titlePanel.add(coverPlaceholder);
        titlePanel.add(titleLabel);

        JLabel artistLabel = createCellLabel(artist, Color.LIGHT_GRAY, false, SwingConstants.LEFT);
        JLabel durationLabel = createCellLabel(duration, Color.LIGHT_GRAY, false, SwingConstants.CENTER);

        gbc.gridx = 0; gbc.weightx = colWeights[0]; songRow.add(indexLabel, gbc);
        gbc.gridx = 1; gbc.weightx = colWeights[1]; songRow.add(titlePanel, gbc);
        gbc.gridx = 2; gbc.weightx = colWeights[2]; songRow.add(artistLabel, gbc);
        gbc.gridx = 3; gbc.weightx = colWeights[3]; songRow.add(durationLabel, gbc);

        songsListPanel.add(songRow);
    }

    private JLabel createHeaderLabel(String text, Color color, int align) {
        JLabel lbl = new JLabel(text, align);
        lbl.setForeground(color);
        lbl.setFont(fontFactory.createFont("dunbartall_bold", 18)); // Smaller bold font for header
        return lbl;
    }

    private JLabel createCellLabel(String text, Color color, boolean bold, int align) {
        JLabel lbl = new JLabel(text, align);
        lbl.setForeground(color);
        // Use the appropriate font style
        String fontStyle = bold ? "dunbartall_bold" : "dunbartall_book";
        lbl.setFont(fontFactory.createFont(fontStyle, 16));
        return lbl;
    }

    // --- Theme Listener Implementation ---

    private void applyTheme() {
        ThemeManager tm = ThemeManager.getInstance();
        Color bgColor = tm.getTrackAlbumColor();
        Color fgColor = tm.getForegroundColor();

        setBackground(bgColor);

        // Update header
        Component header = getComponent(0);
        if (header instanceof JLabel) {
            ((JLabel) header).setForeground(fgColor);
        }

        // Update song rows (simplistic re-load for theme change)
        // In a complex app, you'd iterate and update each row's components.
        // For now, re-load the rows to pick up new colors:
        loadLikedSongs();
    }

    // ✅ NEW HELPER METHOD: Apply custom scrollbar
    private void applyScrollBarTheme() {
        if (scrollPane == null) return;

        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        // Set the custom UI class
        verticalScrollBar.setUI(new CustomScrollBarUI());

        verticalScrollBar.revalidate();
        verticalScrollBar.repaint();

        // Remove the corner square and fill it with the track color
        scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, new JPanel());
        scrollPane.getCorner(JScrollPane.UPPER_RIGHT_CORNER).setBackground(ThemeManager.getInstance().getTrackAlbumColor());
    }

    @Override
    public void onThemeChanged(boolean isDarkMode) {
        applyTheme();
        applyScrollBarTheme();
        revalidate();
        repaint();
    }
}