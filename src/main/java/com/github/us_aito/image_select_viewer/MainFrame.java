package com.github.us_aito.image_select_viewer;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ImageIcon;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

public class MainFrame {

  private static final int RIGHT_PANE_WIDTH = 250;

  /**
   * OS 標準のフォルダ選択ダイアログを開く。
   * macOS では native Finder ダイアログ（java.awt.FileDialog）を使用し、
   * 他 OS では JFileChooser を使用する。
   *
   * @param frame 親フレーム
   * @return 選択されたディレクトリの絶対パス文字列、キャンセル時は null
   */
  private static String openFolderDialog(JFrame frame) {
    boolean isMac = System.getProperty("os.name", "").toLowerCase().contains("mac");
    if (isMac) {
      System.setProperty("apple.awt.fileDialogForDirectories", "true");
      try {
        FileDialog dialog = new FileDialog(frame, "フォルダを選択", FileDialog.LOAD);
        dialog.setVisible(true);
        String dir = dialog.getDirectory();
        String file = dialog.getFile();
        if (file == null) {
          return null;
        }
        return new File(dir, file).getAbsolutePath();
      } finally {
        System.setProperty("apple.awt.fileDialogForDirectories", "false");
      }
    } else {
      JFileChooser chooser = new JFileChooser();
      chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      int result = chooser.showOpenDialog(frame);
      if (result == JFileChooser.APPROVE_OPTION) {
        return chooser.getSelectedFile().getAbsolutePath();
      }
      return null;
    }
  }

  /** 指定フォルダから画像を読み込む（タスク 6.3 で実装予定） */
  private static void loadImagesFromFolder(String folderPath) {
    // stub: タスク 6.3 で実装
  }

  public static JFrame createMainFrame(String title) {
    JFrame frame = new JFrame(title);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setBounds(300, 80, 900, 600);

    // メニューバー
    JMenuBar menuBar = new JMenuBar();
    JMenu fileMenu = new JMenu("File");
    JMenuItem openFolderItem = new JMenuItem("Open Folder");
    openFolderItem.addActionListener(e -> {
      String folderPath = openFolderDialog(frame);
      if (folderPath != null) {
        loadImagesFromFolder(folderPath);
      }
    });
    fileMenu.add(openFolderItem);
    menuBar.add(fileMenu);
    frame.setJMenuBar(menuBar);

    // メインパネル（BorderLayout）
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    frame.getContentPane().add(panel, BorderLayout.CENTER);

    // 画像表示ラベルとスクロールペイン
    JLabel imageLabel = new JLabel();
    imageLabel.setHorizontalAlignment(JLabel.CENTER);
    JScrollPane imageScrollPane = new JScrollPane(imageLabel);

    // 右ペイン（プロンプト表示用 JTextArea）
    JTextArea promptTextArea = new JTextArea();
    promptTextArea.setEditable(false);
    promptTextArea.setLineWrap(true);
    promptTextArea.setWrapStyleWord(true);
    JScrollPane promptScrollPane = new JScrollPane(promptTextArea);
    promptScrollPane.setPreferredSize(new Dimension(RIGHT_PANE_WIDTH, 0));
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

    // サムネイルリスト（起動時はフォルダ未選択のため null を渡す）
    ThumbnailList thumbnailList = new ThumbnailList(null);

    // 現在表示中の画像（スケーリング再計算のために保持）
    BufferedImage[] currentImage = {null};
    ImageFile[] currentImageFile = {null};

    // 画像をビューポートサイズに合わせてスケーリング表示
    Runnable updateImageDisplay = () -> {
      if (currentImage[0] == null) return;
      int vpW = imageScrollPane.getViewport().getWidth();
      int vpH = imageScrollPane.getViewport().getHeight();
      if (vpW <= 0 || vpH <= 0) return;
      BufferedImage img = currentImage[0];
      double scale = Math.min((double) vpW / img.getWidth(), (double) vpH / img.getHeight());
      int newW = (int) (img.getWidth() * scale);
      int newH = (int) (img.getHeight() * scale);
      Image scaled = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
      imageLabel.setIcon(new ImageIcon(scaled));
    };

    // ウィンドウリサイズ時に画像を再スケーリング
    imageScrollPane.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        updateImageDisplay.run();
      }
    });

    // プロンプト読み込みヘルパー（右ペイン展開中のみ）
    Runnable loadPrompt = () -> {
      if (!promptScrollPane.isVisible()) return;
      ImageFile current = currentImageFile[0];
      if (current == null) return;
      try {
        Optional<String> prompt = PngMetadataReader.readPrompt(current.file());
        String displayText = prompt
            .map(MainFrame::extractPromptTexts)
            .orElse("\u30d7\u30ed\u30f3\u30d7\u30c8\u60c5\u5831\u304c\u3042\u308a\u307e\u305b\u3093");
        promptTextArea.setText(displayText);
        promptTextArea.setCaretPosition(0);
      } catch (IOException ex) {
        promptTextArea.setText("\u30a8\u30e9\u30fc: " + ex.getMessage());
      }
    };

    // トグルボタンのアクションリスナー（右ペイン開閉時にウィンドウ幅を変更）
    toggleButton.addActionListener(e -> {
      boolean nowVisible = !promptScrollPane.isVisible();
      promptScrollPane.setVisible(nowVisible);
      toggleButton.setText(nowVisible ? "\u25C4" : "\u25B6");
      if (nowVisible) {
        frame.setSize(frame.getWidth() + RIGHT_PANE_WIDTH, frame.getHeight());
      } else {
        frame.setSize(frame.getWidth() - RIGHT_PANE_WIDTH, frame.getHeight());
      }
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
              currentImage[0] = fullImage;
              updateImageDisplay.run();
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

  /**
   * ComfyUI の "prompt" tEXt チャンク値（API フォーマット JSON）から
   * CLIPTextEncode ノードのテキスト入力を抽出して可読形式で返す。
   * CLIPTextEncode が見つからない場合は元の文字列をそのまま返す。
   */
  private static String extractPromptTexts(String rawJson) {
    if (rawJson == null || !rawJson.contains("\"CLIPTextEncode\"")) {
      return rawJson;
    }

    // トップレベルのノードオブジェクトを抽出（ブレース深度トラッキング）
    List<String> nodeBlocks = new ArrayList<>();
    int depth = 0;
    int nodeStart = -1;
    boolean inString = false;
    boolean escaped = false;

    for (int i = 0; i < rawJson.length(); i++) {
      char c = rawJson.charAt(i);
      if (escaped) { escaped = false; continue; }
      if (c == '\\' && inString) { escaped = true; continue; }
      if (c == '"') { inString = !inString; continue; }
      if (inString) continue;

      if (c == '{') {
        depth++;
        if (depth == 2) nodeStart = i;
      } else if (c == '}') {
        if (depth == 2 && nodeStart >= 0) {
          nodeBlocks.add(rawJson.substring(nodeStart, i + 1));
          nodeStart = -1;
        }
        depth--;
      }
    }

    // CLIPTextEncode ノードから "text" フィールドを抽出
    Pattern textPattern = Pattern.compile("\"text\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");
    List<String> texts = new ArrayList<>();
    for (String block : nodeBlocks) {
      if (block.contains("\"CLIPTextEncode\"")) {
        Matcher m = textPattern.matcher(block);
        if (m.find()) {
          String text = m.group(1)
              .replace("\\n", "\n")
              .replace("\\t", "\t")
              .replace("\\\"", "\"")
              .replace("\\\\", "\\");
          texts.add(text);
        }
      }
    }

    if (texts.isEmpty()) {
      return rawJson;
    }

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < texts.size(); i++) {
      if (i > 0) sb.append("\n\n");
      sb.append("--- ").append(i + 1).append(" ---\n");
      sb.append(texts.get(i));
    }
    return sb.toString();
  }
}
