package com.mycompany.vibra.panels;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.*;

public class mainPanel extends JFrame {
    public mainPanel() {
        SwingUtilities.invokeLater(() -> {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Dimension screenSize = toolkit.getScreenSize();
            int screenWidth = screenSize.width;
            int screenHeight = screenSize.height;

            JFrame frame = new JFrame("Vibra");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(screenWidth, screenHeight);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);

            // Create a main panel with no gaps
            JPanel mainPanel = new JPanel(new BorderLayout(0, 0)); // 0 gaps

            // Left side panel (combine Account + TrackList)
            JPanel leftPanel = new JPanel(new BorderLayout(0, 0));
            AccountPanel accountPanel = new AccountPanel();

            accountPanel.setPreferredSize(new Dimension(100, screenHeight));
            TrackListPanel trackListPanel = new TrackListPanel();

            trackListPanel.setPreferredSize(new Dimension(320, screenHeight));
            leftPanel.add(accountPanel, BorderLayout.WEST);
            leftPanel.add(trackListPanel, BorderLayout.CENTER);

            // Center panel
            MusicPlayerPanel musicPlayerPanel = new MusicPlayerPanel();

            // Right panel
            LibraryPanel libraryPanel = new LibraryPanel();
            libraryPanel.setPreferredSize(new Dimension(388, screenHeight));

            mainPanel.add(leftPanel, BorderLayout.WEST);
            mainPanel.add(musicPlayerPanel, BorderLayout.CENTER);
            mainPanel.add(libraryPanel, BorderLayout.EAST);

            frame.add(mainPanel);
            frame.setVisible(true);
        });

    }

    public static void main(String[] args) {
        new mainPanel();

    }
}
