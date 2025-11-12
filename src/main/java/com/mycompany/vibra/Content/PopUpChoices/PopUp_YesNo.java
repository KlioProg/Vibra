package com.mycompany.vibra.Content.PopUpChoices;

import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.DunbarFactory;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.FontFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundedButtonFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundedPanelFactory;
import com.mycompany.vibra.Factories.ThemeFactory.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PopUp_YesNo extends RoundedPanelFactory implements ThemeManager.ThemeChangerListener {

    private final FontFactory fontFactory = new DunbarFactory();
    private boolean isConfirmed = false;
    private final String titleText;
    private final String messageText;

    private JLabel titleLabel;
    private JLabel messageLabel;
    private RoundedButtonFactory yesButton;
    private RoundedButtonFactory noButton;

    public PopUp_YesNo(String title, String message) {
        super(20, ThemeManager.getInstance().getContainerColor(), null, 0);
        this.titleText = title;
        this.messageText = message;

        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(25, 25, 15, 25));
        setOpaque(false); // Crucial for rounded border to show up when in a JDialog

        initUI();

        ThemeManager.getInstance().addThemeChangerListener(this);
        applyTheme();
    }

    private void initUI() {
        // --- 1. Text Content ---
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setAlignmentX(LEFT_ALIGNMENT);

        titleLabel = new JLabel(titleText);
        titleLabel.setFont(fontFactory.createFont("dunbartall_bold", 22));
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);

        messageLabel = new JLabel("<html><p style='width: 250px;'>"+ messageText + "</p></html>");
        messageLabel.setFont(fontFactory.createFont("dunbartall_book", 14));
        messageLabel.setAlignmentX(LEFT_ALIGNMENT);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(messageLabel);

        add(textPanel, BorderLayout.CENTER);

        // --- 2. Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        noButton = createCancelButton("No");
        yesButton = createConfirmButton("Yes");

        buttonPanel.add(noButton);
        buttonPanel.add(yesButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        noButton.addActionListener(e -> {
            isConfirmed = false;
            closeDialog();
        });

        yesButton.addActionListener(e -> {
            isConfirmed = true;
            closeDialog();
        });
    }

    private void closeDialog() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w != null) {
            w.dispose();
        }
    }

    private RoundedButtonFactory createConfirmButton(String text) {
        RoundedButtonFactory button = new RoundedButtonFactory(text, 25);

        Color baseColor = ThemeManager.getInstance().getAccentColor(); // Purple accent
        Color hoverColor = baseColor.brighter();
        Color pressColor = baseColor.darker();

        button.setBackground(baseColor);
        button.setForeground(Color.WHITE);
        button.setFont(fontFactory.createFont("dunbartall_bold", 16));
        button.setPreferredSize(new Dimension(120, 35));

        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { button.setBackground(hoverColor); }
            @Override public void mouseExited(MouseEvent e) { button.setBackground(baseColor); }
            @Override public void mousePressed(MouseEvent e) { button.setBackground(pressColor); }
            @Override public void mouseReleased(MouseEvent e) { button.setBackground(hoverColor); }
        });
        return button;
    }

    private RoundedButtonFactory createCancelButton(String text) {
        RoundedButtonFactory button = new RoundedButtonFactory(text, 25);

        // Use a theme-aware neutral color for Cancel/No
        Color baseColor = new Color(100, 100, 100);
        Color hoverColor = new Color(130, 130, 130);
        Color pressColor = new Color(70, 70, 70);

        button.setBackground(baseColor);
        button.setForeground(Color.WHITE);
        button.setFont(fontFactory.createFont("dunbartall_book", 16));
        button.setPreferredSize(new Dimension(120, 35));

        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { button.setBackground(hoverColor); }
            @Override public void mouseExited(MouseEvent e) { button.setBackground(baseColor); }
            @Override public void mousePressed(MouseEvent e) { button.setBackground(pressColor); }
            @Override public void mouseReleased(MouseEvent e) { button.setBackground(hoverColor); }
        });
        return button;
    }

    /**
     * Removes the theme listener when the panel is disposed to prevent memory leaks.
     */
    @Override
    public void removeNotify() {
        super.removeNotify();
        ThemeManager.getInstance().removeThemeChangerListener(this);
    }

    // --- Theme Logic ---

    @Override
    public void onThemeChanged(boolean isDarkMode) {
        applyTheme();
        revalidate();
        repaint();
    }

    private void applyTheme() {
        ThemeManager tm = ThemeManager.getInstance();
        Color foreground = tm.getForegroundColor();
        Color accent = tm.getAccentColor();

        // 1. Update Panel Background
        setBackgroundColor(tm.getContainerColor());

        // 2. Update Text Colors
        titleLabel.setForeground(foreground);
        messageLabel.setForeground(foreground);

        // 3. Update Confirm Button Colors (Accent)
        Color confirmBase = accent;
        Color confirmHover = confirmBase.brighter();
        Color confirmPress = confirmBase.darker();

        yesButton.setBackground(confirmBase);
        // Note: MouseAdapter references these colors, but applying background here handles the un-hovered state
        for (java.awt.event.MouseListener listener : yesButton.getMouseListeners()) {
            if (listener instanceof MouseAdapter) {
                // If we had direct access to the anonymous MouseAdapter's state variables, we'd update them.
                // Since we don't, we just rely on setBackground() for the base state.
            }
        }

        // 4. Repaint buttons to ensure they pick up new colors
        noButton.repaint();
        yesButton.repaint();
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }
}