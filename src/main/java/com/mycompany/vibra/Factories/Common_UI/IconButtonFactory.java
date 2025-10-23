package com.mycompany.vibra.Factories.Common_UI;

import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.IconFactory;

import javax.swing.*;
import java.awt.*;

public class IconButtonFactory extends JButton {

    private final IconFactory iconFactory;

    public IconButtonFactory(IconFactory IconFactory) {
        this.iconFactory = IconFactory;
    }

    public JLabel createIconLabel(String type, int width, int height) {
        ImageIcon icon = iconFactory.createIcon(type);
        Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        JLabel label = new JLabel(new ImageIcon(scaledImage));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

}
