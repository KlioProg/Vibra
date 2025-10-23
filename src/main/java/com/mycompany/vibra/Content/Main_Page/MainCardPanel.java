package com.mycompany.vibra.Content.Main_Page;

import com.mycompany.vibra.Content.Main_Page.Main_Contents.Like_Panel.likedPanel;
import com.mycompany.vibra.Content.Main_Page.Main_Contents.Music_Player.MusicPanel;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.ButtonIconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.DarkModeIconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.IconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.LightModeIconFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundPadderFactory;
import com.mycompany.vibra.Factories.ThemeFactory.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainCardPanel extends JPanel implements ThemeManager.ThemeChangerListener {

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JPanel sidebar;
    private IconFactory iconFactory;
    private IconFactory themeFactory;

    // Keep references to sidebar icons for theme changes
    private JLabel logoLabel;
    private JLabel musicLabel;
    private JLabel likedLabel;

    public MainCardPanel() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        iconFactory = ThemeManager.getInstance().isDarkMode() ? new DarkModeIconFactory() : new LightModeIconFactory();
        initializePage();

        // Register for theme updates
        ThemeManager.getInstance().addThemeChangerListener(this);

        // Apply initial theme
        applyTheme(ThemeManager.getInstance().isDarkMode());
    }

    private void changeCard(String text){
        cardLayout.show(contentPanel, text);
    }

    private void initializePage() {
        setLayout(new BorderLayout());

        boolean isDark = ThemeManager.getInstance().isDarkMode();
        iconFactory = isDark ? new DarkModeIconFactory() : new LightModeIconFactory();

        contentPanel.add(new MusicPanel(iconFactory), "MusicPlayer");
        contentPanel.add(new likedPanel(iconFactory), "Liked");

        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(100, getHeight()));
        sidebar.setBackground(ThemeManager.getInstance().getSidebarColor());

        logoLabel = new JLabel(iconFactory.createIcon("vibra_logo"));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(logoLabel);

        sidebar.add(Box.createVerticalStrut(44));
        musicLabel = new JLabel(iconFactory.createIcon("music"));
        sidebar.add(createSidebarIcon(musicLabel, "MusicPlayer", "music", "music_hover"));

        sidebar.add(Box.createVerticalStrut(24));
        likedLabel = new JLabel(iconFactory.createIcon("heart"));
        sidebar.add(createSidebarIcon(likedLabel, "Liked", "heart", "heart_hover"));

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        changeCard("MusicPlayer");
    }


    private JPanel createSidebarIcon(JLabel label, String cardKey, String iconType, String hoverIconType) {
        RoundPadderFactory rounded = new RoundPadderFactory(12, 6, 6);
        rounded.setLayout(new BorderLayout());
        rounded.add(label, BorderLayout.CENTER);

        rounded.setMaximumSize(new Dimension(60, 60));
        rounded.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Initial state
        rounded.setPaintBackground(false);

        rounded.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(contentPanel, cardKey);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                rounded.setBackground(Color.decode("#9D4EDD")); // purple
                rounded.setPaintBackground(true);
                rounded.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                label.setIcon(iconFactory.createIcon(hoverIconType));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                rounded.setPaintBackground(false);
                rounded.setCursor(Cursor.getDefaultCursor());

                label.setIcon(iconFactory.createIcon(iconType));
            }
        });

        return rounded;
    }




    private void applyTheme(boolean isDarkMode) {
        // Update the theme factory reference
        iconFactory = isDarkMode ? new DarkModeIconFactory() : new LightModeIconFactory();

        ImageIcon musicIcon = iconFactory.createIcon("music");
        ImageIcon likedIcon = iconFactory.createIcon("heart");
        ImageIcon logoIcon = iconFactory.createIcon("vibra_logo");

        // Sidebar background
        sidebar.setBackground(ThemeManager.getInstance().getSidebarColor());

        // Update icons
        logoLabel.setIcon(logoIcon);
        musicLabel.setIcon(musicIcon);
        likedLabel.setIcon(likedIcon);

        revalidate();
        repaint();
    }

    @Override
    public void onThemeChanged(boolean isDarkMode) {
        applyTheme(isDarkMode);
    }
}
