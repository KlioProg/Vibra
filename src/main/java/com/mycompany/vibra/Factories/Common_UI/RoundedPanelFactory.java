package com.mycompany.vibra.Factories.Common_UI;

import javax.swing.*;
import java.awt.*;

public class RoundedPanelFactory extends JPanel {

    private int cornerRadius;
    private Color backgroundColor;
    private Color borderColor;
    private int borderThickness;

    public RoundedPanelFactory(
            int cornerRadius,
            Color backgroundColor,
            Color borderColor,
            int borderThickness,
            int preferredWidth,
            int preferredHeight
    ) {
        this.cornerRadius = cornerRadius;
        this.backgroundColor = backgroundColor != null ? backgroundColor : getBackground();
        this.borderColor = borderColor;
        this.borderThickness = borderThickness;

        setOpaque(false);
        // setLayout(new GridBagLayout()); // 👈 FIX #1: REMOVE THIS LINE

        // --- Optional fixed size ---
        if (preferredWidth > 0 && preferredHeight > 0) {
            Dimension size = new Dimension(preferredWidth, preferredHeight);
            setPreferredSize(size);
            setMaximumSize(size);
            setMinimumSize(size);
        }
    }

    // --- For backward compatibility ---
    public RoundedPanelFactory(int cornerRadius, Color bg, Color border, int borderThickness) {
        this(cornerRadius, bg, border, borderThickness, 0, 0);
    }

    // 👇 FIX #2: ADD THIS METHOD TO ALLOW HOVER COLOR
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        repaint(); // Tell the panel to repaint with the new color
    }

    public Color getBackgroundColor() {
        return this.backgroundColor;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Draw background
        g2.setColor(backgroundColor);
        // 👇 FIX #3: REMOVE THE -1 TO FIX THE 1-PIXEL BORDER
        g2.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);

        // Draw border
        if (borderColor != null && borderThickness > 0) {
            g2.setStroke(new BasicStroke(borderThickness));
            g2.setColor(borderColor);
            g2.drawRoundRect(
                    borderThickness / 2,
                    borderThickness / 2,
                    width - borderThickness,
                    height - borderThickness,
                    cornerRadius,
                    cornerRadius
            );
        }

        g2.dispose();
    }

    public void addIconWithText(ImageIcon icon, String text, Font font, Color color) {
        JLabel iconLabel = new JLabel(icon);
        JLabel textLabel = new JLabel("<html>" + text + "</html>");
        textLabel.setFont(font);
        textLabel.setForeground(color);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Icon left
        gbc.gridx = 0;
        add(iconLabel, gbc);

        // Text right
        gbc.gridx = 1;
        add(textLabel, gbc);
    }

    public static RoundedPanelFactory createErrorPanel(ImageIcon errorIcon, String message, Font font) {
        RoundedPanelFactory panel = new RoundedPanelFactory(
                16, new Color(0xC1121F), new Color(0xC1121F), 1,
                350, 60 // you can adjust width/height here
        );
        panel.addIconWithText(errorIcon, "<b style='color:white;'>" + message + "</b>", font, Color.WHITE);
        return panel;
    }
}
