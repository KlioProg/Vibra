package com.mycompany.vibra.Factories.Common_UI;


import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.DunbarFactory;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.FontFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A factory that creates a hover popup message that appears when the user
 * hovers over a component, and disappears when the mouse leaves.
 *
 * Usage:
 * HoverPopupMessageFactory.attachHoverPopup(button, "<html>Hello!</html>");
 */
public class HoverPopUpMessageFactory {

    private final FontFactory fontFactory = new DunbarFactory();
    private final Font defaultFont = fontFactory.createFont("dunbartall_book", 14);
    private final Color defaultBg = new Color(0x9D4EDD);
    private final Color defaultFg = new Color(0xF9F6EE);


    public HoverPopUpMessageFactory() {
        // Prevent instantiation
    }

    /**
     * Attaches a hover popup message to a component.
     *
     * @param component  The component to attach to (e.g., JButton, JLabel)
     * @param message    The HTML/text message to display in the popup
     */
    public void attachHoverPopup(JComponent component, String message) {
        attachHoverPopup(component, message, defaultBg, defaultFg, defaultFont);
    }

    /**
     * Attaches a hover popup message with custom styling.
     *
     * @param component The component to attach to
     * @param message   The message to display
     * @param bgColor   Background color
     * @param fgColor   Text color
     * @param font      Font used in the message
     */
    public static void attachHoverPopup(JComponent component, String message,
                                        Color bgColor, Color fgColor, Font font) {

        PopupFactory popupFactory = PopupFactory.getSharedInstance();
        final Popup[] activePopup = new Popup[1]; // Holder for the currently shown popup

        // --- Function to show popup ---
        Runnable showPopup = () -> {
            JPanel popupPanel = new JPanel(new BorderLayout());
            popupPanel.setBackground(bgColor);
            popupPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(bgColor.darker(), 0),
                    BorderFactory.createEmptyBorder(5, 7, 5, 7)
            ));

            JLabel label = new JLabel(message);
            label.setForeground(fgColor);
            label.setFont(font);
            popupPanel.add(label, BorderLayout.CENTER);

            // Get screen location
            Point location = MouseInfo.getPointerInfo().getLocation();

            SwingUtilities.invokeLater(() -> {
                Window window = SwingUtilities.getWindowAncestor(component);
                if (window != null) {
                    activePopup[0] = popupFactory.getPopup(window, popupPanel, location.x + 5, location.y + 10);
                    activePopup[0].show();
                }
            });
        };

        // --- Function to hide popup ---
        Runnable hidePopup = () -> {
            if (activePopup[0] != null) {
                activePopup[0].hide();
                activePopup[0] = null;
            }
        };

        // --- Mouse listeners ---
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                showPopup.run();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hidePopup.run();
            }
        });
    }
}
