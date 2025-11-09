package com.mycompany.vibra.musicUtilities;

import com.mycompany.vibra.model.Track;

import javax.swing.*;
import java.io.File;
import java.util.List;

public class fileChooser {
    public static List<Track> chooseFolder(JFrame parentComponent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Select Music Folder");

        int option = chooser.showOpenDialog(parentComponent);
        if (option == JFileChooser.APPROVE_OPTION) {
            File folder = chooser.getSelectedFile();
            List<Track> tracks = TrackLoader.loadTracks(folder.getAbsolutePath());

            if (tracks.isEmpty()) {
                JOptionPane.showMessageDialog(parentComponent,
                        "No MP3 files found in this folder.",
                        "Empty Folder",
                        JOptionPane.WARNING_MESSAGE);
            }
            return tracks;
        } else {
            return List.of(); // empty list if cancelled
        }
    }
}
