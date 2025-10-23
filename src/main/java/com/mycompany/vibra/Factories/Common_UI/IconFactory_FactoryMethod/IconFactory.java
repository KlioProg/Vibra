package com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod;

import javax.swing.*;


public abstract class IconFactory{

    public abstract ImageIcon createIcon(String type);

    protected ImageIcon createIcon(String type, int width, int height) {
        try {
            return new ImageIcon(
                    new ImageIcon(getClass().getResource(type))
                            .getImage()
                            .getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH)
            );
        } catch (Exception e) {
            System.err.println("Error loading icon: " + type);
            return new ImageIcon();
        }
    }
}
