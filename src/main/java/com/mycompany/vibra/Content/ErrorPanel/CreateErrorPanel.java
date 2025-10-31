package com.mycompany.vibra.Content.ErrorPanel;

import com.mycompany.vibra.Content.Login_Panel.LoginContentPanel;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.DunbarFactory;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.FontFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.ButtonIconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.CommonIconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.IconFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundPadderFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundedButtonFactory;

import javax.swing.*;
import java.awt.*;

public class CreateErrorPanel extends JPanel {
    FontFactory fontFactory = new DunbarFactory();
    IconFactory commonIcon = new CommonIconFactory();

    public CreateErrorPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Top glue for centering vertically
        add(Box.createVerticalGlue());

        RoundPadderFactory panel = new RoundPadderFactory(50, 0, 0);
        panel.setPreferredSize(new Dimension(480, 500));
        panel.setBackground(new Color(0x100D0D));
        panel.setPaintBackground(true);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(loginContents());
        add(panel);

        // Bottom glue
        add(Box.createVerticalGlue());
    }

    private JPanel loginContents() {
        JPanel contentPanel = new JPanel();
        contentPanel.setPreferredSize(new Dimension(400, 400));
        contentPanel.setBackground(new Color(0x100D0D));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        JLabel iconLabel = new JLabel(commonIcon.createIcon("handler"));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title
        JLabel label1 = new JLabel("Oops!!");
        label1.setAlignmentX(Component.CENTER_ALIGNMENT);
        label1.setFont(fontFactory.createFont("dunbartall_bold", 32));
        label1.setForeground(Color.WHITE);

        // Subtitle
        JLabel label2 = new JLabel("Our hamsters need a coffee break!!");
        label2.setAlignmentX(Component.CENTER_ALIGNMENT);
        label2.setFont(fontFactory.createFont("dunbartall_book", 16));
        label2.setForeground(Color.WHITE);

        JLabel label3 = new JLabel("Please try again!!");
        label3.setAlignmentX(Component.CENTER_ALIGNMENT);
        label3.setFont(fontFactory.createFont("dunbartall_book", 16));
        label3.setForeground(Color.WHITE);




        RoundedButtonFactory startButton = button("Go back home!");

        contentPanel.add(Box.createVerticalStrut(40));
        contentPanel.add(iconLabel);
        contentPanel.add(Box.createVerticalStrut(24));
        contentPanel.add(label1);
        contentPanel.add(Box.createVerticalStrut(4));
        contentPanel.add(label2);
        contentPanel.add(Box.createVerticalStrut(1));
        contentPanel.add(label3);
        contentPanel.add(Box.createVerticalStrut(28));
        contentPanel.add(startButton);

        return contentPanel;
    }

    private RoundedButtonFactory button(String text) {
        RoundedButtonFactory buttons = new RoundedButtonFactory(text, 40);
        buttons.setMaximumSize(new Dimension(350, 44));
        buttons.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttons.setBackground(new Color(0x9D4EDD));
        buttons.setForeground(new Color(0xF9F6EE));
        buttons.setFont(fontFactory.createFont("dunbartall_book", 24));

        buttons.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                buttons.setBackground(new Color(0x7B2CBF));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                buttons.setBackground(new Color(0x9D4EDD));
                buttons.setForeground(new Color(0xF9F6EE));
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                buttons.setBackground(new Color(0x5A189A));
                buttons.setForeground(new Color(0x9D4EDD));
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                buttons.setBackground(new Color(0x7B2CBF));
                buttons.setForeground(new Color(0xF9F6EE));

                // --- SWITCH PANEL LOGIC ---
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(CreateErrorPanel.this);
                if (frame != null) {
                    JPanel newPanel = new LoginContentPanel();
                    frame.getContentPane().removeAll();
                    frame.getContentPane().add(newPanel);
                    frame.revalidate();
                    frame.repaint();
                }
            }
        });

        return buttons;
    }
}
