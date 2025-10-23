package com.mycompany.vibra.Content.Main_Page.Main_Contents.Like_Panel;

import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.IconFactory;

import javax.swing.*;
import java.awt.*;

public class likedPanel extends JPanel {
    
    private LikedSongsPanel likedSongsPanel;

    public likedPanel(IconFactory iconFactory) {
        setLayout(new BorderLayout());

        // Gradient background panel
        JPanel gradientPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int width = getWidth();
                int height = getHeight();

                Color topColor = new Color(102, 0, 153);   // dark purple
                Color bottomColor = new Color(204, 153, 255); // light lavender

                GradientPaint gp = new GradientPaint(0, 0, topColor, 0, height, bottomColor);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, width, height);
            }
        };
        gradientPanel.setLayout(new BorderLayout());
        gradientPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Inner liked songs panel
        likedSongsPanel = new LikedSongsPanel();
        gradientPanel.add(likedSongsPanel, BorderLayout.CENTER);

        add(gradientPanel, BorderLayout.CENTER);
    }

    public LikedSongsPanel getLikedSongsPanel() {
        return likedSongsPanel;
    }
}
  
        
