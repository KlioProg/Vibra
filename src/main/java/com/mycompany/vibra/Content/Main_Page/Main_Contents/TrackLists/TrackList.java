package com.mycompany.vibra.Content.Main_Page.Main_Contents.TrackLists;

import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.DunbarFactory;
import com.mycompany.vibra.Factories.Common_UI.FontFactory_FactoryMethod.FontFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.DarkModeIconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.IconFactory;
import com.mycompany.vibra.Factories.Common_UI.IconFactory_FactoryMethod.LightModeIconFactory;
import com.mycompany.vibra.Factories.ThemeFactory.ThemeManager;
import com.mycompany.vibra.musicUtilities.AudioPlayer;
import com.mycompany.vibra.model.Track;

import java.awt.*;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class TrackList extends JButton implements ThemeManager.ThemeChangerListener {

    // --- Fields ---
    private Track track; // Holds all the track data
    private static AudioPlayer audioPlayer; // For playing the song

    // --- UI Components that need to change color ---
    private static IconFactory themeIcons;
    private ImageIcon defaultCover;
    private JLabel trackNumberLabel;
    private JLabel songLabel; // For album art
    private JLabel songNameLabel;
    private JLabel artistNameLabel;
    private JLabel songDurationLabel;
    FontFactory fontFactory = new DunbarFactory();

    /**
     * ✅ NEW CONSTRUCTOR
     * Creates a new TrackList component based on a Track data object.
     *
     * @param track       The Track object containing all metadata.
     * @param trackNumber The position of this track in the list (e.g., 1, 2, 3...).
     */
    public TrackList(Track track, int trackNumber) {
        super();
        this.track = track;

        // --- Setup the button itself ---
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setPreferredSize(new Dimension(282, 44));
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder());
        setContentAreaFilled(false);
        setOpaque(false);

        // --- Build the UI components ---
        trackLoader(trackNumber);

        // --- Setup Theme and Audio ---
        ThemeManager.getInstance().addThemeChangerListener(this);
        applyTheme(ThemeManager.getInstance().isDarkMode());

        // Action: play when clicked
        addActionListener(e -> {
            if (audioPlayer != null) {
                audioPlayer.play(track);
            }
        });
    }

    /**
     * This method builds your EXACT original UI,
     * but pulls data from the 'track' object.
     */
    private void trackLoader(int trackNumber) {
        // Track number
        trackNumberLabel = new JLabel(String.format("%02d", trackNumber));
        trackNumberLabel.setFont(fontFactory.createFont("dunbartall_bold", 14));
        trackNumberLabel.setPreferredSize(new Dimension(30, 20));
        trackNumberLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 5));
        add(trackNumberLabel);

        // Album art (JLabel for the icon)
        songLabel = new JLabel(); // Icon is set in applyTheme
        songLabel.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
        add(songLabel);

        // Text panel
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        // ✅ Get title from track object
        songNameLabel = new JLabel(track.getTitle());
        songNameLabel.setFont(fontFactory.createFont("dunbartall_bold", 14));
        textPanel.add(songNameLabel);
        textPanel.add(Box.createVerticalStrut(2));

        // ✅ Get artist from track object
        artistNameLabel = new JLabel(track.getArtist());
        artistNameLabel.setFont(fontFactory.createFont("dunbartall_book", 12));
        textPanel.add(artistNameLabel);
        add(textPanel);

        // Song duration
        // ✅ Get duration from track object and format it
        songDurationLabel = new JLabel(formatDuration(track.getDuration()));
        songDurationLabel.setFont(fontFactory.createFont("dunbartall_book", 14));
        songDurationLabel.setPreferredSize(new Dimension(50, 20));
        songDurationLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(songDurationLabel);
    }

    private String queueLoader(){

        // TODO: Make a queue loader in order to be integrated in the TrackLoader and will be shown as a track not as a song only.
        return "Queue";

    }

    /**
     * Formats total seconds into a "M:SS" string.
     */
    private String formatDuration(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    /**
     * Updates this component's colors and icons based on the theme.
     */
    private void applyTheme(boolean isDarkMode) {
        this.themeIcons = isDarkMode ? new DarkModeIconFactory() : new LightModeIconFactory();
        // Make sure you have an icon named "default_cover" in your factory
        this.defaultCover = themeIcons.createIcon("default_cover");

        // Update Album Art
        ImageIcon albumArtIcon;
        Image art = track.getAlbumArtImage();
        if (art != null) {
            albumArtIcon = new ImageIcon(art.getScaledInstance(35, 35, Image.SCALE_SMOOTH));
        } else {
            // Use the theme-appropriate default cover
            albumArtIcon = new ImageIcon(defaultCover.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH));
        }
        songLabel.setIcon(albumArtIcon);

        // Update Text Colors (using your original colors for dark mode)
        if (isDarkMode) {
            trackNumberLabel.setForeground(new Color(0xF9F6EE));
            songNameLabel.setForeground(Color.WHITE);
            artistNameLabel.setForeground(new Color(0xF9F6EE)); // Your original color
            songDurationLabel.setForeground(new Color(0xF9F6EE));
            setBackground(new Color(0x100D0D));
        } else {
            // Example light mode colors
            trackNumberLabel.setForeground(new Color(0x333333));
            songNameLabel.setForeground(Color.BLACK);
            artistNameLabel.setForeground(new Color(0x555555));
            songDurationLabel.setForeground(new Color(0x333333));
            setBackground(new Color(0xFAFAFA));
        }
    }

    /**
     * This is called by the ThemeManager when the theme changes.
     */
    @Override
    public void onThemeChanged(boolean isDarkMode) {
        applyTheme(isDarkMode);
    }

    /**
     * Allows the track number to be updated if the list is re-ordered.
     */
    public void updateTrackNumber(int number) {
        trackNumberLabel.setText(String.format("%02d", number));
    }

    /**
     * Sets the static AudioPlayer instance for all TrackList components.
     */
    public static void setAudioPlayer(AudioPlayer player) {
        audioPlayer = player;
    }
}