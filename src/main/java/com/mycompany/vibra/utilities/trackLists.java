package com.mycompany.vibra.utilities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class trackLists extends JButton {
    public trackLists(int trackNumber, String normalPath, String songname, String artistname, String duration) {
        super(); // Call JButton constructor
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS)); // Horizontal layout for the button
        setPreferredSize(new Dimension(282, 44));
        setBackground(new Color(0x100D0D));
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder());

        // Track number
        JLabel trackNumberLabel = new JLabel(String.format("%02d", trackNumber)); // Format to two digits
        trackNumberLabel.setFont(fontLoader.loadFont("/fonts/DunbarTall-Bold.ttf", 14f)); // Use the same font
        trackNumberLabel.setForeground(new Color(0xF9F6EE));
        trackNumberLabel.setPreferredSize(new Dimension(30, 20)); // Set a fixed width for consistent alignment
        trackNumberLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 5)); // Reduced padding

        add(trackNumberLabel); // Add track number label

        // Album art
        ImageIcon song = new ImageIcon(normalPath);
        JLabel songLabel = new JLabel(new ImageIcon(song.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
        songLabel.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4)); // Padding around the icon

        add(songLabel); // Add album art label

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false); // transparent

        JLabel songNameLabel = new JLabel(songname);
        songNameLabel.setFont(fontLoader.loadFont("/fonts/DunbarTall-Bold.ttf", 14f));
        songNameLabel.setForeground(Color.WHITE);
        textPanel.add(songNameLabel);


        textPanel.add(Box.createVerticalStrut(2));

        JLabel artistNameLabel = new JLabel(artistname);
        artistNameLabel.setFont(fontLoader.loadFont("/fonts/DunbarTall-Book.ttf", 12f));
        artistNameLabel.setForeground(new Color(0xF9F6EE));
        textPanel.add(artistNameLabel);

        add(textPanel);


        // Song duration
        JLabel songDurationLabel = new JLabel(duration);
        songDurationLabel.setForeground(new Color(0xF9F6EE));
        songDurationLabel.setFont(fontLoader.loadFont("/fonts/DunbarTall-Book.ttf", 14f));
        songDurationLabel.setPreferredSize(new Dimension(50, 20)); // Set a fixed width for consistent alignment
        songDurationLabel.setHorizontalAlignment(SwingConstants.RIGHT); // Align to the right

        add(songDurationLabel); // Add song duration label

        // Set button properties
        setContentAreaFilled(false); // Make button transparent
        setOpaque(false); // Make button transparent
    }

    // Method to add action listener
    public void addActionListener(ActionListener listener) {
        super.addActionListener(listener);
    }
}
