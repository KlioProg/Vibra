package com.mycompany.vibra.panels;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.*;

/**
 *
 * @author robbi
 */

public class VibraLoginPanel extends JPanel {

    public VibraLoginPanel(){
        // Center login panel - fixed position in center
        JPanel loginPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 30, 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50);
                g2.dispose();
            }
        };

        loginPanel.setOpaque(false);
        loginPanel.setBounds(0, 0, 487, 648);
        loginPanel.setLayout(null);


        // Logo at top
        ImageIcon icon = new ImageIcon("Vibra_Logo.png");
        // Scale logo to 86x86
        if (icon.getIconWidth() != -1) {
            Image img = icon.getImage();
            Image scaledImg = img.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            icon = new ImageIcon(scaledImg);
        }
        JLabel logo = new JLabel(icon);
        if (icon.getIconWidth() == -1) {
            logo.setText("Vibra");
            logo.setForeground(Color.WHITE);
            logo.setFont(new Font("Dunbar Tall", Font.BOLD, 24));
        }
        logo.setBounds(0, 50, 487, 100);
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        loginPanel.add(logo);

        // "Login to continue" text
        JLabel loginText = new JLabel("Login to continue.");
        loginText.setForeground(Color.WHITE);
        loginText.setFont(new Font("Dunbar Text", Font.BOLD, 20));
        loginText.setBounds(0, 180, 487, 30);
        loginText.setHorizontalAlignment(SwingConstants.CENTER);
        loginPanel.add(loginText);

        // Username field with placeholder
        JTextField usernameField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(245, 245, 245));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        usernameField.setOpaque(false);
        usernameField.setFont(new Font("Dunbar Tall", Font.PLAIN, 20));
        usernameField.setForeground(Color.GRAY);
        usernameField.setText("Username");
        usernameField.setBounds(85, 250, 320, 45);
        usernameField.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        usernameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (usernameField.getText().equals("Username")) {
                    usernameField.setText("");
                    usernameField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (usernameField.getText().isEmpty()) {
                    usernameField.setText("Username");
                    usernameField.setForeground(Color.GRAY);
                }
            }
        });
        loginPanel.add(usernameField);

        // Password field with placeholder
        JTextField passwordField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(245, 245, 245));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        passwordField.setOpaque(false);
        passwordField.setFont(new Font("Dunbar Tall", Font.PLAIN, 20));
        passwordField.setForeground(Color.GRAY);
        passwordField.setText("Password");
        passwordField.setBounds(85, 320, 320, 45);
        passwordField.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (passwordField.getText().equals("Password")) {
                    passwordField.setText("");
                    passwordField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (passwordField.getText().isEmpty()) {
                    passwordField.setText("Password");
                    passwordField.setForeground(Color.GRAY);
                }
            }
        });
        loginPanel.add(passwordField);

        // Remember me checkbox
        JCheckBox rememberMe = new JCheckBox("Remember me");
        rememberMe.setFont(new Font ("Dunbar Tall", Font.PLAIN, 20));
        rememberMe.setForeground(Color.WHITE);
        rememberMe.setOpaque(false);
        rememberMe.setBounds(85, 390, 320, 25);
        loginPanel.add(rememberMe);

        // Login button with rounded corners, hover, and press effects
        JButton loginButton = new JButton("Start the vibe!") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(new Color(108, 13, 196)); // Pressed color
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(168, 73, 236)); // Hover color
                } else {
                    g2.setColor(new Color(138, 43, 226)); // Default color
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override
            public void updateUI() {
                super.updateUI();
                setBorderPainted(false); // Ensure no default border
                setFocusPainted(false);  // Remove focus rectangle
                setContentAreaFilled(false); // Ensure custom painting
            }
        };
        loginButton.setBounds(85, 450, 320, 50);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Dunbar Tall", Font.BOLD, 30));
        loginButton.setOpaque(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setBorderPainted(false);
        loginPanel.add(loginButton);

        // Forgot password
        JLabel forgotPassword = new JLabel("Forgot password?");
        forgotPassword.setForeground(Color.GRAY);
        forgotPassword.setFont(new Font("Dunbar Tall", Font.PLAIN, 12));
        forgotPassword.setBounds(0, 530, 487, 20);
        forgotPassword.setHorizontalAlignment(SwingConstants.CENTER);
        loginPanel.add(forgotPassword);
        // Prevent auto-focus on username
        setFocusTraversalPolicy(new DefaultFocusTraversalPolicy() {
            @Override
            public Component getInitialComponent(Window w) {
                return loginButton; // Set initial focus to login button
            }
        });
    }
}
