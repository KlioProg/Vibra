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

public class PopUp_Alert extends RoundedPanelFactory implements ThemeManager.ThemeChangerListener {

    private final FontFactory fontFactory = new DunbarFactory();

    private JLabel titleLabel;
    private JLabel messageLabel;
    private RoundedButtonFactory okButton;

    public PopUp_Alert(String title, String message) {
        super(20, ThemeManager.getInstance().getContainerColor(), null, 0);

        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(25, 25, 15, 25));
        setOpaque(false);

        initUI(title, message);

        ThemeManager.getInstance().addThemeChangerListener(this);
        applyTheme();
    }

    private void initUI(String title, String message) {
        // --- 1. Text Content ---
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setAlignmentX(LEFT_ALIGNMENT);

        titleLabel = new JLabel(title);
        titleLabel.setFont(fontFactory.createFont("dunbartall_bold", 22));
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);

        // Use HTML to auto-wrap the message text
        messageLabel = new JLabel("<html><p style='width: 250px;'>"+ message + "</p></html>");
        messageLabel.setFont(fontFactory.createFont("dunbartall_book", 14));
        messageLabel.setAlignmentX(LEFT_ALIGNMENT);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(messageLabel);

        add(textPanel, BorderLayout.CENTER);

        // --- 2. Button ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setOpaque(false);

        okButton = createConfirmButton("OK");
        buttonPanel.add(okButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        okButton.addActionListener(e -> closeDialog());
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
        button.setPreferredSize(new Dimension(90, 35));

        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { button.setBackground(hoverColor); }
            @Override public void mouseExited(MouseEvent e) { button.setBackground(baseColor); }
            @Override public void mousePressed(MouseEvent e) { button.setBackground(pressColor); }
            @Override public void mouseReleased(MouseEvent e) { button.setBackground(hoverColor); }
        });
        return button;
    }

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
        setBackgroundColor(tm.getContainerColor());
        titleLabel.setForeground(tm.getForegroundColor());
        messageLabel.setForeground(tm.getForegroundColor());

        // Update OK Button
        Color baseColor = tm.getAccentColor();
        Color hoverColor = baseColor.brighter();
        Color pressColor = baseColor.darker();
        okButton.setBackground(baseColor);
        // We can't update the anonymous listener's colors,
        // but setting the base and repainting is enough.
        okButton.repaint();
    }

    public static void showAlert(Component parent, String title, String message) {
        PopUp_Alert alertPanel = new PopUp_Alert(title, message);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), title, true); // Modal
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));
        dialog.setContentPane(alertPanel);

        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true); // Pauses here
    }
}