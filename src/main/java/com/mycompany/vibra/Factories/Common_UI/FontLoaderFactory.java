package com.mycompany.vibra.Factories.Common_UI;

import java.awt.Font;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FontLoaderFactory {
    
       public static Font loadFont(String resourcePath, float size) {
    System.out.println("Loading font: " + resourcePath);
    try (InputStream is = FontLoaderFactory.class.getResourceAsStream(resourcePath)) {
        if (is == null) {
            throw new FileNotFoundException("Font not found: " + resourcePath);
        }
        Font font = Font.createFont(Font.TRUETYPE_FONT, is);
        return font.deriveFont(size);
    } catch (Exception e) {
        e.printStackTrace();
        return new Font("SansSerif", Font.PLAIN, (int) size);
    }
}


}