/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vibra.Content.Main_Page.Main_Contents.Like_Panel;

import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.DunbarFactory;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.FontFactory;

import java.awt.*;
import javax.swing.*;

/**
 *
 * @author robbi
 */
public class LikedSongsPanel extends JPanel {

    private JPanel songsListPanel;
    
    FontFactory fontFactory = new DunbarFactory();

    public LikedSongsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(0, 0, 0, 0));

       
        JLabel header = new JLabel("Liked Songs");
        header.setFont(fontFactory.createFont("dunbartall_book", 60));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(header, BorderLayout.NORTH);

        
        songsListPanel = new JPanel();
        songsListPanel.setLayout(new BoxLayout(songsListPanel, BoxLayout.Y_AXIS));
        songsListPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(songsListPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);

        add(scrollPane, BorderLayout.CENTER);

        addColumnHeaders();
        loadLikedSongs();
    }

    private void addColumnHeaders() {
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBackground(new Color(0x2B2C28));
        headerPanel.setPreferredSize(new Dimension(900, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.weighty = 1;
        gbc.insets = new Insets(5, 10, 5, 10);

        double[] colWeights = {0.1, 0.5, 0.25, 0.15};

        JLabel indexHeader = createHeaderLabel("#", SwingConstants.CENTER);
        JLabel titleHeader = createHeaderLabel("Title", SwingConstants.CENTER);
        JLabel artistHeader = createHeaderLabel("Artist", SwingConstants.CENTER);
        JLabel durationHeader = createHeaderLabel("Duration", SwingConstants.CENTER);

        gbc.gridx = 0; gbc.weightx = colWeights[0]; headerPanel.add(indexHeader, gbc);
        gbc.gridx = 1; gbc.weightx = colWeights[1]; headerPanel.add(titleHeader, gbc);
        gbc.gridx = 2; gbc.weightx = colWeights[2]; headerPanel.add(artistHeader, gbc);
        gbc.gridx = 3; gbc.weightx = colWeights[3]; headerPanel.add(durationHeader, gbc);

        songsListPanel.add(headerPanel);
    }

    private void loadLikedSongs() {
        addSongEntry("Pasilyo", "Sunkissed Lola", "4:30");
        addSongEntry("Pagtingin", "Ben & Ben", "3:47");
        addSongEntry("Maybe Maybe", "Lola Amour", "5:13");
    }

    private void addSongEntry(String title, String artist, String duration) {
        JPanel songRow = new JPanel(new GridBagLayout());
        songRow.setBackground(new Color(0x2B2C28));
        songRow.setPreferredSize(new Dimension(900, 70));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.weighty = 1;
        gbc.insets = new Insets(5, 10, 5, 10);

        double[] colWeights = {0.1, 0.5, 0.25, 0.15};

        int index = songsListPanel.getComponentCount() ; 
        JLabel indexLabel = createCellLabel(String.valueOf(index), Color.WHITE, true, SwingConstants.CENTER);

        // Column  Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        titlePanel.setOpaque(false);

        JPanel coverPlaceholder = new JPanel();
        coverPlaceholder.setPreferredSize(new Dimension(200, 200));
        coverPlaceholder.setBackground(new Color(138, 43, 226));
        coverPlaceholder.setBorder(BorderFactory.createLineBorder(new Color(138, 43, 226), 1, true));

        JLabel titleLabel = createCellLabel(title, Color.WHITE, true, SwingConstants.LEFT);
        titlePanel.add(coverPlaceholder);
        titlePanel.add(titleLabel);

        JLabel artistLabel = createCellLabel(artist, Color.LIGHT_GRAY, false, SwingConstants.CENTER);
        JLabel durationLabel = createCellLabel(duration, Color.WHITE, false, SwingConstants.CENTER);

        gbc.gridx = 0; gbc.weightx = colWeights[0]; songRow.add(indexLabel, gbc);
        gbc.gridx = 1; gbc.weightx = colWeights[1]; songRow.add(titlePanel, gbc);
        gbc.gridx = 2; gbc.weightx = colWeights[2]; songRow.add(artistLabel, gbc);
        gbc.gridx = 3; gbc.weightx = colWeights[3]; songRow.add(durationLabel, gbc);

        songsListPanel.add(songRow);
    }

    // 
    private JLabel createHeaderLabel(String text, int align) {
        JLabel lbl = new JLabel(text, align);
        lbl.setForeground(new Color(138, 43, 226));
        lbl.setFont(fontFactory.createFont("dunbartall_book", 20));
        return lbl;
    }

    private JLabel createCellLabel(String text, Color color, boolean bold, int align) {
        JLabel lbl = new JLabel(text, align);
        lbl.setForeground(color);
        lbl.setFont(fontFactory.createFont("dunbartall_book", 20));
        return lbl;
    }
}
