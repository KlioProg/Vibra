package com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod;

import javax.swing.*;
import java.awt.*;
import java.net.URL; // Make sure to import this;

public abstract class FontFactory {

    public abstract Font createFont(String type, int size);

    protected Font createFont(String type) {
        int defaultSize = 14; // or any size you want
        try {
            URL url = getClass().getResource(type);
            return Font.createFont(Font.TRUETYPE_FONT, url.openStream());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading font: " + type);
            return new Font("SansSerif", Font.PLAIN, defaultSize);
        }
    }


}