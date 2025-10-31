package com.mycompany.vibra.Factories.Common_UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoundedIconButtonFactory {

    /**
     * Creates a clean, icon-only JButton.
     *
     * @param icon         the default icon
     * @param hoverIcon    optional hover icon (can be null)
     * @param size         button size in pixels (width = height)
     * @param tooltipText  optional tooltip (can be null)
     * @return styled JButton
     */
    public static JButton createIconButton(Icon icon, Icon hoverIcon, int size, String tooltipText) {
        JButton button = new JButton(icon);

        // Basic styling for a flat, modern look
        button.setPreferredSize(new Dimension(size, size));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setToolTipText(tooltipText);

        // Optional hover effect (change icon)
        if (hoverIcon != null) {
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    button.setIcon(hoverIcon);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    button.setIcon(icon);
                }
            });
        }

        return button;
    }
}
