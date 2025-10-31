package com.mycompany.vibra.Content.Login_Panel;

import com.mycompany.vibra.Content.Create_Account.CreateWindow;
import com.mycompany.vibra.Content.MainAppFrame;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.DunbarFactory;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.FontFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.ButtonIconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.IconFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundPadderFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundedButtonFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundedTextFieldFactory;
import com.mycompany.vibra.model.User;
import com.mycompany.vibra.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginContentPanel extends JPanel {

        private RoundedTextFieldFactory username;
        private RoundedTextFieldFactory password;
        private IconFactory iconFactory;
        FontFactory fontFactory = new DunbarFactory();


    public LoginContentPanel() {
            setOpaque(false);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.iconFactory = new ButtonIconFactory();


            // Top glue
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

            // Logo
            JLabel imageLabel = new JLabel(iconFactory.createIcon("vibra"));
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Title
            JLabel label1 = new JLabel("Login to Continue");
            label1.setAlignmentX(Component.CENTER_ALIGNMENT);
            label1.setFont(fontFactory.createFont("dunbartall_bold", 32));
            label1.setForeground(Color.WHITE);

            // Subtitle
            JLabel label2 = new JLabel("let's get it groovin!");
            label2.setAlignmentX(Component.CENTER_ALIGNMENT);
            label2.setFont(fontFactory.createFont("dunbartall_book", 16));
            label2.setForeground(Color.WHITE);


            // Fields: only username + password
            username = textField("Username");
            password = textField("Password");

            RoundedButtonFactory startButton = button("Start the Vibe!");

            contentPanel.add(Box.createVerticalStrut(36));
            contentPanel.add(imageLabel);
            contentPanel.add(Box.createVerticalStrut(32));
            contentPanel.add(label1);
            contentPanel.add(Box.createVerticalStrut(4));
            contentPanel.add(label2);
            contentPanel.add(Box.createVerticalStrut(12));
            contentPanel.add(username);
            contentPanel.add(Box.createVerticalStrut(12));
            contentPanel.add(password);
            contentPanel.add(Box.createVerticalStrut(28));
            contentPanel.add(startButton);
            contentPanel.add(Box.createVerticalStrut(4));
            contentPanel.add(createAccountLabel());

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

                    try {
                        AuthService authService = new AuthService();
                        User user = authService.login(userText, passwordText);

                        if (user != null) {
                            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(LoginContentPanel.this);
                            MainAppFrame mainFrame = new MainAppFrame();
                            mainFrame.setVisible(true);
                            topFrame.dispose();
                        } else {
                            JOptionPane.showMessageDialog(
                                LoginContentPanel.this,
                                "Invalid username or password.",
                                "Login Failed",
                                JOptionPane.ERROR_MESSAGE
                                );
                        }
                    } catch (Exception ex) {
                        javax.swing.JOptionPane.showMessageDialog(
                                LoginContentPanel.this,
                                "Login failed: " + ex.getMessage(),
                                "Error",
                                javax.swing.JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            });

            return buttons;
        }

    private JLabel createAccountLabel() {
        JLabel label = new JLabel("Don't have an account? Let's Create one!");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(fontFactory.createFont("dunbartall_book", 12));
        label.setForeground(new Color(0x717171));

        // Hover + click behavior
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                label.setForeground(new Color(0x9A9A9A));
                label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setForeground(new Color(0x717171));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(LoginContentPanel.this);
                frame.dispose(); // close the current login window
                new CreateWindow();  // open the new one
            }

        });

        return label;
    }


}

