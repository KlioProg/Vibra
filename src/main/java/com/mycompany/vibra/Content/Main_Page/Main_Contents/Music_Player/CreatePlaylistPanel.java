package com.mycompany.vibra.Content.Main_Page.Main_Contents.Music_Player; // Or your preferred package

import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.DunbarFactory;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.FontFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.ButtonIconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.DarkModeIconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.IconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.LightModeIconFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundedButtonFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundedPanelFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundedTextFieldFactory;
import com.mycompany.vibra.Factories.ThemeFactory.ThemeManager;

// --- IMPORTS FOR CUSTOM POPUPS ---
import com.mycompany.vibra.Content.PopUpChoices.PopUp_Alert;
import com.mycompany.vibra.Content.PopUpChoices.PopUp_YesNo;
import javax.swing.JDialog;
import java.awt.Dialog;
import java.awt.Frame;
// --- END IMPORTS ---

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
    private RoundedPanelFactory roundedCoverPanel;

    private ImageIcon playlistCover;
    private IconFactory icons;
    private IconFactory themeIcons;
    FontFactory fontFactory = new DunbarFactory();

    // --- Logic Fields ---
    private RoundedButtonFactory deleteButton;
    private boolean playlistCreated = false;
    private boolean playlistDeleted = false;
    private boolean isEditMode = false;

    // --- Constructor 1: For "Create" ---
    public CreatePlaylistPanel() {
        super(20, new Color(0x18, 0x18, 0x18), null, 0, 330, 460); // Call super constructor
        this.fontFactory = new DunbarFactory();
        this.themeIcons = ThemeManager.getInstance().isDarkMode() ? new DarkModeIconFactory() : new LightModeIconFactory();
        this.icons = new ButtonIconFactory();
        this.playlistCover = icons.createIcon("playlist_default");
        this.isEditMode = false; // This is "Create" mode
        initUI();
    }

    // --- Constructor 2: For "Edit" ---
    public CreatePlaylistPanel(String initialName, String initialBio, ImageIcon initialCover) {
        super(20, new Color(0x18, 0x18, 0x18), null, 0, 330, 460); // Call super constructor
        this.fontFactory = new DunbarFactory();
        this.themeIcons = ThemeManager.getInstance().isDarkMode() ? new DarkModeIconFactory() : new LightModeIconFactory();
        this.icons = new ButtonIconFactory();
        this.playlistCover = (initialCover != null) ? initialCover : icons.createIcon("playlist_default");
        this.isEditMode = true; // This is "Edit" mode
        initUI();
        // Set existing text
        nameField.setText(initialName);
        bioField.setText(initialBio);
    }


    /**
     * Initializes and lays out all UI components for the panel.
     */
    private void initUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20)); // Padding

        // --- Close Button ---
        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        closePanel.setOpaque(false);
        closeButton = new RoundedButtonFactory("X", 30);
        closeButton.setPreferredSize(new Dimension(40, 40));
        closeButton.setBackground(new Color(0x33, 0x33, 0x33));
        closeButton.setForeground(Color.WHITE);
        closeButton.addActionListener(e -> closeDialog());
        closePanel.add(closeButton);
        closePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(closePanel);

        add(Box.createVerticalStrut(4));

        // --- Cover Art ---
        Image scaledImg = playlistCover.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        coverArtLabel = new JLabel(new ImageIcon(scaledImg));
        roundedCoverPanel = new RoundedPanelFactory(15, Color.BLACK, null, 0, 150, 150);
        roundedCoverPanel.setLayout(new BorderLayout());
        roundedCoverPanel.add(coverArtLabel, BorderLayout.CENTER);
        roundedCoverPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(roundedCoverPanel);

        // --- "Change Cover" Button ---
        add(Box.createVerticalStrut(15));
        RoundedButtonFactory changeCoverBtn = createChooseImageButton();
        changeCoverBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(changeCoverBtn);

        // --- Text Fields ---
        add(Box.createVerticalStrut(16));
        nameField = playlistNameTextField();
        add(nameField);
        add(Box.createVerticalStrut(12));
        bioField = bioPlaylistTextField();
        add(bioField);

        // --- Button Panel (Pushed to bottom) ---
        add(Box.createVerticalGlue());
        add(Box.createVerticalStrut(12));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        // Add Save Button (always)
        saveButton = saveButton();
        buttonPanel.add(saveButton);

        // Add Delete Button (only in edit mode)
        if (isEditMode) {
            deleteButton = createDeleteButton();
            buttonPanel.add(deleteButton);
        }
        add(buttonPanel);
    }

    // --- Factory Methods for Components ---

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

        button.addActionListener(e -> {
            if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
                nameField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                // --- FIXED: Use PopUp_Alert ---
                PopUp_Alert.showAlert(this, "Error", "Playlist name is required.");
                nameField.setBorder(null); // Reset border
                return;
            }
            this.playlistCreated = true;
            this.playlistDeleted = false; // Ensure delete flag is off
            closeDialog();
        });

        // Add the hover/press effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { button.setBackground(new Color(0x7B2CBF)); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { button.setBackground(new Color(0x9D4EDD)); button.setForeground(new Color(0xF9F6EE)); }
            @Override public void mousePressed(java.awt.event.MouseEvent e) { button.setBackground(new Color(0x5A189A)); button.setForeground(new Color(0x9D4EDD)); }
            @Override public void mouseReleased(java.awt.event.MouseEvent e) { button.setBackground(new Color(0x7B2CBF)); button.setForeground(new Color(0xF9F6EE)); }
        });
        return button;
    }

    /**
     * Creates a red "Delete" button with a confirmation dialog.
     */
    private RoundedButtonFactory createDeleteButton() {
        RoundedButtonFactory button = new RoundedButtonFactory("Delete", 30);

        // Red "Destructive" Style
        Color baseColor = new Color(0xC0392b); // Red
        Color hoverColor = new Color(0xE74C3C); // Brighter Red
        Color pressColor = new Color(0x962D22); // Darker Red

        button.setBackground(baseColor);
        button.setForeground(Color.WHITE);

        Dimension btnSize = new Dimension(100, 44); // A bit smaller than save
        button.setPreferredSize(btnSize);
        button.setMaximumSize(btnSize);
        button.setMinimumSize(btnSize);

        button.addActionListener(e -> {
            // --- FIXED: Use custom confirmation ---
            String title = "Confirm Deletion";
            String message = "Are you sure you want to delete this playlist?";
            boolean confirmed = showConfirmDialog(this, title, message); // Call helper

            if (confirmed) {
                this.playlistDeleted = true;  // Set the flag
                this.playlistCreated = false; // Unset the other flag
                closeDialog();                // Close the popup
            }
            // If not confirmed, do nothing.
        });

        // --- Hover/Press Effects ---
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { button.setBackground(hoverColor); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { button.setBackground(baseColor); button.setForeground(Color.WHITE); }
            @Override public void mousePressed(java.awt.event.MouseEvent e) { button.setBackground(pressColor); }
            @Override public void mouseReleased(java.awt.event.MouseEvent e) { button.setBackground(hoverColor); button.setForeground(Color.WHITE); }
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
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { button.setBackground(new Color(0x7B2CBF)); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { button.setBackground(new Color(0x9D4EDD)); button.setForeground(new Color(0xF9F6EE)); }
            @Override public void mousePressed(java.awt.event.MouseEvent e) { button.setBackground(new Color(0x5A189A)); button.setForeground(new Color(0x9D4EDD)); }
            @Override public void mouseReleased(java.awt.event.MouseEvent e) { button.setBackground(new Color(0x7B2CBF)); button.setForeground(new Color(0xF9F6EE)); }
        });
        return button;
    }

    // --- Helper Methods ---

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

    /**
     * Shows a custom modal "Yes/No" dialog using the PopUp_YesNo panel.
     */
    private boolean showConfirmDialog(Component parent, String title, String message) {
        PopUp_YesNo confirmPanel = new PopUp_YesNo(title, message);
        Window parentWindow = SwingUtilities.getWindowAncestor(parent);
        JDialog dialog;
        if (parentWindow instanceof Frame) {
            dialog = new JDialog((Frame) parentWindow, true);
        } else {
            dialog = new JDialog((Dialog) parentWindow, true);
        }
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));
        dialog.setContentPane(confirmPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        return confirmPanel.isConfirmed();
    }


    // --- Public Getters ---
    public boolean isPlaylistCreated() { return this.playlistCreated; }
    public String getPlaylistName() { return nameField.getText(); }
    public String getPlaylistBio() { return bioField.getText(); }
    public ImageIcon getPlaylistCover() { return this.playlistCover; }
    public boolean isPlaylistDeleted() { return this.playlistDeleted; } // The getter
}