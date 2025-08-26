package com.mycompany.vibra.panels;


import com.mycompany.vibra.utilities.firstPanelBg;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class vibraOpen {
    

    public static void main(String[] args) {
        GridBagConstraints c = new GridBagConstraints();
        SwingUtilities.invokeLater(() -> {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Dimension screenSize = toolkit.getScreenSize();
            int width = (screenSize.width);
            int height = (screenSize.height);

            JFrame frame = new JFrame("Vibra");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(width, height);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setLayout(new GridBagLayout());

            firstPanelBg bg = new firstPanelBg();
            bg.setLayout(new GridBagLayout());
            frame.setContentPane(bg);


            //Center
            JPanel login = new VibraLoginPanel();
            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 1.0;
            c.weighty = 1.0;
            c.anchor = GridBagConstraints.CENTER;
            frame.add(login, c);

            frame.setVisible(true);
        });
    }
}



