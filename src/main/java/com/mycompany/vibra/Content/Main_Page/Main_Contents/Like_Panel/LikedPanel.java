package com.mycompany.vibra.Content.Main_Page.Main_Contents.Like_Panel;

import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.DunbarFactory;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.FontFactory;
import com.mycompany.vibra.dao.LikedSongsDao;
import com.mycompany.vibra.musicUtilities.Track;
import com.mycompany.vibra.Factories.Common_UI.RoundedButtonFactory;
import com.mycompany.vibra.Factories.Common_UI.GradientPainter;
import com.mycompany.vibra.Content.Main_Page.Main_Contents.Music_Player.MusicPlayerPanel;
import com.mycompany.vibra.Factories.ThemeFactory.ThemeManager;
import com.mycompany.vibra.musicUtilities.Mp3Utils; // Make sure this import is correct

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * LikedPanel: Displays all songs liked by the current user.
 * This is a merged file combining database logic with UI enhancements.
 */
public class LikedPanel extends JPanel implements ThemeManager.ThemeChangerListener {
    
    // --- Design System ---
    private static final Color GRADIENT_COLOR_1 = new Color(157, 78, 221);
    private static final Color GRADIENT_COLOR_2 = new Color(87, 0, 255);
    private static final Color GRADIENT_COLOR_3 = new Color(157, 78, 221);
    private static final float[] GRADIENT_FRACTIONS = {0.0f, 0.5f, 1.0f};
    private static final Color[] GRADIENT_COLORS = {GRADIENT_COLOR_1, GRADIENT_COLOR_2, GRADIENT_COLOR_3};
    private static final Color CARD_BG = new Color(55, 55, 55);
    private static final Color CARD_HOVER_BG = new Color(70, 70, 70);
    private static final Color TEXT_WHITE = Color.WHITE;
    private static final Color TEXT_GRAY = new Color(169, 169, 169);
    private static final Color TEXT_PURPLE = new Color(168, 85, 247);
    
    private FontFactory fontFactory;
    
    // --- UI Components ---
    private JPanel songListContainer;
    private JPanel emptyStateContent;
    private JPanel darkBackdrop;
    private JLabel header;
    private JLabel subheader;
    private JLabel emptyMessage;
    private JLabel titleHeaderLabel;
    private JLabel artistHeaderLabel;
    private JLabel durationHeaderLabel;
    
    // --- Data & Logic Fields ---
    private List<Track> displaySongs;
    private final LikedSongsDao likedSongsDao;
    private final int currentUserID;
    private MusicPlayerPanel musicPlayerPanel; // For click-to-play and syncing

    /**
     * Main constructor, requires the ID of the logged-in user.
     */
    public LikedPanel(int userID) {
        this.currentUserID = userID;
        this.fontFactory = new DunbarFactory();
        this.displaySongs = new ArrayList<>();
        this.likedSongsDao = new LikedSongsDao();
        
        ThemeManager.getInstance().addThemeChangerListener(this);
        
        setOpaque(false); // We paint our own gradient
        initializeUI();
        applyTheme(ThemeManager.getInstance().isDarkMode());
        updateContentState();
    }

    /**
     * Default constructor for the UI builder or testing.
     * Uses a default user ID of 0.
     */
    public LikedPanel() {
        this(0); 
    }

    /**
     * Allows MainCardPanel to inject the central MusicPlayerPanel.
     * This enables click-to-play and UI syncing.
     */
    public void setMusicPlayerPanel(MusicPlayerPanel musicPlayerPanel) {
        this.musicPlayerPanel = musicPlayerPanel;
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setOpaque(false);
        mainContainer.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Top section
        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setOpaque(false);
        
        header = new JLabel("Liked Songs");
        header.setFont(fontFactory.createFont("dunbartall_bold", 50));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        topSection.add(header);
        
        topSection.add(Box.createVerticalStrut(5));
        
        subheader = new JLabel("Certified bops only.");
        subheader.setFont(fontFactory.createFont("dunbartall_bold", 20));
        subheader.setAlignmentX(Component.LEFT_ALIGNMENT);
        topSection.add(subheader);
        
        topSection.add(Box.createVerticalStrut(30));
        mainContainer.add(topSection, BorderLayout.NORTH);
        
        // Dark backdrop panel
        darkBackdrop = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground()); // Use background color from theme
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2d.dispose();
            }
        };
        darkBackdrop.setOpaque(false);
        darkBackdrop.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel tableHeader = buildTableHeader();
        darkBackdrop.add(tableHeader, BorderLayout.NORTH);
        
        // Content area (swaps between empty and populated list)
        JPanel contentArea = new JPanel(new CardLayout());
        contentArea.setOpaque(false);
        
        emptyStateContent = createEmptyStateContent();

        songListContainer = new JPanel();
        songListContainer.setLayout(new BoxLayout(songListContainer, BoxLayout.Y_AXIS));
        songListContainer.setOpaque(false);
        songListContainer.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JScrollPane scrollPane = new JScrollPane(songListContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        styleScrollBar(scrollPane.getVerticalScrollBar());
        
        contentArea.add(emptyStateContent, "EMPTY");
        contentArea.add(scrollPane, "POPULATED");
        
        darkBackdrop.add(contentArea, BorderLayout.CENTER);
        mainContainer.add(darkBackdrop, BorderLayout.CENTER);
        
        mainContainer.add(buildFooter(), BorderLayout.SOUTH);
        add(mainContainer, BorderLayout.CENTER);
    }
    
    private JPanel buildFooter() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        RoundedButtonFactory clearBtn = new RoundedButtonFactory("Clear All Songs", 10);
        clearBtn.setFont(fontFactory.createFont("dunbartall_bold", 15));
        clearBtn.setForeground(Color.BLACK);
        clearBtn.setBackground(Color.LIGHT_GRAY); 
        clearBtn.setFocusPainted(false);
        
        clearBtn.addActionListener(e -> {
            clearAll(); // This now calls our database-aware method
        });

        footerPanel.add(clearBtn);
        return footerPanel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the radial gradient background
        GradientPainter.paintRadialGradient(g, this, GRADIENT_COLORS, GRADIENT_FRACTIONS);
    }
    
    private JPanel createEmptyStateContent() {
        JPanel emptyPanel = new JPanel(new GridBagLayout());
        emptyPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(50, 0, 30, 0);
        
        ImageIcon bugIcon = new ImageIcon(getClass().getResource("/images/bug.png"));
        JLabel bugPlaceholder = new JLabel(bugIcon);
        bugPlaceholder.setHorizontalAlignment(SwingConstants.CENTER);
        emptyPanel.add(bugPlaceholder, gbc);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        emptyMessage = new JLabel("Crickets..* tap that heart and add some bangers!");
        emptyMessage.setFont(fontFactory.createFont("dunbartall_bold", 28));
        emptyMessage.setHorizontalAlignment(SwingConstants.CENTER);
        emptyPanel.add(emptyMessage, gbc);
        
        return emptyPanel;
    }
    
    private JPanel buildTableHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(100, 100, 100, 100)); // semi-transparent gray
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2d.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(900, 50));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        JPanel headerContent = new JPanel(new GridBagLayout());
        headerContent.setOpaque(false);
        headerContent.setBorder(new EmptyBorder(10, 30, 10, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0;
        gbc.weightx = 0.4;
        gbc.anchor = GridBagConstraints.WEST;
        titleHeaderLabel = new JLabel("Title");
        titleHeaderLabel.setFont(fontFactory.createFont("dunbartall_bold", 20));
        headerContent.add(titleHeaderLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.4;
        artistHeaderLabel = new JLabel("Artist");
        artistHeaderLabel.setFont(fontFactory.createFont("dunbartall_bold", 20));
        headerContent.add(artistHeaderLabel, gbc);
        
        gbc.gridx = 2;
        gbc.weightx = 0.2;
        gbc.anchor = GridBagConstraints.EAST;
        durationHeaderLabel = new JLabel("Duration");
        durationHeaderLabel.setFont(fontFactory.createFont("dunbartall_bold", 20));
        headerContent.add(durationHeaderLabel, gbc);
        
        header.add(headerContent, BorderLayout.CENTER);
        return header;
    }
    
    
    private void styleScrollBar(JScrollBar scrollBar) {
        scrollBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 100, 100);
                this.trackColor = new Color(40, 40, 40);
            }
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createInvisibleButton();
            }
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createInvisibleButton();
            }
            private JButton createInvisibleButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
        });
    }
    
    /**
     * Represents a single clickable song card in the list.
     */
    private class SongCard extends JPanel {
        private Track track;
        private boolean isHovered = false;
        private boolean isHeartHovered = false;

        public SongCard(Track track) {
            this.track = track;
            setLayout(new BorderLayout());
            setOpaque(false);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
            setPreferredSize(new Dimension(900, 70));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            JPanel contentPanel = new JPanel(new GridBagLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Color bgColor = isHovered ? CARD_HOVER_BG : CARD_BG;
                    g2d.setColor(bgColor);
                    g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                    g2d.dispose();
                }
            };
            contentPanel.setOpaque(false);
            contentPanel.setBorder(new EmptyBorder(12, 30, 12, 30));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(0, 0, 0, 15);
            
            // Album art
            gbc.gridx = 0;
            gbc.weightx = 0;
            gbc.anchor = GridBagConstraints.WEST;
            JPanel albumArt = createAlbumArt();
            contentPanel.add(albumArt, gbc);
            
            // Title
            gbc.gridx = 1;
            gbc.weightx = 0.4;
            JLabel titleLabel = new JLabel(track.getTitle());
            titleLabel.setFont(fontFactory.createFont("dunbartall_bold", 15));
            titleLabel.setForeground(TEXT_WHITE);
            contentPanel.add(titleLabel, gbc);

            // Artist
            gbc.gridx = 2;
            gbc.weightx = 0.4;
            JLabel artistLabel = new JLabel(track.getArtist());
            artistLabel.setFont(fontFactory.createFont("dunbartall_bold", 13));
            artistLabel.setForeground(TEXT_GRAY);
            contentPanel.add(artistLabel, gbc);

            // Duration
            gbc.gridx = 3;
            gbc.weightx = 0.15;
            gbc.anchor = GridBagConstraints.EAST;
            JLabel durationLabel = new JLabel(track.getDurationFormatted());
            durationLabel.setFont(fontFactory.createFont("dunbartall_bold", 13));
            durationLabel.setForeground(TEXT_GRAY);
            contentPanel.add(durationLabel, gbc);
            
            // Heart button
            gbc.gridx = 4;
            gbc.weightx = 0;
            gbc.insets = new Insets(0, 0, 0, 0);
            JButton heartBtn = createHeartButton();
            contentPanel.add(heartBtn, gbc);
            
            add(contentPanel, BorderLayout.CENTER);
            
            // Add click-to-play and hover logic
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
                    // Click-to-play feature
                    if (musicPlayerPanel != null) {
                        musicPlayerPanel.loadTrack(track);
                    }
                }
            });
        }
        
        private JPanel createAlbumArt() {
            Image artImage = track.getAlbumArtImage();
            int size = 40;
            JPanel art = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setClip(new Ellipse2D.Float(0, 0, size, size));
                    if (artImage != null) {
                        g2d.drawImage(artImage, 0, 0, size, size, this);
                    } else {
                        // Default placeholder
                        GradientPaint gradient = new GradientPaint(
                                0, 0, new Color(168, 85, 247),
                                size, size, new Color(139, 92, 246)
                        );
                        g2d.setPaint(gradient);
                        g2d.fill(new Ellipse2D.Float(0, 0, size, size));
                        g2d.setColor(TEXT_WHITE);
                        g2d.setFont(new Font("Serif", Font.BOLD, 20));
                        g2d.drawString("♪", 12, 28);
                    }
                    g2d.dispose();
                }
            };
            art.setOpaque(false);
            art.setPreferredSize(new Dimension(size, size));
            return art;
        }
        
        // --- Merged Heart Button (with database logic) ---
        private JButton createHeartButton() {
            JButton btn = new JButton("❤️");
            btn.setFont(new Font("Apple Color Emoji", Font.PLAIN, 22));
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setPreferredSize(new Dimension(40, 40));
            
            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHeartHovered = true;
                    btn.repaint();
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    isHeartHovered = false;
                    btn.repaint();
                }
            });
            
            // --- This is our database logic ---
            btn.addActionListener(e -> {
                try {
                    // 1. Call the database
                    if (likedSongsDao.unlike(currentUserID, track.getId())) {
                        // 2. Tell the player to update its cache/icon
                        if (musicPlayerPanel != null) {
                            musicPlayerPanel.syncLikeStatus(track, false); // false = not liked
                        }

                        // 3. Start the animation (which will remove from UI)
                        animateUnlikeFeedback();
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });
            
            return btn;
        }
        
        // --- Animation for unliking ---
        private void animateUnlikeFeedback() {
            Timer timer = new Timer(20, null);
            final float[] opacity = {1.0f};
            timer.addActionListener(e -> {
                opacity[0] -= 0.1f;
                if (opacity[0] <= 0) {
                    removeSong(track); // Calls our good removeSong method
                    timer.stop();
                } else {
                    repaint(); // Just repaint to trigger alpha (if we add it)
                }
            });
            timer.start();
        }
    }
    
    // === PUBLIC API (from our version) ===
    
    /**
     * Replaces the entire list of songs.
     * Called by MusicPlayerPanel on startup.
     */
    public void setSongs(List<Track> tracks) {
        this.displaySongs = new ArrayList<>(tracks); // Make a copy
        updateContentState();
    }

    /**
     * Adds a single song to the top of the list.
     * Called by MusicPlayerPanel when a song is liked.
     */
    public void addSong(Track track) {
        // Check by ID to prevent duplicates
        if (displaySongs.stream().noneMatch(t -> t.getId() == track.getId())) {
            displaySongs.add(0, track); // Add to the top of the list
            updateContentState();    
        }
    }
    
    /**
     * Removes a single song from the list.
     * Called by MusicPlayerPanel or SongCard when a song is unliked.
     */
    public void removeSong(Track track) {
        // Remove by ID
        displaySongs.removeIf(t -> t.getId() == track.getId());
        updateContentState();
    }
    
    /**
     * Clears all songs from the DB and the UI.
     */
    public void clearAll() {
        try {
            // 1. Call the DAO to clear the database
            likedSongsDao.unlikeAll(currentUserID);
            
            // 2. Clear the local UI list
            displaySongs.clear();
            updateContentState();
            
            // 3. Tell MusicPlayerPanel to clear its cache
            if (musicPlayerPanel != null) {
                musicPlayerPanel.clearLikedCache();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Update content inside the dark backdrop
    private void updateContentState() {
        // Find the contentArea panel (which has the CardLayout)
        Component contentArea = null;
        for (Component comp : darkBackdrop.getComponents()) {
            // Correctly check if comp is a Container before calling getLayout()
            if (comp instanceof Container && ((Container) comp).getLayout() instanceof CardLayout) {
                contentArea = comp;
                break;
            }
        }

        if (contentArea == null) return; // Should not happen
        
        CardLayout cardLayout = (CardLayout) ((JPanel) contentArea).getLayout();
        
        if (displaySongs.isEmpty()) {
            cardLayout.show((JPanel) contentArea, "EMPTY");
        } else {
            refreshSongList();
            cardLayout.show((JPanel) contentArea, "POPULATED");
        }
        revalidate();
        repaint();
    }
    
    /**
     * Refresh the song list display
     */
    private void refreshSongList() {
        songListContainer.removeAll();
        for (Track track : displaySongs) {
            SongCard card = new SongCard(track);
            songListContainer.add(card);
            songListContainer.add(Box.createVerticalStrut(6));
        }
        songListContainer.revalidate();
        songListContainer.repaint();
    }

    // --- Themeing Methods ---
    
    private void applyTheme(boolean isDark) {
        Color foreground = ThemeManager.getInstance().getForegroundColor();
        Color accent = ThemeManager.getInstance().getAccentColor();

        darkBackdrop.setBackground(ThemeManager.getInstance().getContainerColor());

        header.setForeground(foreground);
        subheader.setForeground(foreground);
        emptyMessage.setForeground(isDark ? TEXT_GRAY : new Color(100, 100, 100));

        titleHeaderLabel.setForeground(accent);
        artistHeaderLabel.setForeground(accent);
        durationHeaderLabel.setForeground(accent);
    }

    @Override
    public void onThemeChanged(boolean isDarkMode) {
        applyTheme(isDarkMode);
    }
}