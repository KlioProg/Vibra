package com.mycompany.vibra.Content.Main_Page.Main_Contents.Album_Panel;

import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.DunbarFactory;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.FontFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundedBackdropFactory;
import com.mycompany.vibra.Factories.Common_UI.GradientPainter;
import com.mycompany.vibra.Factories.ThemeFactory.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AlbumPanel extends JPanel implements ThemeManager.ThemeChangerListener {

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
        JPanel gridPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        gridPanel.setOpaque(false);

        // Add mock data using the new AlbumCardPanel
        gridPanel.add(new AlbumCardPanel(
                new ImageIcon(getClass().getResource("/images/gym.jpg")),
                "Gym", "50 songs"
        ));
        gridPanel.add(new AlbumCardPanel(
                new ImageIcon(getClass().getResource("/images/lofi.jpg")),
                "Lofi Beats", "120 songs"
        ));
        gridPanel.add(new AlbumCardPanel(
                new ImageIcon(getClass().getResource("/images/roadtrip.jpg")),
                "Roadtrip", "88 songs"
        ));


        // Use a scroll pane for the grid
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        darkBackdrop.add(scrollPane, BorderLayout.CENTER);
        add(darkBackdrop, BorderLayout.CENTER);

        applyTheme(ThemeManager.getInstance().isDarkMode());
    }

    private void applyTheme(boolean isDark) {
        Color foreground = ThemeManager.getInstance().getForegroundColor();
        header.setForeground(foreground);
        subheader.setForeground(foreground);
        darkBackdrop.setBackground(ThemeManager.getInstance().getContainerColor());

        // In the future, you could also update the AlbumCardPanel text colors here
        darkBackdrop.repaint();
    }

    @Override
    public void onThemeChanged(boolean isDarkMode) {
        applyTheme(isDarkMode);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Use the centralized GradientPainter utility to draw the background
        GradientPainter.paintRadialGradient(g, this, GRADIENT_COLORS, GRADIENT_FRACTIONS);
    }
}