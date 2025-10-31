package com.mycompany.vibra.Factories.Common_UI;

import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.DunbarFactory;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.FontFactory;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JButton;

public class RoundedButtonFactory extends JButton {
    private int radius;
    FontFactory fontFactory = new DunbarFactory();

    public RoundedButtonFactory(String text, int radius) {
        super(text);
        this.radius = radius;
        setContentAreaFilled(false); // don't fill default button background
        setFocusPainted(false);      // remove focus outline
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15)); // padding
        setForeground(Color.WHITE);  // default text color
        setBackground(new Color(0x5A189A)); // default background
        setFont(fontFactory.createFont("dunbartall_book", 16));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background only (no border)
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        g2.dispose();

        // Draw the button text
        super.paintComponent(g);
    }
}
