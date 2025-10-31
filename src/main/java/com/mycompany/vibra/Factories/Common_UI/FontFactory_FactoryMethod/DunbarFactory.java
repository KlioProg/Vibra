package com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod;

import java.awt.*;
import java.io.InputStream;

public class DunbarFactory  extends FontFactory{

    @Override
    public Font createFont(String type, int size) {
        try {
            String fontPath;
            int style;

            switch (type.toLowerCase()) {
                case "dunbarlow_bold" -> {
                    fontPath = "/fonts/DunbarLow-Bold.ttf";
                    style = Font.BOLD;
                }
                case "dunbartall_bold" -> {
                    fontPath = "/fonts/DunbarTall-Bold.ttf";
                    style = Font.BOLD;
                }
                case "dunbartall_book" -> {
                    fontPath = "/fonts/DunbarTall-Book.ttf";
                    style = Font.PLAIN;
                }
                default -> throw new IllegalArgumentException("Unknown font type: " + type);
            }

            // Load the font file from resources
            InputStream fontStream = getClass().getResourceAsStream(fontPath);
            if (fontStream == null) {
                throw new IllegalArgumentException("Font file not found: " + fontPath);
            }

            // Create and size the font
            Font baseFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
            return baseFont.deriveFont(style, (float) size);

        } catch (Exception e) {
            e.printStackTrace();
            return new Font("SansSerif", Font.PLAIN, size);
        }
    }
}
