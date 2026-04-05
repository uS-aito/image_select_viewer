package com.github.us_aito.image_select_viewer;

import javax.swing.JFrame;

import com.github.us_aito.image_select_viewer.MainFrame;

public class Main{
    public static void main(String[] args) {
        JFrame frame = MainFrame.createMainFrame("Image Select Viewer");

        frame.setVisible(true);
    }
}