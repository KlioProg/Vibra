package com.mycompany.vibra.utilities;

import java.awt.*;
import javax.swing.*;

public class RoundedTextField extends JTextField {
private final int radius;
    private String placeholder;

    public RoundedTextField(int radius) {
        super();
        this.radius = radius;
        setOpaque(false); // we paint our own background
        setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14)); // internal padding
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(350, 44);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        // 1. Paint rounded background
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.dispose();

        // 2. Let Swing paint text and caret
        super.paintComponent(g);

        // 3. Draw placeholder (if needed)
        if (placeholder != null && getText().isEmpty() && !isFocusOwner()) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setFont(getFont());
            g2d.setColor(Color.GRAY);
            Insets insets = getInsets();
            int xOffset = Math.max(insets.left + 4, radius / 2);
            FontMetrics fm = g2d.getFontMetrics();
            int yOffset = insets.top + fm.getAscent();
            g2d.drawString(placeholder, xOffset, yOffset);
            g2d.dispose();
        }
    }
}
