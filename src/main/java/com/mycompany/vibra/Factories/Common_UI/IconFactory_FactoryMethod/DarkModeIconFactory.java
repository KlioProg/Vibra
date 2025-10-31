package com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod;

import javax.swing.*;

public class DarkModeIconFactory extends IconFactory {

    @Override
    public ImageIcon createIcon(String type) {
        return switch (type.toLowerCase()) {

            // Other dark-mode icons
            case "vibra_logo" -> createIcon("/images/vibraSmall.png", 65, 50);
            case "heart" -> createIcon("/icons/heart_white.png", 28, 28);
            case "default_cover" -> createIcon("/images/default_cover.png", 250, 250);
            case "music" -> createIcon("/icons/music_white.png", 28, 28);
            case "theme_button" -> createIcon("/images/darkMode.png", 28, 28);
            case "like" -> createIcon("/images/like_button.png", 28, 28);
            case "heart_hover" -> createIcon("/icons/heart_dark.png", 28, 28);
            case "music_hover" -> createIcon("/icons/music_dark.png", 28, 28);
            case "like_pressed" -> createIcon("/images/like_pressed.png", 28, 28);
            case "logout" -> createIcon("/icons/Logout_white.png", 34,34);
            case "logout_hover" -> createIcon("/icons/Logout_Hover.png", 34,34);

            default -> {
                System.err.println("Unknown dark mode icon type: " + type);
                yield new ImageIcon();
            }
        };
    }
}

