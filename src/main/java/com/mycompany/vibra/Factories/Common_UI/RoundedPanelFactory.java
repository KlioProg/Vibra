package com.mycompany.vibra.Factories.Common_UI;

import javax.swing.*;
import java.awt.*;

public class RoundedPanelFactory extends JPanel {

    private int cornerRadius;
    private Color backgroundColor;
    private Color borderColor;
    private int borderThickness;

    /**
     * Creates a rounded panel with optional border and optional fixed size (no shadows).
     *
     * @param cornerRadius    radius of the corners
     * @param backgroundColor background color
     * @param borderColor     border color (null = no border)
     * @param borderThickness border width in pixels (0 = no border)
     * @param preferredWidth  set preferred width (0 to ignore)
     * @param preferredHeight set preferred height (0 to ignore)
     */
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
        setLayout(new GridBagLayout()); // supports icon + text

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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Draw background
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);

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

    /**
     * Adds an icon + text to the panel, side by side.
     */
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

    // --- Convenience creators ---
    public static RoundedPanelFactory createErrorPanel(ImageIcon errorIcon, String message, Font font) {
        RoundedPanelFactory panel = new RoundedPanelFactory(
                16, new Color(0xC1121F), new Color(0xC1121F), 1,
                350, 60 // you can adjust width/height here
        );
        panel.addIconWithText(errorIcon, "<b style='color:white;'>" + message + "</b>", font, Color.WHITE);
        return panel;
    }
}
