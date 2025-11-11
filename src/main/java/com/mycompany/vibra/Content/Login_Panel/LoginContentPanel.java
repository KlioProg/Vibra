package com.mycompany.vibra.Content.Login_Panel;

import com.mycompany.vibra.Content.Create_Account.CreateWindow;
import com.mycompany.vibra.Content.ErrorPanel.CreateErrorPanel;
import com.mycompany.vibra.Content.MainAppFrame;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.DunbarFactory;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.FontFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.ButtonIconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.CommonIconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.IconFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundPadderFactory;
import com.mycompany.vibra.Factories.Common_UI.RoundedPanelFactory;
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
        IconFactory icon = new CommonIconFactory();
        private RoundedPanelFactory errorPanel; // <-- Make it a field

    public LoginContentPanel() {
            setOpaque(false);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.iconFactory = new ButtonIconFactory();
            this.errorPanel = createErrorPanel();


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
            contentPanel.add(Box.createVerticalStrut(12));
            contentPanel.add(this.errorPanel); // Add it to your layout
            errorPanel.setVisible(false);
            contentPanel.add(Box.createVerticalStrut(12));
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

    /**
     * Creates and configures a standardized error message panel.
     * Assumes 'iconFactory' and 'fontFactory' are available as class fields.
     */
    private RoundedPanelFactory createErrorPanel() {
        Color errorBackground = new Color(0xF9F6EE);
        Color errorBorder = new Color(0xC1121F);
        Color errorText = new Color(0xC1121F);

        RoundedPanelFactory errorPanel = new RoundedPanelFactory(
                16,                      // Corner radius
                errorBackground,         // Background color
                errorBorder,             // Border color
                1,                       // Border thickness (using 1 from your example)
                350,                     // Width
                44                       // Height
        );
        errorPanel.addIconWithText(
                icon.createIcon("error"),
                "Your Email or Password is incorrect!",
                fontFactory.createFont("dunbartall_book", 16),
                errorText
        );
        errorPanel.setVisible(false);

        return errorPanel;
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
                            // --- SUCCESS ---
                            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(LoginContentPanel.this);

                            int loggedInUserID = user.getId();
                            MainAppFrame mainFrame = new MainAppFrame(loggedInUserID);

                            mainFrame.setVisible(true);
                            topFrame.dispose();
                        } else {
                            // --- INVALID CREDENTIALS ---
                            errorPanel.setVisible(true);
                        }

                    } catch (Exception ex) {
                        // --- CRITICAL SYSTEM ERROR ---
                        ex.printStackTrace();

                        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(LoginContentPanel.this);
                        if (frame != null) {
                            JPanel newPanel = new CreateErrorPanel();
                            frame.getContentPane().removeAll();
                            frame.getContentPane().add(newPanel);
                            frame.revalidate();
                            frame.repaint();
                        }
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