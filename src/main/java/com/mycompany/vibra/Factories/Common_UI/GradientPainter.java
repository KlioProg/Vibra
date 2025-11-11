package com.mycompany.vibra.Factories.Common_UI;

import java.awt.*;
import java.awt.geom.Point2D;


public final class GradientPainter {

    private GradientPainter() {}

    public static void paintRadialGradient(Graphics g, Component c, Color[] colors, float[] fractions) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Point2D center = new Point2D.Float(c.getWidth() / 2f, c.getHeight() / 2f);
        float radius = Math.max(c.getWidth(), c.getHeight());

        RadialGradientPaint gradient = new RadialGradientPaint(center, radius, fractions, colors);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, c.getWidth(), c.getHeight());

        g2d.dispose();
    }
}