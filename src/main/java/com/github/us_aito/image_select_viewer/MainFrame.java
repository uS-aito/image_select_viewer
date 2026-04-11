package com.github.us_aito.image_select_viewer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ImageIcon;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;
import javax.imageio.ImageIO;

public class MainFrame {

  public static JFrame createMainFrame(String title, String imagePath) {
    JFrame frame = new JFrame(title);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setBounds(300, 80, 900, 600);

    // メインパネル（BorderLayout）
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    frame.getContentPane().add(panel, BorderLayout.CENTER);

    // 画像表示ラベルとスクロールペイン
    JLabel imageLabel = new JLabel();
    JScrollPane imageScrollPane = new JScrollPane(imageLabel);

    // 右ペイン（プロンプト表示用 JTextArea）
    JTextArea promptTextArea = new JTextArea();
    promptTextArea.setEditable(false);
    promptTextArea.setLineWrap(true);
    promptTextArea.setWrapStyleWord(true);
    JScrollPane promptScrollPane = new JScrollPane(promptTextArea);
    promptScrollPane.setPreferredSize(new Dimension(250, 0));
    promptScrollPane.setVisible(false);

    // トグルボタン
    JButton toggleButton = new JButton("\u25B6");
    toggleButton.setPreferredSize(new Dimension(20, 0));
    toggleButton.setFocusPainted(false);

    // トグルボタンを載せるパネル（中央ペイン右端）
    JPanel togglePanel = new JPanel(new BorderLayout());
    togglePanel.add(toggleButton, BorderLayout.CENTER);

    // 中央ラッパー（画像 + トグルボタン）
    JPanel centerWrapper = new JPanel(new BorderLayout());
    centerWrapper.add(imageScrollPane, BorderLayout.CENTER);
    centerWrapper.add(togglePanel, BorderLayout.LINE_END);

    panel.add(centerWrapper, BorderLayout.CENTER);
    panel.add(promptScrollPane, BorderLayout.LINE_END);

    // サムネイルリスト
    ThumbnailList thumbnailList = new ThumbnailList(imagePath);

    // mutable reference for lambda
    ImageFile[] currentImageFile = {null};

    // プロンプト読み込みヘルパー（右ペイン展開中のみ）
    Runnable loadPrompt = () -> {
      if (!promptScrollPane.isVisible()) return;
      ImageFile current = currentImageFile[0];
      if (current == null) return;
      try {
        Optional<String> prompt = PngMetadataReader.readPrompt(current.file());
        promptTextArea.setText(prompt.orElse("\u30d7\u30ed\u30f3\u30d7\u30c8\u60c5\u5831\u304c\u3042\u308a\u307e\u305b\u3093"));
      } catch (IOException ex) {
        promptTextArea.setText("\u30a8\u30e9\u30fc: " + ex.getMessage());
      }
    };

    // トグルボタンのアクションリスナー
    toggleButton.addActionListener(e -> {
      boolean nowVisible = !promptScrollPane.isVisible();
      promptScrollPane.setVisible(nowVisible);
      toggleButton.setText(nowVisible ? "\u25C4" : "\u25B6");
      panel.revalidate();
      if (nowVisible) {
        loadPrompt.run();
      }
    });

    // サムネイル選択リスナー
    thumbnailList.addThumbnailSelectionListener(e -> {
      if (!e.getValueIsAdjusting()) {
        ImageFile selected = thumbnailList.getSelectedImageFile();
        if (selected != null) {
          currentImageFile[0] = selected;
          try {
            BufferedImage fullImage = ImageIO.read(selected.file());
            if (fullImage != null) {
              imageLabel.setIcon(new ImageIcon(fullImage));
            }
          } catch (IOException ex) {
            ex.printStackTrace();
          }
          loadPrompt.run();
        }
      }
    });

    panel.add(thumbnailList.getThumbnailPane(), BorderLayout.LINE_START);

    return frame;
  }
}
