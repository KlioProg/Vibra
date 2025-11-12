package com.mycompany.vibra.Content.PopUpChoices;

import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.DunbarFactory;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.FontFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundedPanelFactory;
import com.mycompany.vibra.Factories.ThemeFactory.ThemeManager;
import com.mycompany.vibra.model.Playlist; // Assuming your Playlist model is here
import com.mycompany.vibra.Factories.Common_UI.CustomScrollBarUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class PopUp_SelectionList extends RoundedPanelFactory implements ThemeManager.ThemeChangerListener {

    private final FontFactory fontFactory = new DunbarFactory();
    private Playlist selectedPlaylist = null;
    private final String titleText;
    private final List<Playlist> playlists;

    private JLabel titleLabel;
    private JPanel listContainer;
    private JScrollPane scrollPane;

    // --- Colors for list items (NOW includes accentColor) ---
    private Color itemBaseColor;
    private Color itemHoverColor;
    private Color itemPressColor;
    private Color itemTextColor;
    private Color accentColor; // <-- NEW FIELD

    public PopUp_SelectionList(String title, List<Playlist> playlists) {
        super(20, ThemeManager.getInstance().getContainerColor(), null, 0);
        this.titleText = title;
        this.playlists = playlists;

        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(25, 25, 25, 25));
        setOpaque(false);

        initUI();

        ThemeManager.getInstance().addThemeChangerListener(this);
        applyTheme();
    }

    private void initUI() {
        // --- 1. Title ---
        titleLabel = new JLabel(titleText);
        titleLabel.setFont(fontFactory.createFont("dunbartall_bold", 22));
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // --- 2. Scrollable List Container ---
        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setOpaque(false);

        // --- 3. Populate List with Selectable Items ---
        // applyTheme() is called in constructor, so colors are set before this runs
        for (Playlist playlist : playlists) {
            JButton playlistButton = createPlaylistItem(playlist);
            listContainer.add(playlistButton);
            listContainer.add(Box.createVerticalStrut(5));
        }

        // --- 4. CONFIGURE SCROLLPANE ---
        scrollPane = new JScrollPane(listContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        applyCustomScrollBar();

        scrollPane.setPreferredSize(new Dimension(300, 200));
        scrollPane.setMaximumSize(new Dimension(300, 200));

        add(scrollPane, BorderLayout.CENTER);
    }

    private void applyCustomScrollBar() {
        if (scrollPane == null) return;

        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUI(new CustomScrollBarUI());
        verticalScrollBar.setOpaque(false);
        verticalScrollBar.setPreferredSize(new Dimension(12, 0));
    }

    // --- ⬇️ MODIFIED ⬇️ ---
    /**
     * Creates a custom-styled JButton for a playlist item.
     * Now with accent-colored hover effects.
     */
    private JButton createPlaylistItem(Playlist playlist) {
        JButton button = new JButton(playlist.getText());
        button.setFont(fontFactory.createFont("dunbartall_book", 16));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(8, 12, 8, 12));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        // --- NEW ---
        // Set the initial text color (based on theme)
        button.setForeground(itemTextColor);

        // Action: Set the selected item and close the dialog
        button.addActionListener(e -> {
            this.selectedPlaylist = playlist;
            closeDialog();
        });

        // --- NEW MOUSE LISTENERS ---
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                button.setOpaque(true);
                button.setBackground(itemPressColor);
                button.setForeground(accentColor); // Keep text purple
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                button.setOpaque(true);
                button.setBackground(itemHoverColor);
                button.setForeground(accentColor); // Keep text purple
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setOpaque(true);
                button.setBackground(itemHoverColor); // Faint accent bg
                button.setForeground(accentColor);    // Full accent text
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setOpaque(false); // Return to transparent
                button.setBackground(itemBaseColor);
                button.setForeground(itemTextColor);  // Revert text color
            }
        });

        return button;
    }

    private void closeDialog() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w != null) {
            w.dispose();
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        ThemeManager.getInstance().removeThemeChangerListener(this);
    }

    // --- ⬇️ MODIFIED ⬇️ ---
    /**
     * Applies theme colors to the panel and defines
     * the new accent-based hover/press colors.
     */
    @Override
    public void onThemeChanged(boolean isDarkMode) {
        applyTheme();
        revalidate();
        repaint();
    }

    private void applyTheme() {
        ThemeManager tm = ThemeManager.getInstance();
        setBackgroundColor(tm.getContainerColor());
        titleLabel.setForeground(tm.getForegroundColor());

        // --- NEW ACCENT-BASED COLOR DEFINITIONS ---
        itemTextColor = tm.getForegroundColor();
        accentColor = tm.getAccentColor(); // Store the accent color (e.g., #9D4EDD)

        // Base color is always transparent
        itemBaseColor = tm.getContainerColor();

        // Use a semi-transparent version of the accent color for hover
        itemHoverColor = new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 40); // ~15% opacity
        // Use a slightly more opaque version for press
        itemPressColor = new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 70); // ~27% opacity
        // --- END OF NEW COLORS ---

        // Apply theme to all child buttons in the list
        if (listContainer != null) {
            for (Component comp : listContainer.getComponents()) {
                if (comp instanceof JButton) {
                    JButton button = (JButton) comp;
                    // Reset background to transparent (mouseExit state)
                    button.setOpaque(false);
                    button.setBackground(itemBaseColor);
                    // Set text color to default
                    button.setForeground(itemTextColor);
                }
            }
        }

        // Re-apply the scrollbar UI so it picks up the new theme colors.
        applyCustomScrollBar();
    }

    public Playlist getSelectedPlaylist() {
        return selectedPlaylist;
    }

    public static Playlist showPlaylistSelection(Component parent, String title, List<Playlist> playlists) {
        // (This static method is unchanged)

        PopUp_SelectionList selectionPanel = new PopUp_SelectionList(title, playlists);
        Window parentWindow = SwingUtilities.getWindowAncestor(parent);

        JDialog dialog;
        if (parentWindow instanceof Frame) {
            dialog = new JDialog((Frame) parentWindow, true);
        } else {
            dialog = new JDialog((Dialog) parentWindow, true);
        }

        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));
        dialog.setContentPane(selectionPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);

        return selectionPanel.getSelectedPlaylist();
    }
}