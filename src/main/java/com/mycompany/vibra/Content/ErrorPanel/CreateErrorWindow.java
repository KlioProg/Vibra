package com.mycompany.vibra.Content.ErrorPanel;

import com.mycompany.vibra.Content.Create_Account.createAccPanel;
import com.mycompany.vibra.Factories.Common_UI.BackgroundFactory;

import javax.swing.*;
import java.awt.*;

public class CreateErrorWindow extends JFrame{
    public CreateErrorWindow() {
        GridBagConstraints gbc = new GridBagConstraints();
        SwingUtilities.invokeLater(() -> {
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


            //Center
            JPanel Error = new CreateErrorPanel();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.anchor = GridBagConstraints.CENTER;
            frame.add(Error, gbc);

            frame.setVisible(true);
        });
    }

    public static void main(String[] args) {
        new CreateErrorWindow();
    }
}
