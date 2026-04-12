package com.github.us_aito.image_select_viewer;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = MainFrame.createMainFrame("ComfyUI Image Viewer");
            frame.setVisible(true);
        });
    }
}
