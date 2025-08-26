package mainPanel;


import com.mycompany.vibra.utilities.fontLoader;
import com.mycompany.vibra.utilities.trackLists;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class TrackListPanel extends JPanel {
      ArrayList<trackLists> trackList = new ArrayList<>();

    public TrackListPanel() {
        setBackground(new Color(0x100D0D));
        setLayout(new BorderLayout()); // use BorderLayout for left alignment

// Container for vertical stacking
        JPanel trackPanel = new JPanel();
        trackPanel.setLayout(new BoxLayout(trackPanel, BoxLayout.Y_AXIS));
        trackPanel.setOpaque(false); // transparent so background shows
        trackPanel.setBorder(BorderFactory.createEmptyBorder(32,12,0,0));

// "Track List" label
        JLabel track = new JLabel("Track List");
        track.setFont(fontLoader.loadFont("/resources/DunbarTall-Bold.ttf", 44f));
        track.setForeground(new Color(0xF9F6EE));
        track.setAlignmentX(LEFT_ALIGNMENT); // important for BoxLayout
        trackPanel.add(track);

        trackPanel.add(Box.createVerticalStrut(8));

// "Playing next:" label
        JLabel play = new JLabel("Playing next:");
        play.setFont(fontLoader.loadFont("/resources/DunbarTall-Bold.ttf", 20f));
        play.setForeground(new Color(0xF9F6EE));
        play.setAlignmentX(LEFT_ALIGNMENT);
        trackPanel.add(play);

// Vertical strut of 32px
        trackPanel.add(Box.createVerticalStrut(20));
// Add track list buttons


// Add to main panel, aligned left
        add(trackPanel, BorderLayout.WEST);
    }
}
