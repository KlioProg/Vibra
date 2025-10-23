package com.mycompany.vibra.Content.Main_Page.Main_Contents.TrackLists;

import com.mycompany.vibra.Factories.Common_UI.FontLoaderFactory;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class TrackList extends JButton {
    private JLabel trackNumberLabel;

    public TrackList(int trackNumber, ImageIcon albumArtIcon, String songname, String artistname, String duration) {
        super();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setPreferredSize(new Dimension(282, 44));
        setBackground(new Color(0x100D0D));
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder());

        // Track number
        trackNumberLabel = new JLabel(String.format("%02d", trackNumber));
        trackNumberLabel.setFont(FontLoaderFactory.loadFont("/fonts/DunbarTall-Bold.ttf", 14f));
        trackNumberLabel.setForeground(new Color(0xF9F6EE));
        trackNumberLabel.setPreferredSize(new Dimension(30, 20));
        trackNumberLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 5));
        add(trackNumberLabel);

        // Album art (now comes in as an ImageIcon already scaled)
        JLabel songLabel = new JLabel(albumArtIcon);
        songLabel.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
        add(songLabel);

        // Text panel
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel songNameLabel = new JLabel(songname);
        songNameLabel.setFont(FontLoaderFactory.loadFont("/fonts/DunbarTall-Bold.ttf", 14f));
        songNameLabel.setForeground(Color.WHITE);
        textPanel.add(songNameLabel);
        textPanel.add(Box.createVerticalStrut(2));

        JLabel artistNameLabel = new JLabel(artistname);
        artistNameLabel.setFont(FontLoaderFactory.loadFont("/fonts/DunbarTall-Book.ttf", 12f));
        artistNameLabel.setForeground(new Color(0xF9F6EE));
        textPanel.add(artistNameLabel);
        add(textPanel);

        // Song duration
        JLabel songDurationLabel = new JLabel(duration);
        songDurationLabel.setForeground(new Color(0xF9F6EE));
        songDurationLabel.setFont(FontLoaderFactory.loadFont("/fonts/DunbarTall-Book.ttf", 14f));
        songDurationLabel.setPreferredSize(new Dimension(50, 20));
        songDurationLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(songDurationLabel);

        // Transparent button
        setContentAreaFilled(false);
        setOpaque(false);
    }

    public void updateTrackNumber(int number) {
        trackNumberLabel.setText(String.format("%02d", number));
    }
}
