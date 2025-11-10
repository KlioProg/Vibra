package com.mycompany.vibra.Content.Main_Page.Main_Contents.Like_Panel;

import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.DunbarFactory;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.FontFactory;
import com.mycompany.vibra.Factories.Common_UI.GradientPainter;
import com.mycompany.vibra.Factories.Common_UI.RoundedBackdropFactory;
import com.mycompany.vibra.Content.Main_Page.Main_Contents.Music_Player.MusicPlayerPanel;
import com.mycompany.vibra.Factories.Common_UI.RoundedButtonFactory;
import com.mycompany.vibra.Factories.ThemeFactory.ThemeManager;
import com.mycompany.vibra.musicUtilities.Mp3Utils;
import com.mycompany.vibra.musicUtilities.Track;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author robbiebelen
 */
public class LikedPanel extends JPanel implements ThemeManager.ThemeChangerListener {
    
    //DESIGN SYSTEM 
    private static final Color GRADIENT_COLOR_1 = new Color(157, 78, 221);  // #9D4EDD
    private static final Color GRADIENT_COLOR_2 = new Color(87, 0, 255);    // #5700FF
    private static final Color GRADIENT_COLOR_3 = new Color(157, 78, 221);  // #9D4EDD
    // Gradient properties
    private static final float[] GRADIENT_FRACTIONS = {0.0f, 0.5f, 1.0f};
    private static final Color[] GRADIENT_COLORS = {GRADIENT_COLOR_1, GRADIENT_COLOR_2, GRADIENT_COLOR_3};

    private static final Color HEADER_BG = new Color(64, 64, 64);        // #404040
    private static final Color CARD_BG = new Color(55, 55, 55);          // Neutral-700
    private static final Color CARD_HOVER_BG = new Color(70, 70, 70);
    private static final Color TEXT_WHITE = Color.WHITE;
    private static final Color TEXT_GRAY = new Color(169, 169, 169);    // Light gray
    private static final Color TEXT_PURPLE = new Color(168, 85, 247);      // #A855F7
    private static final Color BORDER_SUBTLE = new Color(255, 255, 255, 25);
    
    private FontFactory fontFactory;
    
    // UI Components
    private JPanel songListContainer;
    private JPanel emptyStateContent;
    private RoundedBackdropFactory darkBackdrop;
    private JLabel header;
    private JLabel subheader;
    private JLabel emptyMessage;
    private JLabel titleHeaderLabel;
    private JLabel artistHeaderLabel;
    private JLabel durationHeaderLabel;
    private List<Track> displaySongs;
    private MusicPlayerPanel musicPlayerPanel;
    
    public LikedPanel() {
        this.fontFactory = new DunbarFactory();
        this.displaySongs = new ArrayList<>();
        ThemeManager.getInstance().addThemeChangerListener(this);

        initializeUI();
        applyTheme(ThemeManager.getInstance().isDarkMode());
        updateContentState();
    }

    /**
     * This method allows the main application frame (MainCardPanel) to give this panel
     * a reference to the central music player. This is a clean way to enable communication
     * without creating tight coupling.
     * @param musicPlayerPanel The application's single MusicPlayerPanel instance.
     */
    public void setMusicPlayerPanel(MusicPlayerPanel musicPlayerPanel) {
        this.musicPlayerPanel = musicPlayerPanel;
    }
    
    private void initializeUI() {
//        setPreferredSize(new Dimension(1039, 1093));
        setLayout(new BorderLayout());
        
        // Main container
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setOpaque(false); // Make transparent
        mainContainer.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Top section: Header 
        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setOpaque(false);
        
        // Header: "Liked Songs"
        header = new JLabel("Liked Songs");
        // USE FONTLOADERFACTORY
        header.setFont(fontFactory.createFont("dunbartall_bold", 50));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        topSection.add(header);
        
        topSection.add(Box.createVerticalStrut(5));
        
        // Subheader: "Certified bops only."
        subheader = new JLabel("Certified bops only.");
        // USE FONTLOADERFACTORY
        subheader.setFont(fontFactory.createFont("dunbartall_bold", 20));
        subheader.setAlignmentX(Component.LEFT_ALIGNMENT);
        topSection.add(subheader);
        
        topSection.add(Box.createVerticalStrut(30));
        
        mainContainer.add(topSection, BorderLayout.NORTH);
        
        // Dark backdrop panel 
        darkBackdrop = new RoundedBackdropFactory(new BorderLayout(), 20);
        darkBackdrop.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Table header
        JPanel tableHeader = buildTableHeader();
        darkBackdrop.add(tableHeader, BorderLayout.NORTH);
        
        // Content area (switches between empty state and song list)
        JPanel contentArea = new JPanel(new CardLayout());
        contentArea.setOpaque(false);
        
        //bug vector
        emptyStateContent = createEmptyStateContent();

        
        // Song list container
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
        
        //footer placeholder button container
        mainContainer.add(buildFooter(), BorderLayout.SOUTH);
        // Add main container to this panel
        add(mainContainer, BorderLayout.CENTER);
    }
    
    private JPanel buildFooter() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(20, 0, 0, 0)); // Space above buttons

        // Button 1: Add 5 Songs (Using RoundedButtonFactory)
        // RoundedButtonFactory addBtn = new RoundedButtonFactory("Add 5 Songs", 10);
        // addBtn.setFont(fontFactory.createFont("dunbartall_bold", 15)); // Use factory
        // addBtn.setForeground(Color.WHITE);
        // addBtn.setBackground(new Color(0x9D4EDD)); // Accent Color
        // addBtn.setFocusPainted(false);
        // addBtn.addActionListener(e -> {
        //     MOCK_SONGS.forEach(this::addSong);
        // });

        // Button 2: Clear Songs (Using RoundedButtonFactory)
        RoundedButtonFactory clearBtn = new RoundedButtonFactory("Clear All Songs", 10);
        clearBtn.setFont(fontFactory.createFont("dunbartall_bold", 15)); // Use factory
        clearBtn.setForeground(Color.BLACK);
        clearBtn.setBackground(Color.LIGHT_GRAY); 
        clearBtn.setFocusPainted(false);
        clearBtn.addActionListener(e -> {
            clearAll();
        });

        // footerPanel.add(addBtn);
        footerPanel.add(clearBtn);

        return footerPanel;
    }
    // THIS IS THE GRADIENT CODE, KEPT SAFELY INSIDE LIKEDPANEL
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Use the centralized GradientPainter utility to draw the background
        GradientPainter.paintRadialGradient(g, this, GRADIENT_COLORS, GRADIENT_FRACTIONS);
    }
    
    /**
     * Create empty state content (placeholder for bug vector) shown when no songs
     */
    private JPanel createEmptyStateContent() {
        JPanel emptyPanel = new JPanel(new GridBagLayout());
        emptyPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(50, 0, 30, 0);
        
        // Bug vector as ImageIcon
        ImageIcon bugIcon = new ImageIcon(getClass().getResource("/images/bug.png"));
        JLabel bugPlaceholder = new JLabel(bugIcon);
        bugPlaceholder.setHorizontalAlignment(SwingConstants.CENTER);
        emptyPanel.add(bugPlaceholder, gbc);
        
        // Empty message - Dunbar SemiBold
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        emptyMessage = new JLabel("Crickets..* tap that heart and add some bangers!");
        // USE FONTLOADERFACTORY
        emptyMessage.setFont(fontFactory.createFont("dunbartall_bold", 28));
        emptyMessage.setHorizontalAlignment(SwingConstants.CENTER);
        emptyPanel.add(emptyMessage, gbc);
        
        return emptyPanel;
    }
    
    /**
     * Build table header matching screenshot
     */
    private JPanel buildTableHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gray background
                g2d.setColor(new Color(100, 100, 100, 100));
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
        
        // Title - dunbar SemiBold
        gbc.gridx = 0;
        gbc.weightx = 0.4;
        gbc.anchor = GridBagConstraints.WEST;
        titleHeaderLabel = new JLabel("Title");
        // USE FONTLOADERFACTORY
        titleHeaderLabel.setFont(fontFactory.createFont("dunbartall_bold", 20));
        headerContent.add(titleHeaderLabel, gbc);
        
        // Artist - dunbar SemiBold
        gbc.gridx = 1;
        gbc.weightx = 0.4;
        artistHeaderLabel = new JLabel("Artist");
        // USE FONTLOADERFACTORY
        artistHeaderLabel.setFont(fontFactory.createFont("dunbartall_bold", 20));
        headerContent.add(artistHeaderLabel, gbc);
        
        // Duration - dunbar SemiBold
        gbc.gridx = 2;
        gbc.weightx = 0.2;
        gbc.anchor = GridBagConstraints.EAST;
        durationHeaderLabel = new JLabel("Duration");
        // USE FONTLOADERFACTORY
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
     * Individual song card component
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
            
            // Song title - Dunbar SemiBold
            gbc.gridx = 1;
            gbc.weightx = 0.4;
            JLabel titleLabel = new JLabel(track.getTitle());
            // USE FONTLOADERFACTORY
            titleLabel.setFont(fontFactory.createFont("dunbartall_bold", 15));
            titleLabel.setForeground(TEXT_WHITE);
            contentPanel.add(titleLabel, gbc);

            // Artist name - Dunbar SemiBold
            gbc.gridx = 2;
            gbc.weightx = 0.4;
            JLabel artistLabel = new JLabel(track.getArtist());
            // USE FONTLOADERFACTORY
            artistLabel.setFont(fontFactory.createFont("dunbartall_bold", 13));
            artistLabel.setForeground(TEXT_GRAY);
            contentPanel.add(artistLabel, gbc);

            // Duration - Dunbar SemiBold
            gbc.gridx = 3;
            gbc.weightx = 0.15;
            gbc.anchor = GridBagConstraints.EAST;
            JLabel durationLabel = new JLabel(Mp3Utils.formatMinutes(track.getDuration()));
            // USE FONTLOADERFACTORY
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
                    // When a card is clicked, we check if we have a reference
                    // to the music player and, if so, tell it to load our track.
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

                    // Clip to a circle
                    g2d.setClip(new Ellipse2D.Float(0, 0, size, size));

                    if (artImage != null) {
                        // Draw the album art, scaled to fill the circle
                        g2d.drawImage(artImage, 0, 0, size, size, this);
                    } else {
                        // Draw the default placeholder (purple circle with note)
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
        
        private JButton createHeartButton() {
            JButton btn = new JButton("❤️") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                                         RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    
                    if (isHeartHovered) {
                        g2d.scale(1.2, 1.2);
                    }
                    super.paintComponent(g2d);
                    g2d.dispose();
                }
            };
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
            
            btn.addActionListener(e -> animateUnlikeFeedback());
            
            return btn;
        }
        
        private void animateUnlikeFeedback() {
            Timer timer = new Timer(20, null);
            final float[] opacity = {1.0f};
            timer.addActionListener(e -> {
                opacity[0] -= 0.1f;
                if (opacity[0] <= 0) {
                    removeSong(track);
                    timer.stop();
                } else {
                    repaint();
                }
            });
            timer.start();
        }
    }
    
    // === PUBLIC API ===
    
    public void addSong(Track track) {
        if (!displaySongs.contains(track)) {
            displaySongs.add(track);
            updateContentState();
        }
    }
    
    public void removeSong(Track track) {
        displaySongs.remove(track);
        updateContentState();
    }

    public void clearAll() {
        displaySongs.clear();
        updateContentState();
    }
    
    // Update content inside the dark backdrop (switch between empty state and song list)
    private void updateContentState() {
        // Get the CardLayout from the content area
        Component[] components = darkBackdrop.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                LayoutManager layout = ((JPanel) comp).getLayout();
                if (layout instanceof CardLayout) {
                    CardLayout cardLayout = (CardLayout) layout;
                    
                    if (displaySongs.isEmpty()) {
                        // Show empty state
                        cardLayout.show((JPanel) comp, "EMPTY");
                    } else {
                        // Populate song list and show it
                        refreshSongList();
                        cardLayout.show((JPanel) comp, "POPULATED");
                    }
                    break;
                }
            }
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

    // === DEMO ===

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Liked Songs Panel");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Create panel
            LikedPanel panel = new LikedPanel();

            frame.add(panel, BorderLayout.CENTER);
            frame.setSize(1100, 1150);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

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
