package com.mycompany.vibra.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.mycompany.vibra.utilities.RoundedButton;
import com.mycompany.vibra.utilities.RoundedTextField;
import com.mycompany.vibra.utilities.fontLoader;
import com.mycompany.vibra.utilities.roundBorder;


public class createAccPanel extends JPanel {

    private RoundedTextField username;
    private RoundedTextField email;
    private RoundedTextField phone;
    private RoundedTextField password;

    public createAccPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Top glue
        add(Box.createVerticalGlue());

        // Login panel with rounded border
        roundBorder panel = new roundBorder(50); // Ensure this class exists
        panel.setPreferredSize(new Dimension(480, 650));
        panel.setBackground(new Color(0x100D0D));
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
        contentPanel.setPreferredSize(new Dimension(400, 560));
        contentPanel.setBackground(new Color(0x100D0D));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Logo
        ImageIcon icon = new ImageIcon("D:\\Academics\\Vibra\\src\\main\\resources\\images\\vibra.png");
        JLabel imageLabel = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(180, 176, Image.SCALE_SMOOTH)));
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title
        JLabel label1 = new JLabel("Get Grooving");
        label1.setAlignmentX(Component.CENTER_ALIGNMENT);
        label1.setFont(fontLoader.loadFont("/fonts/DunbarTall-Bold.ttf", 28f));
        label1.setForeground(Color.WHITE);

        // Subtitle
        JLabel label2 = new JLabel("by creating a free account");
        label2.setAlignmentX(Component.CENTER_ALIGNMENT);
        label2.setFont(fontLoader.loadFont("/fonts/DunbarTall-Book.ttf", 16f));
        label2.setForeground(Color.WHITE);



        username = textField("Username");
        email = textField("Valid Email");
        phone = textField("Phone Number");
        password = textField("Strong Password");

        RoundedButton startButton = button("Start the Vibe!");

        contentPanel.add(Box.createVerticalStrut(0));
        contentPanel.add(imageLabel);
        contentPanel.add(Box.createVerticalStrut(0));
        contentPanel.add(label1);
        contentPanel.add(Box.createVerticalStrut(0));
        contentPanel.add(label2);
        contentPanel.add(Box.createVerticalStrut(4));
        contentPanel.add(username);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(email);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(phone);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(password);
        contentPanel.add(Box.createVerticalStrut(62));
        contentPanel.add(startButton);

        return contentPanel;
    }

    private RoundedTextField textField(String placeholder) {
        RoundedTextField textField = new RoundedTextField(40); // your custom class
        textField.setPreferredSize(new Dimension(350, 44));
        textField.setAlignmentX(Component.CENTER_ALIGNMENT);
        textField.setBackground(new Color(0xF9F6EE));
        textField.setForeground(new Color(0x100D0D));
        textField.setFont(fontLoader.loadFont("/fonts/DunbarTall-Book.ttf", 16f));
        textField.setPlaceholder(placeholder);
        return textField;
}


    private RoundedButton button(String text){
        RoundedButton buttons = new RoundedButton(text,40);
        buttons.setMaximumSize(new Dimension(350, 44));
        buttons.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttons.setBackground(new Color(0x9D4EDD));
        buttons.setForeground(new Color(0xF9F6EE));
        buttons.setFont(fontLoader.loadFont("/fonts/DunbarTall-Book.ttf", 24f));

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
                buttons.setBackground(new Color(0x5A189A)); // darker color when pressed
                buttons.setForeground(new Color(0x9D4EDD)); // optional
                //save method
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                buttons.setBackground(new Color(0x7B2CBF));
                buttons.setForeground(new Color(0xF9F6EE));

                String userText = username.getText();
                String emailText = email.getText();
                String phoneText = phone.getText();
                String passwordText = password.getText();

//                Insert into database only if not duplicate
//                boolean success = DatabaseHelper.insertUserIfNew(userText, emailText, phoneText, passwordText);

//                if (success) {
//                    // Open MainFrame from different package
                    JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(createAccPanel.this);
                    mainPanel mainFrame = new mainPanel();
                    mainFrame.setVisible(true);
                    topFrame.dispose();
//                }

            }
        });

        return buttons;
    }
}
