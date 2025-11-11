package com.mycompany.vibra.Factories.Common_UI;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * A utility class with static methods to paint gradients on components.
 * This prevents duplicating gradient code in multiple panels.
 */
public final class GradientPainter {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private GradientPainter() {}

    /**
     * Paints a radial gradient on the background of a given component.
     *
     * @param g The Graphics object from the component's paintComponent method.
     * @param c The component on which to paint the gradient.
     * @param colors The array of colors to use in the gradient.
     * @param fractions The array of floats (0.0 to 1.0) specifying the distribution of colors.
     */
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