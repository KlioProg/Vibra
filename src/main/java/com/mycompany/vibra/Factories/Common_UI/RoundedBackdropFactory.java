package com.mycompany.vibra.Factories.Common_UI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * A simple JPanel that paints a rounded background based on its current
 * background color. Designed to be theme-aware.
 */
public class RoundedBackdropFactory extends JPanel {
    private int cornerRadius;

    public RoundedBackdropFactory(LayoutManager layout, int cornerRadius) {
        super(layout);
        this.cornerRadius = cornerRadius;
        setOpaque(false); // We are responsible for painting the background
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(getBackground()); // Use the standard background color
        g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
        g2d.dispose();
    }
}