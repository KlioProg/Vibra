package com.mycompany.vibra.Content.Main_Page.Main_Contents.TrackLists;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.mycompany.vibra.Factories.Common_UI.RoundedButtonFactory;
import com.mycompany.vibra.Factories.Common_UI.FontLoaderFactory;
import com.mycompany.vibra.Factories.ThemeFactory.ThemeManager;

public class TrackListPanel extends JPanel implements ThemeManager.ThemeChangerListener {
    ArrayList<TrackList> trackList = new ArrayList<>();

    private JLabel trackLabel;
    private JLabel playLabel;

    public TrackListPanel() {
        setLayout(new BorderLayout());

        // Register for theme changes
        ThemeManager.getInstance().addThemeChangerListener(this);

        JPanel trackPanel = new JPanel();
        trackPanel.setLayout(new BoxLayout(trackPanel, BoxLayout.Y_AXIS));
        trackPanel.setOpaque(false);
        trackPanel.setBorder(BorderFactory.createEmptyBorder(32, 12, 0, 0));
        trackPanel.setPreferredSize(new Dimension(331, Integer.MAX_VALUE));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        topPanel.setOpaque(false);
        topPanel.setAlignmentX(LEFT_ALIGNMENT);
        topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        trackLabel = new JLabel("Track List");
        trackLabel.setFont(FontLoaderFactory.loadFont("/fonts/DunbarTall-Bold.ttf", 36));
        topPanel.add(trackLabel);

        topPanel.add(Box.createHorizontalStrut(10));

        RoundedButtonFactory roundButton = createSavePlaylistButton();
        topPanel.add(roundButton);

        trackPanel.add(topPanel);
        trackPanel.add(Box.createVerticalStrut(8));

        playLabel = new JLabel("Playing next:");
        playLabel.setFont(FontLoaderFactory.loadFont("/fonts/DunbarTall-Bold.ttf", 20f));
        playLabel.setAlignmentX(LEFT_ALIGNMENT);
        trackPanel.add(playLabel);

        trackPanel.add(Box.createVerticalStrut(20));

        add(trackPanel, BorderLayout.WEST);

        // Apply current theme immediately
        applyTheme(ThemeManager.getInstance().isDarkMode());
    }

    private RoundedButtonFactory createSavePlaylistButton() {
        RoundedButtonFactory button = new RoundedButtonFactory("Save Playlist", 35);
        button.setPreferredSize(new Dimension(133, 29));
        button.setBackground(new Color(0x9D4EDD));
        button.setForeground(new Color(0xF9F6EE));
        button.setFont(FontLoaderFactory.loadFont("/fonts/DunbarTall-Bold.ttf", 16f));

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Save Playlist button clicked!");
            }
        });

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
        // Background of this panel
        setBackground(ThemeManager.getInstance().getTrackAlbumColor());

        // Update labels
        Color fg = ThemeManager.getInstance().getForegroundColor();
        trackLabel.setForeground(fg);
        playLabel.setForeground(fg);

        // Recursively apply to children if needed
        applyThemeToContainer(this, isDarkMode);
    }

    private void applyThemeToContainer(Container container, boolean isDarkMode) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JLabel label) {
                label.setForeground(ThemeManager.getInstance().getForegroundColor());
            } else if (comp instanceof Container sub) {
                applyThemeToContainer(sub, isDarkMode);
            }
        }
    }

    @Override
    public void onThemeChanged(boolean isDarkMode) {
        applyTheme(isDarkMode);
        revalidate();
        repaint();
    }
}
