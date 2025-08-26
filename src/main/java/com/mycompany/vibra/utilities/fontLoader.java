package com.mycompany.vibra.utilities;

import java.awt.Font;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class fontLoader {
    
       public static Font loadFont(String resourcePath, float size) {
    System.out.println("Loading font: " + resourcePath);
    try (InputStream is = fontLoader.class.getResourceAsStream(resourcePath)) {
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