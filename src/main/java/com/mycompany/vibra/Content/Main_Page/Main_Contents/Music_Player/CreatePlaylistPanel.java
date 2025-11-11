package com.mycompany.vibra.Content.Main_Page.Main_Contents.Music_Player; // Or your preferred package

import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.DunbarFactory;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.FontFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.ButtonIconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.DarkModeIconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.IconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.LightModeIconFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundedButtonFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundedPanelFactory; // Import Panel factory
import com.mycompany.vibra.Factories.Common_UI.RoundedTextFieldFactory;
import com.mycompany.vibra.Factories.ThemeFactory.ThemeManager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * A modern, rounded panel for creating OR editing a playlist.
 * This class EXTENDS RoundedPanelFactory to get its rounded-corner look.
 */
public class CreatePlaylistPanel extends RoundedPanelFactory {

    // --- Fields ---
    private RoundedTextFieldFactory nameField;
    private RoundedTextFieldFactory bioField;
    private RoundedButtonFactory saveButton;
    private RoundedButtonFactory closeButton;
    private JLabel coverArtLabel;
    private RoundedPanelFactory roundedCoverPanel; // For the cover art itself

    private ImageIcon playlistCover;
    private boolean playlistCreated = false;
    private IconFactory icons;
    private IconFactory themeIcons;
    FontFactory fontFactory = new DunbarFactory();

    // --- Constructor 1: For "Create" ---
    public CreatePlaylistPanel() {
        // Call the super constructor to make THIS panel rounded
        super(
                20, // cornerRadius
                new Color(0x18, 0x18, 0x18), // backgroundColor
                null, // borderColor
                0,    // borderThickness
                330,  // preferredWidth
                460   // preferredHeight
        );

        // Initialize factories
        this.fontFactory = new DunbarFactory();
        this.themeIcons = ThemeManager.getInstance().isDarkMode() ? new DarkModeIconFactory() : new LightModeIconFactory();
        this.icons = new ButtonIconFactory();

        // Set default cover for a new playlist
        this.playlistCover = icons.createIcon("playlist_default");

        // Build the UI
        initUI();
    }

    // --- ✅ Constructor 2: For "Edit" ---
    public CreatePlaylistPanel(String initialName, String initialBio, ImageIcon initialCover) {
        // Call the super constructor (same as above)
        super(
                20, // cornerRadius
                new Color(0x18, 0x18, 0x18), // backgroundColor
                null, // borderColor
                0,    // borderThickness
                330,  // preferredWidth
                460   // preferredHeight
        );

        // Initialize factories
        this.fontFactory = new DunbarFactory();
        this.themeIcons = ThemeManager.getInstance().isDarkMode() ? new DarkModeIconFactory() : new LightModeIconFactory();
        this.icons = new ButtonIconFactory();

        // Set the EXISTING cover
        this.playlistCover = (initialCover != null) ? initialCover : icons.createIcon("playlist_default");

        // Build the UI
        initUI();

        // ✅ Set the initial text for the fields
        nameField.setText(initialName);
        bioField.setText(initialBio);
    }


    /**
     * Initializes and lays out all UI components for the panel.
     * This method is now called by BOTH constructors.
     */
    private void initUI() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20)); // Padding

        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        closePanel.setOpaque(false);
        closeButton = new RoundedButtonFactory("X", 30); // 30-radius makes it a circle
        closeButton.setPreferredSize(new Dimension(40, 40)); // Force square shape
        closeButton.setBackground(new Color(0x33, 0x33, 0x33));
        closeButton.setForeground(Color.WHITE);
        closeButton.addActionListener(e -> closeDialog());
        closePanel.add(closeButton);
        closePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(closePanel);

        add(Box.createVerticalStrut(4));

        // ✅ This 'playlistCover' field is now set by the constructor
        //    before initUI() is even called.
        Image scaledImg = playlistCover.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        coverArtLabel = new JLabel(new ImageIcon(scaledImg));

        roundedCoverPanel = new RoundedPanelFactory(15, Color.BLACK, null, 0, 150, 150);
        roundedCoverPanel.setLayout(new BorderLayout());
        roundedCoverPanel.add(coverArtLabel, BorderLayout.CENTER);
        roundedCoverPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(roundedCoverPanel);

        // --- 4. "Change Cover" Button ---
        add(Box.createVerticalStrut(15));
        RoundedButtonFactory changeCoverBtn = createChooseImageButton();
        changeCoverBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(changeCoverBtn);

        // --- 5. Text Fields ---
        add(Box.createVerticalStrut(16));

        nameField = playlistNameTextField(); // Use your factory
        add(nameField);

        add(Box.createVerticalStrut(12));

        bioField = bioPlaylistTextField(); // Use your factory
        add(bioField);

        // --- 6. Save Button (Pushed to bottom) ---
        add(Box.createVerticalGlue()); // This pushes the save button down!
        add(Box.createVerticalStrut(12));

        saveButton = saveButton(); // Use your factory
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(saveButton);
    }

    // --- Factory Methods for Components ---
    // (No changes needed in these methods)

    private RoundedTextFieldFactory createStyledTextField(String placeholder){
        RoundedTextFieldFactory textField = new RoundedTextFieldFactory(40);
        textField.setPreferredSize(new Dimension(350, 44));
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        textField.setAlignmentX(Component.CENTER_ALIGNMENT);
        textField.setBackground(new Color(0xF9F6EE));
        textField.setForeground(new Color(0x100D0D));
        textField.setFont(fontFactory.createFont("dunbartall_book", 16));
        textField.setPlaceholder(placeholder);
        return textField;
    }

    private RoundedTextFieldFactory playlistNameTextField(){
        return createStyledTextField("Playlist name");
    }

    private RoundedTextFieldFactory bioPlaylistTextField(){
        return createStyledTextField("Playlist Bio");
    }

    private RoundedButtonFactory saveButton() {
        RoundedButtonFactory button = new RoundedButtonFactory("Save Playlist!", 30);
        button.setBackground(new Color(0x9D4EDD));
        button.setForeground(new Color(0xF9F6EE));

        Dimension saveButtonSize = new Dimension(150, 44);
        button.setPreferredSize(saveButtonSize);
        button.setMaximumSize(saveButtonSize);
        button.setMinimumSize(saveButtonSize);

        // Add the save logic
        button.addActionListener(e -> {
            if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
                // ✅ Added a simple visual warning
                nameField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                JOptionPane.showMessageDialog(this, "Playlist name is required.", "Error", JOptionPane.ERROR_MESSAGE);
                nameField.setBorder(null); // Reset border
                return;
            }
            this.playlistCreated = true;
            closeDialog();
        });

        // Add the hover/press effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(0x7B2CBF));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(0x9D4EDD));
                button.setForeground(new Color(0xF9F6EE));
            }
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(0x5A189A));
                button.setForeground(new Color(0x9D4EDD));
            }
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(0x7B2CBF));
                button.setForeground(new Color(0xF9F6EE));
            }
        });
        return button;
    }

    private RoundedButtonFactory createChooseImageButton() {
        RoundedButtonFactory button = new RoundedButtonFactory("Change Cover", 30);
        button.setBackground(new Color(0x9D4EDD));
        button.setForeground(new Color(0xF9F6EE));

        Dimension btnSize = new Dimension(150, 44);
        button.setPreferredSize(btnSize);
        button.setMaximumSize(btnSize);
        button.setMinimumSize(btnSize);

        button.addActionListener(e -> openImageChooser());

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(0x7B2CBF));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(0x9D4EDD));
                button.setForeground(new Color(0xF9F6EE));
            }
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(0x5A189A));
                button.setForeground(new Color(0x9D4EDD));
            }
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(0x7B2CBF));
                button.setForeground(new Color(0xF9F6EE));
            }
        });
        return button;
    }

    // --- Helper Methods ---
    // (No changes here)

    private void openImageChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Playlist Cover");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Images (png, jpg, jpeg)", "png", "jpg", "jpeg"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            playlistCover = new ImageIcon(selectedFile.getAbsolutePath());

            // Update the label, scaled to 150x150
            Image scaledImg = playlistCover.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            coverArtLabel.setIcon(new ImageIcon(scaledImg));
        }
    }

    private void closeDialog() {
        Window a = SwingUtilities.getWindowAncestor(this);
        if (a != null) {
            a.dispose();
        }
    }

    // --- Public Getters ---
    // (No changes here)
    public boolean isPlaylistCreated() { return this.playlistCreated; }
    public String getPlaylistName() { return nameField.getText(); }
    public String getPlaylistBio() { return bioField.getText(); }
    public ImageIcon getPlaylistCover() { return this.playlistCover; }
}