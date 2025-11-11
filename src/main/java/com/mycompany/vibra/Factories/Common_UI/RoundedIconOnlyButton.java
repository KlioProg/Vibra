package com.mycompany.vibra.Factories.Common_UI;

import javax.swing.*;
import java.awt.*;

public class RoundedIconOnlyButton extends RoundedButtonFactory {

    public RoundedIconOnlyButton(Icon icon, int radius, int size) {
        super("", radius);
        setIcon(icon);

        setPreferredSize(new Dimension(size, size));

        setBorder(BorderFactory.createEmptyBorder());
    }
}