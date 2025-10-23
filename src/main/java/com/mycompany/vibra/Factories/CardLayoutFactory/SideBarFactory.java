package com.mycompany.vibra.Factories.CardLayoutFactory;

import com.mycompany.vibra.Factories.Common_UI.RoundedButtonFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class SideBarFactory extends JPanel {

    private static final Color DEFAULT_BG = new Color(0x010101);
    private static final Color HIGHLIGHT_BG = new Color(0x9D4EDD);

    public static JPanel createNavigationSideBar(int width, Consumer<String> onItemClick) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(width, Integer.MAX_VALUE));
        panel.setBackground(DEFAULT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        addNavigationItem(panel, "Main", onItemClick);
        addNavigationItem(panel, "Liked", onItemClick);

        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private static void addNavigationItem(JPanel panel, String label, Consumer<String> onItemClick) {
        JButton button = new RoundedButtonFactory(label, 20);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(60, 60));
        button.setPreferredSize(new Dimension(60, 60));
        button.setFocusable(false);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setForeground(Color.WHITE);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e){
                button.setBackground(HIGHLIGHT_BG);
                button.setOpaque(true);
            }

            public void mouseExited(MouseEvent e){
                button.setBackground(null);
                button.setOpaque(false);
            }
        });

        button.addActionListener(e ->{
            if(onItemClick != null){
                onItemClick.accept(label);
            }
        });


        panel.add(button);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }
}

