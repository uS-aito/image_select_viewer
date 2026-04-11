package com.github.us_aito.image_select_viewer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;

public class MainFrame {
  private static String imagePath = "/Users/yu/git/java_labo/image_select_viewer_aid/src/main/resources";

  public static JFrame createMainFrame(String title) {
    // 本体フレームの作成
    JFrame frame = new JFrame(title);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setBounds(300, 80, 500, 600);

    // メインパネルを作成しメインフレームに追加
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    frame.getContentPane().add(panel, BorderLayout.CENTER);

    // メインビューのラベルを作成しメインフレームに追加
    JLabel mainView = new JLabel();
    panel.add(mainView, BorderLayout.CENTER);

    // サムネイルリストのスクロールペイン作成しメインパネルに追加
    ThumbnailList thumbnailList = new ThumbnailList(imagePath);
    thumbnailList.addThumbnailSelectionListener(e -> {
      if (!e.getValueIsAdjusting()) {
        ImageFile selected = thumbnailList.getSelectedImageFile();
        if (selected != null) {
          mainView.setIcon(selected.thumbnail());
        }
      }
    });
    panel.add(thumbnailList.getThumbnailPane(), BorderLayout.LINE_START);

    // JScrollPane thumbnailPane = ThumbnailList.createThumbnailListPanel("/Users/yu/git/java_labo/image_select_viewer_aid/src/main/resources");
    // panel.add(thumbnailPane, BorderLayout.LINE_START);
    // JList imageList = (JList)thumbnailPane.getViewport().getView();
    // imageList.addListSelectionListener(e -> {
    //   if (!e.getValueIsAdjusting()) {
    //     ImageIcon selected = (ImageIcon)imageList.getSelectedValue();
    //     if (selected != null) {
    //       // mainViewを更新する処理
    //     }
    //   }
    // });

    return frame;
  }
}
