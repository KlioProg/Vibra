package com.mycompany.vibra.Factories.Common_UI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundPadderFactory extends JPanel {
    private int cornerRadius;
    private int vPadding;
    private int hPadding;
    private boolean paintBackground = false; // 👈 Only paint when true

    public RoundPadderFactory(int radius, int vPadding, int hPadding) {
        this.cornerRadius = radius;
        this.vPadding = vPadding;
        this.hPadding = hPadding;

        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(vPadding, hPadding, vPadding, hPadding));
    }

    public void setPaintBackground(boolean paintBackground) {
        this.paintBackground = paintBackground;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (paintBackground) { // ✅ Only paint when true
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
            g2.dispose();
        }

        super.paintComponent(g);
    }
}
