package com.github.us_aito.image_select_viewer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import java.awt.BorderLayout;
import java.awt.Dimension;

import com.github.us_aito.image_select_viewer.ImageLoader;

public class MainFrame {
  public static JFrame createMainFrame(String title) {
    JFrame frame = new JFrame(title);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setBounds(300, 80, 500, 600);

    DefaultListModel model = new DefaultListModel();
    JList imageList = new JList(model);

    JScrollPane sp = new JScrollPane();
    sp.getViewport().setView(imageList);
    sp.setPreferredSize(new Dimension(200, 800));

    JPanel panel = new JPanel();
    panel.add(sp);

    frame.getContentPane().add(panel, BorderLayout.CENTER);

    ImageLoader il = new ImageLoader("/Users/yu/git/java_labo/image_select_viewer_aid/src/main/resources", model);
    il.execute();

    return frame;
  }
}
