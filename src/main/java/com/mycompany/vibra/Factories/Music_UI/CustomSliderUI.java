package com.mycompany.vibra.Factories.Music_UI;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;

public class CustomSliderUI extends BasicSliderUI {
    private final Color thumbColor;

    public CustomSliderUI(JSlider slider, Color thumbColor) {
        super(slider);
        this.thumbColor = thumbColor;
    }

    @Override
    public void paintTrack(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int trackHeight = 4;
        int trackTop = (trackRect.height - trackHeight) / 2;

        // Background track
        g2.setColor(new Color(60, 60, 60));
        g2.fillRoundRect(trackRect.x, trackRect.y + trackTop, trackRect.width, trackHeight, trackHeight, trackHeight);

        // Progress track
        int progressWidth = (int) ((double) (slider.getValue() - slider.getMinimum()) /
                (slider.getMaximum() - slider.getMinimum()) * trackRect.width);
        g2.setColor(thumbColor);
        g2.fillRoundRect(trackRect.x, trackRect.y + trackTop, progressWidth, trackHeight, trackHeight, trackHeight);
    }

    @Override
    public void paintThumb(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(thumbColor);
        g2.fillOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
    }

    @Override
    protected Dimension getThumbSize() {
        return new Dimension(12, 12);
    }
}