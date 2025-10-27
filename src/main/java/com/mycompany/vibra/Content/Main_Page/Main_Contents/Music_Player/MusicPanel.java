package com.mycompany.vibra.Content.Main_Page.Main_Contents.Music_Player;

import com.mycompany.vibra.Content.Main_Page.Main_Contents.TrackLists.TrackListPanel;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.IconFactory;
import com.mycompany.vibra.musicUtilities.AudioPlayer;
import com.mycompany.vibra.model.Playlist;
import com.mycompany.vibra.musicUtilities.Track;

import javax.swing.*;
import java.awt.*;

public class MusicPanel extends JPanel {

    private AudioPlayer audioPlayer;
    private MusicPlayerPanel musicPlayerPanel;
    private Playlist mainPlaylist;


    public MusicPanel(IconFactory iconFactory) {
        setLayout(new BorderLayout());

        // Shared AudioPlayer instance
        audioPlayer = new AudioPlayer();
        //placeholder playlist
        mainPlaylist = new Playlist(1, "My Playlist", 1); // (Example ID, name, user ID)

        
        // Left: Track list
        TrackListPanel trackListPanel = new TrackListPanel();
        trackListPanel.setPreferredSize(new Dimension(332, 0));
        add(trackListPanel, BorderLayout.WEST);

        // Center: Music player panel (share AudioPlayer ✅)
        musicPlayerPanel = new MusicPlayerPanel(audioPlayer, mainPlaylist);
        musicPlayerPanel.setPreferredSize(new Dimension(610, 0));
        add(musicPlayerPanel, BorderLayout.CENTER);

        // Right: Main library (pass MusicPlayerPanel ✅)
        MainLibraryPanel mainLibraryPanel = new MainLibraryPanel(musicPlayerPanel);
        mainLibraryPanel.setPreferredSize(new Dimension(402, 0));
        add(mainLibraryPanel, BorderLayout.EAST);
    }

    // Optional: expose player panel for other panels to load tracks
    public MusicPlayerPanel getMusicPlayerPanel() {
        return musicPlayerPanel;
    }

    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }
}
