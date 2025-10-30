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
    // We need to store these to pass them
    private TrackListPanel trackListPanel;
    private MainLibraryPanel mainLibraryPanel;
    private Playlist mainPlaylist;


    public MusicPanel(IconFactory iconFactory) {
        setLayout(new BorderLayout());

        // ✅ 1. Create the ONE shared AudioPlayer
        audioPlayer = new AudioPlayer();
        //placeholder playlist
        mainPlaylist = new Playlist(1, "My Playlist", 1); // (Example ID, name, user ID)

        // ✅ 2. Create the TrackListPanel and GIVE it the player
        trackListPanel = new TrackListPanel(audioPlayer);
        trackListPanel.setPreferredSize(new Dimension(332, 0));
        add(trackListPanel, BorderLayout.WEST);

        // ✅ 3. Create the MusicPlayerPanel and GIVE it the player
        musicPlayerPanel = new MusicPlayerPanel(audioPlayer, mainPlaylist);
        
        // Left: Track list
        TrackListPanel trackListPanel = new TrackListPanel(audioPlayer);
        trackListPanel.setPreferredSize(new Dimension(332, 0));
        add(trackListPanel, BorderLayout.WEST);

        // Center: Music player panel (share AudioPlayer ✅)
        musicPlayerPanel = new MusicPlayerPanel(audioPlayer, mainPlaylist);
        musicPlayerPanel.setPreferredSize(new Dimension(610, 0));
        add(musicPlayerPanel, BorderLayout.CENTER);

        // ✅ 4. Create the MainLibraryPanel and GIVE it the other two panels
        mainLibraryPanel = new MainLibraryPanel(musicPlayerPanel, trackListPanel);
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