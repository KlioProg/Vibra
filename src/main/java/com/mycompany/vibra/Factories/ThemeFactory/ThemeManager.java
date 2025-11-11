package com.mycompany.vibra.Factories.ThemeFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ThemeManager {
    private static ThemeManager instance;
    private boolean darkMode = true;

    private final List<ThemeChangerListener> listeners = new ArrayList<>();

    // Light theme colors
    private final Color lightSidebar = Color.decode("#DADDD8");
    private final Color lightTrackAlbum = Color.decode("#ECEBE4");
    private final Color lightMusicPanel = Color.decode("#F8F9FA");
    private final Color lightForeground = Color.decode("#100D0D");
    private final Color lightContainer = Color.decode("#FFFFFF");
    private final Color lightCardHover = new Color(0xD9B1F1); // hover on album
    private final Color lightSecondaryForeground = new Color(157,78,221);// color of # of songs in album

    // Dark theme colors
    private final Color darkSidebar = Color.decode("#010101");
    private final Color darkTrackAlbum = Color.decode("#100D0D");
    private final Color darkMusicPanel = Color.decode("#2B2C28");
    private final Color darkForeground = Color.WHITE;
    private final Color darkContainer = new Color(48, 48, 48); // the backdrop panel
    private final Color darkCardHover = new Color(0xD9B1F1); // hover on album in dark mode
    private final Color darkSecondaryForeground = new Color(157,78,221); //color of # of songs in album

    // Accent color (stays constant)
    private final Color accentPurple = new Color(138, 43, 226);

    private ThemeManager() {}

    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    public void toggleDarkMode() {
        darkMode = !darkMode;
        notifyListeners();
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
        notifyListeners();
    }

    // Color getters
    public Color getSidebarColor() {
        return darkMode ? darkSidebar : lightSidebar;
    }

    public Color getTrackAlbumColor() {
        return darkMode ? darkTrackAlbum : lightTrackAlbum;
    }

    public Color getMusicPanelColor() {
        return darkMode ? darkMusicPanel : lightMusicPanel;
    }

    public Color getForegroundColor() {
        return darkMode ? darkForeground : lightForeground;
    }

    public Color getAccentColor() {
        return accentPurple;
    }

    public Color getContainerColor() {
        return darkMode ? darkContainer : lightContainer;
    }

    public Color getCardHoverColor() {
        return darkMode ? darkCardHover : lightCardHover;
    }

    public Color getSecondaryForegroundColor() {
        return darkMode ? darkSecondaryForeground : lightSecondaryForeground;
    }

    // Theme listeners
    public void addThemeChangerListener(ThemeChangerListener listener) {
        if (!listeners.contains(listener)) listeners.add(listener);
    }

    public void removeThemeChangerListener(ThemeChangerListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (ThemeChangerListener listener : listeners) {
            listener.onThemeChanged(darkMode);
        }
    }

    public interface ThemeChangerListener {
        void onThemeChanged(boolean isDarkMode);
    }
}
