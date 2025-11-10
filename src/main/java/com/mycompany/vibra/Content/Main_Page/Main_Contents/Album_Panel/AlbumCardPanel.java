package com.mycompany.vibra.Content.Main_Page.Main_Contents.Album_Panel;

import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.DunbarFactory;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.FontFactory;
import com.mycompany.vibra.Factories.ThemeFactory.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class AlbumCardPanel extends JPanel implements ThemeManager.ThemeChangerListener {

    private boolean isHovered = false;
    private final FontFactory fontFactory = new DunbarFactory();

    // UI components to be themed
    private JLabel titleLabel;
    private JLabel countLabel;
    private Color hoverBackgroundColor;

    public AlbumCardPanel(ImageIcon albumArt, String title, String songCount) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(160, 220));
        setMaximumSize(new Dimension(160, 220));

        ThemeManager.getInstance().addThemeChangerListener(this);

        // --- Album Art ---
        JLabel artLabel = new JLabel();
        artLabel.setPreferredSize(new Dimension(150, 150));
        artLabel.setMaximumSize(new Dimension(150, 150));
        artLabel.setHorizontalAlignment(SwingConstants.CENTER);

        if (albumArt != null) {
            // Create a scaled image
            Image scaledImg = albumArt.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            artLabel.setIcon(new ImageIcon(scaledImg));
        }
        artLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- Info Panel ---
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(10, 5, 5, 5));

        titleLabel = new JLabel(title);
        titleLabel.setFont(fontFactory.createFont("dunbartall_bold", 16));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        countLabel = new JLabel(songCount);
        countLabel.setFont(fontFactory.createFont("dunbartall_book", 14));
        countLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(countLabel);
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(artLabel);
        add(infoPanel);

        // --- Interactivity ---
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Clicked on album: " + title);
                // Future logic: switch to a detailed view of this album
            }
        });

        applyTheme(ThemeManager.getInstance().isDarkMode());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set background color based on hover state
        if (isHovered) {
            g2d.setColor(hoverBackgroundColor); // Use theme-aware hover color
            g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
        }

        g2d.dispose();
    }

    private void applyTheme(boolean isDark) {
        ThemeManager tm = ThemeManager.getInstance();
        hoverBackgroundColor = tm.getCardHoverColor();
        titleLabel.setForeground(tm.getForegroundColor());
        countLabel.setForeground(tm.getSecondaryForegroundColor());
        repaint();
    }

    @Override
    public void onThemeChanged(boolean isDarkMode) {
        applyTheme(isDarkMode);
    }
}