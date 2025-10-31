/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.vibra;

import com.mycompany.vibra.Content.Login_Panel.LoginContentPanel;
import com.mycompany.vibra.Factories.Common_UI.BackgroundFactory;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author magno
 */
public class Vibra {
    public static JFrame createMainFrame() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int width = (int) (screenSize.width * 0.75);
        int height = (int) (screenSize.height * 0.75);

        JFrame frame = new JFrame("Vibra");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setLayout(new GridBagLayout());

        BackgroundFactory bg = new BackgroundFactory();
        bg.setLayout(new GridBagLayout());
        frame.setContentPane(bg);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;

        frame.add(new LoginContentPanel(), gbc);
        return frame;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createMainFrame().setVisible(true);
        });
    }
}


