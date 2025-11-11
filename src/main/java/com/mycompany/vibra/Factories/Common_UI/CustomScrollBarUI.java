package com.mycompany.vibra.Factories.Common_UI;

import com.mycompany.vibra.Factories.ThemeFactory.ThemeManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 * A custom ScrollBarUI to match the Vibra theme.
 * It features rounded thumbs and no arrow buttons.
 */
public class CustomScrollBarUI extends BasicScrollBarUI {

    private final int thumbWidth = 8;
    private final int thumbArc = 8;

    @Override
    protected void configureScrollBarColors() {
        // This is called when the UI is set.
        // We get the colors from our ThemeManager.
        ThemeManager tm = ThemeManager.getInstance();
        thumbColor = tm.getAccentColor();
        trackColor = tm.getSidebarColor();

        // Apply the track color to the scrollbar itself
        scrollbar.setBackground(trackColor);
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        // Make the scrollbar thinner than default
        return new Dimension(thumbWidth + 4, super.getPreferredSize(c).height);
    }

    // --- Remove the Arrow Buttons ---

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    private JButton createZeroButton() {
        JButton button = new JButton();
        Dimension zeroDim = new Dimension(0, 0);
        button.setPreferredSize(zeroDim);
        button.setMinimumSize(zeroDim);
        button.setMaximumSize(zeroDim);
        return button;
    }

    // --- Paint the Custom Thumb (the part you drag) ---

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color color;
        if (isDragging || isThumbRollover()) {
            color = thumbColor.brighter(); // Brighter purple on hover/drag
        } else {
            color = thumbColor; // Normal purple
        }
        g2.setColor(color);

        // Center the thumb in the available space
        int x = thumbBounds.x + (thumbBounds.width - thumbWidth) / 2;

        // Draw the rounded rectangle
        g2.fillRoundRect(x, thumbBounds.y + 2, thumbWidth, thumbBounds.height - 4, thumbArc, thumbArc);
        g2.dispose();
    }

    // --- Paint the Custom Track (the background) ---

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(trackColor);
        g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        g2.dispose();
    }
}