package com.mycompany.vibra.Factories.Common_UI;

import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.ButtonIconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.IconFactory;

import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageUtils {


    private static ImageIcon defaultPlaylistIcon;

    public static ImageIcon convertBytesToImageIcon(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return getDefaultIcon();
        }
        try {
            return new ImageIcon(ImageIO.read(new ByteArrayInputStream(bytes)));
        } catch (IOException e) {
            System.err.println("Error reading image from byte array: " + e.getMessage());
            return getDefaultIcon();
        }
    }

    public static byte[] convertImageIconToBytes(ImageIcon icon) {
        if (icon == null) {
            return null;
        }

        Image image = icon.getImage();

        BufferedImage bufferedImage = new BufferedImage(
                image.getWidth(null),
                image.getHeight(null),
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g = bufferedImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            System.err.println("Error converting ImageIcon to byte array: " + e.getMessage());
            return null;
        }
    }

    private static ImageIcon getDefaultIcon() {
        if (defaultPlaylistIcon == null) {
            IconFactory icons = new ButtonIconFactory();
            defaultPlaylistIcon = icons.createIcon("playlist_default");
        }
        return defaultPlaylistIcon;
    }
}