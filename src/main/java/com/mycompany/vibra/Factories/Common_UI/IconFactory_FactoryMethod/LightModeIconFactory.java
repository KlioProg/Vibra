package com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod;

import javax.swing.*;

public class LightModeIconFactory extends IconFactory {
    @Override
    public ImageIcon createIcon(String type) {
        return switch (type.toLowerCase()) {

            // Other Light-mode icons
            case "vibra_logo" -> createIcon("/images/vibradark.png", 65, 50);
            case "heart" -> createIcon("/icons/heart_dark.png", 28, 28);
            case "default_cover" -> createIcon("/images/default_cover.png", 250, 250);
            case "music" -> createIcon("/icons/music_dark.png", 28, 28);
            case "theme_button" -> createIcon("/images/lightMode.png", 28, 28);
            // Hover Light-mode icons
            case "heart_hover" -> createIcon("/icons/heart_white.png", 28, 28);
            case "music_hover" -> createIcon("/icons/music_white.png", 28, 28);
            case "logout" -> createIcon("/icons/Logout_black.png", 34,34);
            case "logout_hover" -> createIcon("/icons/Logout_Hover.png", 34,34);


            default -> {
                System.err.println("Unknown light icon: " + type);
                yield new ImageIcon();
            }
        };
    }
}
