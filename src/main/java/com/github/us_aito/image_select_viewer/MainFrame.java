package com.github.us_aito.image_select_viewer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ImageIcon;
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class MainFrame {

  public static JFrame createMainFrame(String title, String imagePath) {
    JFrame frame = new JFrame(title);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setBounds(300, 80, 900, 600);

    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    frame.getContentPane().add(panel, BorderLayout.CENTER);

    JLabel imageLabel = new JLabel();
    JScrollPane imageScrollPane = new JScrollPane(imageLabel);
    panel.add(imageScrollPane, BorderLayout.CENTER);

    ThumbnailList thumbnailList = new ThumbnailList(imagePath);
    thumbnailList.addThumbnailSelectionListener(e -> {
      if (!e.getValueIsAdjusting()) {
        ImageFile selected = thumbnailList.getSelectedImageFile();
        if (selected != null) {
          try {
            BufferedImage fullImage = ImageIO.read(selected.file());
            if (fullImage != null) {
              imageLabel.setIcon(new ImageIcon(fullImage));
            }
          } catch (IOException ex) {
            ex.printStackTrace();
          }
        }
      }
    });
    panel.add(thumbnailList.getThumbnailPane(), BorderLayout.LINE_START);

    return frame;
  }
}
