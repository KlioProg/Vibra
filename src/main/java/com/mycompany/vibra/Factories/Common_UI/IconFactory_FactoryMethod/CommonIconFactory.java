package com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod;

import javax.swing.*;

public class CommonIconFactory extends IconFactory{
    @Override
    public ImageIcon createIcon(String type) {
        return switch (type.toLowerCase()) {

            case "error" -> createIcon("/images/Error.png", 25, 25);
            case "handler" -> createIcon("/icons/ErrorHandling.png", 200, 200);
            case "edit_hover" -> createIcon("/icons/edit_white.png", 34, 34);


            default -> {
                System.err.println("Unknown Common mode icon type: " + type);
                yield new ImageIcon();
            }
        };
    }
}
