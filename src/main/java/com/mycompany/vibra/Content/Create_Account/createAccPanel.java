package com.mycompany.vibra.Content.Create_Account;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.mycompany.vibra.Content.MainAppFrame;
import com.mycompany.vibra.Factories.Common_UI.*;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.DunbarFactory;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.FontFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.ButtonIconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.IconFactory;
import com.mycompany.vibra.model.User;
import com.mycompany.vibra.service.AuthService;

public class createAccPanel extends JPanel {

    private RoundedTextFieldFactory username;
    private RoundedTextFieldFactory password;
    private RoundedTextFieldFactory email;
    private IconFactory iconFactory;
    FontFactory fontFactory = new DunbarFactory();

    public createAccPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.iconFactory = new ButtonIconFactory();


        // Top glue
        add(Box.createVerticalGlue());

        RoundPadderFactory panel = new RoundPadderFactory(50,0,0);
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
        contentPanel.setPreferredSize(new Dimension(400, 600));
        contentPanel.setBackground(new Color(0x100D0D));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Logo
        JLabel imageLabel = new JLabel(iconFactory.createIcon("vibra"));
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title
        JLabel label1 = new JLabel("Get Grooving");
        label1.setAlignmentX(Component.CENTER_ALIGNMENT);
        label1.setFont(fontFactory.createFont("dunbartall_bold", 32));
        label1.setForeground(Color.WHITE);

        // Subtitle
        JLabel label2 = new JLabel("by creating a free account");
        label2.setAlignmentX(Component.CENTER_ALIGNMENT);
        label2.setFont(fontFactory.createFont("dunbartall_book", 16));
        label2.setForeground(Color.WHITE);

        // Fields: only username + password
        username = textField("Username");
        password = textField("Password");
        email = textField("E-mail");    

        RoundedButtonFactory startButton = button("Start the Vibe!");

        contentPanel.add(Box.createVerticalStrut(36));
        contentPanel.add(imageLabel);
        contentPanel.add(Box.createVerticalStrut(32));
        contentPanel.add(label1);
        contentPanel.add(Box.createVerticalStrut(4));
        contentPanel.add(label2);
        contentPanel.add(Box.createVerticalStrut(12));
        contentPanel.add(email);
        contentPanel.add(Box.createVerticalStrut(12));
        contentPanel.add(username);
        contentPanel.add(Box.createVerticalStrut(12));
        contentPanel.add(password);
        contentPanel.add(Box.createVerticalStrut(28));
        contentPanel.add(startButton);

        return contentPanel;
    }

    private RoundedTextFieldFactory textField(String placeholder) {
        RoundedTextFieldFactory textField = new RoundedTextFieldFactory(40);
        textField.setPreferredSize(new Dimension(350, 44));
        textField.setAlignmentX(Component.CENTER_ALIGNMENT);
        textField.setBackground(new Color(0xF9F6EE));
        textField.setForeground(new Color(0x100D0D));
        textField.setFont(fontFactory.createFont("dunbartall_book", 16));
        textField.setPlaceholder(placeholder);
        return textField;
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
                buttons.setBackground(new Color(0x7B2CBF)); // hover color
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                buttons.setBackground(new Color(0x9D4EDD)); // reset background
                buttons.setForeground(new Color(0xF9F6EE)); // reset text color
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                buttons.setBackground(new Color(0x5A189A)); // darker color
                buttons.setForeground(new Color(0x9D4EDD));
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                buttons.setBackground(new Color(0x7B2CBF));
                buttons.setForeground(new Color(0xF9F6EE));

                String userText = username.getText().trim();
                String passwordText = password.getText().trim();
                String emailText = email.getText().trim();

                try {
                    AuthService authService = new AuthService();
                    User user = authService.signup(userText, passwordText, emailText);

                    if (user != null) {
                        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(createAccPanel.this);
                        MainAppFrame mainFrame = new MainAppFrame(user.getId());
                        mainFrame.setVisible(true);
                        topFrame.dispose();
                    }
                } catch (Exception ex) {
                    javax.swing.JOptionPane.showMessageDialog(
                        createAccPanel.this,
                        "Signup failed: " + ex.getMessage(),
                        "Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        return buttons;
    }
}
