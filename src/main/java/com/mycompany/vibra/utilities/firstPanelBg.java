package com.mycompany.vibra.utilities;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.JPanel;

public class firstPanelBg extends JPanel {
    private Image bgImage;

    public firstPanelBg(){
        bgImage = Toolkit.getDefaultToolkit().getImage("background.jpg");
    }


    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        GradientPaint gp = new GradientPaint(
            0, 0, new Color(0x9D4EDD),      // top color
            0, getHeight(), new Color(0x552A77) // bottom color
        );
        g2d.setPaint(gp);
        g2d.fillRect(0,0,getWidth(),getHeight());
    }
}
