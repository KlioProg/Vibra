package com.mycompany.vibra.utilities;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class roundPadder extends JPanel {
    private int cornerRadius;
    private int vPadding; // vertical padding
    private int hPadding; // horizontal padding

    public roundPadder(int radius, int vPadding, int hPadding) {
        this.cornerRadius = radius;
        this.vPadding = vPadding;
        this.hPadding = hPadding;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(vPadding, hPadding, vPadding, hPadding));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
        g2.dispose();
    }
}
