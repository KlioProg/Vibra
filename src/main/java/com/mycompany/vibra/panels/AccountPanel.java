package com.mycompany.vibra.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AccountPanel extends JPanel {
    private JLabel search;
    private JLabel music;
    private JLabel heart;
    private JLabel library;

    public AccountPanel() {
        // Sidebar background and vertical layout
        setBackground(new Color(0x010101));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // --- Top image panel (logo + profile photo) ---
        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        imagePanel.setBackground(new Color(0x010101));
        imagePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140)); // tight height
        imagePanel.setAlignmentX(Component.CENTER_ALIGNMENT); // center in sidebar

        // Logo
        ImageIcon icon = new ImageIcon("src\\main\\resources\\images\\vibraSmall.png");
        JLabel imageLabel = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(65, 50, Image.SCALE_SMOOTH)));
        imageLabel.setBorder(BorderFactory.createEmptyBorder(32, 5, 0, 0));
        imagePanel.add(imageLabel);

        add(imagePanel);
        add(Box.createVerticalStrut(40)); // space between top images and icons

        // --- Icons with hover images ---
            search = createHoverIcon(
            "src\\main\\resources\\icons\\search.png", 
            "src\\main\\resources\\icons\\search_white.png", 
            () -> System.out.println("Search clicked!")
            );
            add(search);
            add(Box.createVerticalStrut(40));

            music = createHoverIcon(
                    "src\\main\\resources\\icons\\music.png", 
                    "src\\main\\resources\\icons\\music_white.png", 
                    () -> System.out.println("Music clicked!")
            );
            add(music);
            add(Box.createVerticalStrut(40));

            heart = createHoverIcon(
                    "src\\main\\resources\\icons\\heart.png", 
                    "src\\main\\resources\\icons\\heart_white.png", 
                    () -> System.out.println("Heart clicked!")
            );
            add(heart);
            add(Box.createVerticalStrut(40));

            library = createHoverIcon(
                    "src\\main\\resources\\icons\\library.png", 
                    "src\\main\\resources\\icons\\library_white.png", 
                    () -> System.out.println("Library clicked!")
            );
            add(library);
            add(Box.createVerticalGlue());


    }

    // Helper method to create a centered icon label
    private JLabel createHoverIcon(String normalPath, String hoverPath, Runnable onClick) {
        ImageIcon normal = new ImageIcon(new ImageIcon(normalPath).getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH));
        ImageIcon hover = new ImageIcon(new ImageIcon(hoverPath).getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH));

        JLabel label = new JLabel(normal);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // always have fixed padding
        label.setBorder(BorderFactory.createEmptyBorder(10, 35, 10, 35));
        label.setOpaque(false); // transparent until pressed

        label.addMouseListener(new java.awt.event.MouseAdapter() {
            private boolean active = false; // tracks toggle state

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                label.setIcon(hover);
                
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                    label.setIcon(normal);
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                    label.setOpaque(true);
                    label.setBackground(new Color(0x9D4EDD)); // active color
                    label.setIcon(hover); // keep hover icon
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                onClick.run(); // perform action
                label.setBackground(new Color(0x010101));
            }
        });


        return label;
    }




    // Test pane
}

//ImageIcon photo = new ImageIcon("D:\\Academics\\projectVibra_verKa\\src\\resources\\photo2.png");
//JLabel photoLabel = new JLabel(new ImageIcon(photo.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
//        imagePanel.add(photoLabel);
