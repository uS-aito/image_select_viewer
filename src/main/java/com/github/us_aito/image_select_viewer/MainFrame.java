package com.github.us_aito.image_select_viewer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;

import com.github.us_aito.image_select_viewer.ImageLoader;

public class MainFrame {
  public static JFrame createMainFrame(String title) {
    // 本体フレームの作成
    JFrame frame = new JFrame(title);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setBounds(300, 80, 500, 600);

    // メインパネルを作成しメインフレームに追加
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    frame.getContentPane().add(panel, BorderLayout.CENTER);

    // サムネイルリストのスクロールペイン作成しメインパネルに追加
    JScrollPane thumbnailPane = ThumbnailList.createThumbnailListPanel("/Users/yu/git/java_labo/image_select_viewer_aid/src/main/resources");
    panel.add(thumbnailPane, BorderLayout.LINE_START);

    return frame;
  }
}
