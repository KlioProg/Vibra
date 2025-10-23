package com.mycompany.vibra.Factories.ThemeFactory;

import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.ButtonIconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.DarkModeIconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.IconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.LightModeIconFactory;

import javax.swing.*;
import java.awt.*;

public class DarkModeToggle extends JPanel {
    private final JToggleButton toggleButton;
    private IconFactory iconFactory;

    public DarkModeToggle() {
        setLayout(new FlowLayout(FlowLayout.RIGHT));
        setOpaque(false);

        iconFactory = new ButtonIconFactory();
        toggleButton = new JToggleButton();

        updateButtonAppearance();

        toggleButton.addActionListener(e -> {
            ThemeManager.getInstance().toggleDarkMode();
        });

        ThemeManager.getInstance().addThemeChangerListener(isDark -> updateButtonAppearance());

        add(toggleButton);
    }

    private void updateButtonAppearance() {
        boolean isDarkMode = ThemeManager.getInstance().isDarkMode();

        if (isDarkMode) {
            iconFactory = new DarkModeIconFactory();
        } else {
            iconFactory = new LightModeIconFactory();
        }
        toggleButton.setIcon(iconFactory.createIcon("theme_button"));

        toggleButton.setToolTipText(isDarkMode ? "Switch to Light Mode" : "Switch to Dark Mode");
        toggleButton.setText(null);
        toggleButton.setBackground(ThemeManager.getInstance().getMusicPanelColor());
        toggleButton.setForeground(ThemeManager.getInstance().getForegroundColor());
        toggleButton.setOpaque(false);
        toggleButton.setContentAreaFilled(false);
        toggleButton.setBorderPainted(false);
        toggleButton.setFocusPainted(false);
    }
}
