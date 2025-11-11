package com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod;

import javax.swing.*;

public class ButtonIconFactory extends IconFactory {

    @Override
    public ImageIcon createIcon(String type) {
        return switch (type.toLowerCase()) {
            // Cannot be affected by the Theme Manager
            case "default_cover" -> createIcon("/images/default_cover.png", 28, 28);
            case "vibra" -> createIcon("/images/vibraSmall.png",120,90);
            case "play" -> createIcon("/images/button_play.png", 80, 80);
            case "pause" -> createIcon("/images/button_pause.png", 80, 80);
            case "back" -> createIcon("/images/backButton.png", 39, 25);
            case "skip" -> createIcon("/images/Skip.png", 39, 25);
            case "sound_down" -> createIcon("/images/soundDown.png", 16, 14);
            case "sound_up" -> createIcon("/images/SoundUp.png", 16, 14);
            case "liked" -> createIcon("/images/like_button.png", 28, 28);
            case "dark" -> createIcon("/images/darkMode.png", 28, 28);
            case "light" -> createIcon("/images/lightMode.png", 28, 28);
            case "playlist_default" -> createIcon("/images/default_cover.png", 190, 190);
            case "add" -> createIcon("/icons/add_icon.png", 28, 28);
            case "delete" -> createIcon("/icons/delete_icon.png", 26, 26);
            // Defaults Hover
            case "liked_pressed" -> createIcon("/images/like_pressed.png", 28, 27);


            default -> {
                System.err.println("Unknown button icon type: " + type);
                yield new ImageIcon();
            }
        };
    }
}

