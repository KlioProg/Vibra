package com.mycompany.vibra.Content.Main_Page;

import com.mycompany.vibra.Content.Login_Panel.LoginContentPanel;
import com.mycompany.vibra.Content.MainAppFrame;
import com.mycompany.vibra.Content.Main_Page.Main_Contents.Like_Panel.LikedPanel;
import com.mycompany.vibra.Content.Main_Page.Main_Contents.Music_Player.MusicPanel;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.ButtonIconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.DarkModeIconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.IconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.LightModeIconFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundPadderFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundedButtonFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundedIconButtonFactory;
import com.mycompany.vibra.Factories.ThemeFactory.ThemeManager;
import com.mycompany.vibra.Vibra;

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
    private JButton logoutButton;



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
        contentPanel.add(new LikedPanel(), "Liked");

        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(100, getHeight()));
        sidebar.setBackground(ThemeManager.getInstance().getSidebarColor());

        sidebar.setAlignmentX(Component.CENTER_ALIGNMENT);

        logoLabel = new JLabel(iconFactory.createIcon("vibra_logo"));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(logoLabel);

        sidebar.add(Box.createVerticalStrut(32));
        musicLabel = new JLabel(iconFactory.createIcon("music"));
        JPanel musicPanel = createSidebarIcon(musicLabel, "MusicPlayer", "music", "music_hover");
        musicPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(musicPanel);

        sidebar.add(Box.createVerticalStrut(24));


        likedLabel = new JLabel(iconFactory.createIcon("heart"));
        JPanel likedPanel = createSidebarIcon(likedLabel, "Liked", "heart", "heart_hover");
        likedPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(likedPanel);

        sidebar.add(Box.createVerticalGlue());


        logoutButton = createLogoutButton();
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logoutButton);

        sidebar.add(Box.createVerticalStrut(36));

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        changeCard("MusicPlayer");
    }



    private JPanel createSidebarIcon(JLabel label, String cardKey, String iconType, String hoverIconType) {
        RoundPadderFactory rounded = new RoundPadderFactory(12, 15, 15);
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

    private JButton createLogoutButton() {
        JButton logout = RoundedIconButtonFactory.createIconButton(
                iconFactory.createIcon("logout"),
                iconFactory.createIcon("logout_hover"),
                32,
                "Logging out?"
        );

        logout.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                logout.setIcon(iconFactory.createIcon("logout_hover"));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                logout.setIcon(iconFactory.createIcon("logout"));
            }
        });

        logout.addActionListener(e -> {
            JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(logout);
            mainFrame.dispose();

            SwingUtilities.invokeLater(() -> Vibra.createMainFrame().setVisible(true));
        });

        return logout;
    }
    
    private void applyTheme(boolean isDarkMode) {
        // Update the theme factory reference
        iconFactory = isDarkMode ? new DarkModeIconFactory() : new LightModeIconFactory();

        ImageIcon musicIcon = iconFactory.createIcon("music");
        ImageIcon likedIcon = iconFactory.createIcon("heart");
        ImageIcon logoIcon = iconFactory.createIcon("vibra_logo");
        ImageIcon logoutIcon = iconFactory.createIcon("logout");
        ImageIcon logoutHoverIcon = iconFactory.createIcon("logout_hover");

        // Sidebar background
        sidebar.setBackground(ThemeManager.getInstance().getSidebarColor());

        // Update icons
        logoLabel.setIcon(logoIcon);
        musicLabel.setIcon(musicIcon);
        likedLabel.setIcon(likedIcon);

        // 🪄 Update the logout button
        if (logoutButton != null) {
            logoutButton.setIcon(logoutIcon);
            logoutButton.setRolloverIcon(logoutHoverIcon);
            logoutButton.repaint();
        }

        revalidate();
        repaint();
    }



    @Override
    public void onThemeChanged(boolean isDarkMode) {
        applyTheme(isDarkMode);
    }
}
