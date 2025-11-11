package com.mycompany.vibra.Content.Main_Page;

import com.mycompany.vibra.Content.Main_Page.Main_Contents.Album_Panel.AlbumPanel;
import com.mycompany.vibra.Content.Main_Page.Main_Contents.Like_Panel.LikedPanel;
import com.mycompany.vibra.Content.Main_Page.Main_Contents.Music_Player.MainLibraryPanel;
import com.mycompany.vibra.Content.Main_Page.Main_Contents.Music_Player.MusicPlayerPanel;
import com.mycompany.vibra.Content.Main_Page.Main_Contents.TrackLists.TrackListPanel;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.DarkModeIconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.IconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.LightModeIconFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundPadderFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundedIconButtonFactory;
import com.mycompany.vibra.Factories.ThemeFactory.ThemeManager;
import com.mycompany.vibra.Vibra;

import com.mycompany.vibra.musicUtilities.AudioPlayer;
import com.mycompany.vibra.model.Playlist;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainCardPanel extends JPanel implements ThemeManager.ThemeChangerListener {

    private CardLayout cardLayout;
    private JPanel contentPanel; // This is the panel with CardLayout
    private JPanel sidebar;
    private IconFactory iconFactory;
    private LikedPanel likedPanelInstance;

    // Keep references to sidebar icons for theme changes
    private JLabel logoLabel;
    private JLabel musicLabel;
    private JLabel albumLabel;
    private JLabel likedLabel;
    private JButton logoutButton;

    // References to all the main panels
    private TrackListPanel trackListPanel;
    private MusicPlayerPanel musicPlayerPanel;
    private MainLibraryPanel mainLibraryPanel;
    private AudioPlayer audioPlayer;
    private Playlist mainPlaylist;
    private AlbumPanel albumPanelInstance;

    private final int currentUserID;

    public MainCardPanel(int userID) {
        this.currentUserID = userID;

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout); // This panel will swap cards

        iconFactory = ThemeManager.getInstance().isDarkMode() ? new DarkModeIconFactory() : new LightModeIconFactory();
        initializePage();

        // Register for theme updates
        ThemeManager.getInstance().addThemeChangerListener(this);

        // Apply initial theme
        applyTheme(ThemeManager.getInstance().isDarkMode());
    }

    public MainCardPanel() {
        this(0);
    }

    private void changeCard(String text){
        cardLayout.show(contentPanel, text);
    }

    
    private void initializePage() {
        setLayout(new BorderLayout()); // MainCardPanel uses BorderLayout

        boolean isDark = ThemeManager.getInstance().isDarkMode();
        iconFactory = isDark ? new DarkModeIconFactory() : new LightModeIconFactory();

        // --- 1. Instantiate all shared components ---
        likedPanelInstance = new LikedPanel(this.currentUserID);
        albumPanelInstance = new AlbumPanel();
        audioPlayer = new AudioPlayer();
        mainPlaylist = new Playlist(1, "My Playlist", this.currentUserID); 

        // --- 2. Build Sidebar (with new Album button) ---
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

        albumLabel = new JLabel(iconFactory.createIcon("album_cover"));
        JPanel albumPanel = createSidebarIcon(albumLabel, "Album", "album_cover", "album_cover_hover");
        albumPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(albumPanel);

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

        // --- 3. Create the Main Content Area ---
        JPanel mainContentArea = new JPanel(new BorderLayout());

        // --- 4. Instantiate all panels, injecting dependencies ---
        
        // Create the panels that others depend on first.
        // This is the correct version (with currentUserID)
        musicPlayerPanel = new MusicPlayerPanel(audioPlayer, mainPlaylist, likedPanelInstance, this.currentUserID);
        
        // This is the correct way to init TrackListPanel (no args)
        trackListPanel = new TrackListPanel(); 
        mainLibraryPanel = new MainLibraryPanel(musicPlayerPanel, trackListPanel, albumPanelInstance);
        
        // --- 5. Inject dependencies ---
        // This gives LikedPanel and TrackListPanel a reference to the player
        // so click-to-play works.
        likedPanelInstance.setMusicPlayerPanel(musicPlayerPanel);
        trackListPanel.setMusicPlayerPanel(musicPlayerPanel);
        albumPanelInstance.setTrackListPanel(trackListPanel);
        

        // --- 6. Build the "MusicPlayer" card ---
        JPanel musicPlayerCard = new JPanel(new BorderLayout());
        musicPlayerPanel.setPreferredSize(new Dimension(610, 0));
        mainLibraryPanel.setPreferredSize(new Dimension(402, 0));
        musicPlayerCard.add(musicPlayerPanel, BorderLayout.CENTER);
        musicPlayerCard.add(mainLibraryPanel, BorderLayout.EAST);

        // --- Add the persistent TrackListPanel to the main content area ---
        trackListPanel.setPreferredSize(new Dimension(332, 0));
        mainContentArea.add(trackListPanel, BorderLayout.WEST);

        // --- 7. Add cards to the contentPanel (the one with CardLayout) ---
        contentPanel.add(musicPlayerCard, "MusicPlayer"); // Card 1
        contentPanel.add(likedPanelInstance, "Liked");    // Card 2
        contentPanel.add(albumPanelInstance, "Album");       // Card 3 (from your friend)

        // --- 8. Add the swappable contentPanel to the main area ---
        mainContentArea.add(contentPanel, BorderLayout.CENTER);

        // --- 9. Add the entire main area to MainCardPanel ---
        add(mainContentArea, BorderLayout.CENTER);

        changeCard("MusicPlayer"); // Show the player by default
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
                changeCard(cardKey); // This will show "MusicPlayer", "Liked", or "Album"
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
        ImageIcon albumIcon = iconFactory.createIcon("album_cover"); // For the new button
        ImageIcon logoIcon = iconFactory.createIcon("vibra_logo");
        ImageIcon logoutIcon = iconFactory.createIcon("logout");
        ImageIcon logoutHoverIcon = iconFactory.createIcon("logout_hover");

        // Sidebar background
        sidebar.setBackground(ThemeManager.getInstance().getSidebarColor());

        // Update icons
        logoLabel.setIcon(logoIcon);
        musicLabel.setIcon(musicIcon);
        likedLabel.setIcon(likedIcon);
        albumLabel.setIcon(albumIcon); // Update the new icon

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