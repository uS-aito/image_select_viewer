package com.github.us_aito.image_select_viewer;

import javax.swing.SwingWorker;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

public class ImageLoader extends SwingWorker<Integer, Integer> {
  String imagePath;
  DefaultListModel model;
  private static int targetWidth = 100;
  private static int targetHeight = 100;

  public ImageLoader(String imagePath, DefaultListModel model) {
    this.imagePath = imagePath;
    this.model = model;
  }

  @Override
  public Integer doInBackground() {
    System.out.println("start doInBackground");
    File imageDir = new File(this.imagePath);
    File imageFileList[] = imageDir.listFiles();
    System.out.printf("num of images: %d\n", imageFileList.length);

    BufferedImage originalImageBuffer = null;
    for (int i = 0; i < imageFileList.length; i++) {
      try {
        originalImageBuffer = ImageIO.read(imageFileList[i]);
      } catch (IOException e) {
        e.printStackTrace();
      }

      Image scaledImage = originalImageBuffer.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
      if (scaledImage != null) {
        ImageIcon thumbnailIcon = new ImageIcon(scaledImage);
        model.addElement(thumbnailIcon);
      } else {
        System.err.println("scaledImage is null");
      }
    }

    return null;
  }

}
