package com.mycompany.vibra.Content.Create_Account;



import com.mycompany.vibra.Factories.Common_UI.BackgroundFactory;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class CreateWindow extends JFrame {
    public CreateWindow() {
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
            JPanel login = new createAccPanel();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.anchor = GridBagConstraints.CENTER;
            frame.add(login, gbc);

            frame.setVisible(true);
        });
    }

    public static void main(String[] args) {
        new CreateWindow();
    }

}
