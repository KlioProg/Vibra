package com.mycompany.vibra.panels;



import com.mycompany.vibra.panels.createAccPanel;
import com.mycompany.vibra.utilities.firstPanelBg;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class createPanel extends JFrame {
    public createPanel() {
        GridBagConstraints gbc = new GridBagConstraints();
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
        new createPanel();
    }

}
