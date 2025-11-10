package com.mycompany.vibra.Content.Main_Page.Main_Contents.Music_Player;

import com.mycompany.vibra.Content.Main_Page.Main_Contents.Like_Panel.LikedPanel;
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
    private LikedPanel likedPanel;

    public MusicPanel(IconFactory iconFactory, LikedPanel likedPanel) {
        setLayout(new BorderLayout());
        this.likedPanel = likedPanel;

        // ✅ 1. Create the ONE shared AudioPlayer
        audioPlayer = new AudioPlayer();
        //placeholder playlist
        mainPlaylist = new Playlist(1, "My Playlist", 1); // (Example ID, name, user ID)
        
        // --- This is the key change ---
        // 1. Create the MusicPlayerPanel FIRST. It's the central component.
        musicPlayerPanel = new MusicPlayerPanel(audioPlayer, mainPlaylist, likedPanel);
        musicPlayerPanel.setPreferredSize(new Dimension(610, 0));
        
        // 2. Create the TrackListPanel and give it a reference to the MusicPlayerPanel.
        //    This is how the track list will tell the player what to play.
        trackListPanel = new TrackListPanel(musicPlayerPanel); // Pass the player panel, not the audio player
        trackListPanel.setPreferredSize(new Dimension(332, 0));
        
        // 3. Create the MainLibraryPanel, giving it the other two panels.
        mainLibraryPanel = new MainLibraryPanel(musicPlayerPanel, trackListPanel);
        mainLibraryPanel.setPreferredSize(new Dimension(402, 0));
        
        // 4. Add the final, wired-up components to the layout.
        add(trackListPanel, BorderLayout.WEST);
        add(musicPlayerPanel, BorderLayout.CENTER);
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