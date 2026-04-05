package com.github.us_aito.image_select_viewer;

import javax.swing.SwingWorker;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ImageLoader extends SwingWorker<Void, ImageIcon> {
  String imagePath;
  DefaultListModel<ImageIcon> model;
  private static int targetWidth = 100;
  private static int targetHeight = 100;

  public ImageLoader(String imagePath, DefaultListModel<ImageIcon> model) {
    this.imagePath = imagePath;
    this.model = model;
  }

  @Override
  public Void doInBackground() {
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

      if (originalImageBuffer == null) {
        System.out.println("skipped: " + imageFileList[i].getName());
        continue;
      }

      Image scaledImage = originalImageBuffer.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
      if (scaledImage != null) {
        ImageIcon thumbnailIcon = new ImageIcon(scaledImage);
        publish(thumbnailIcon);
      } else {
        System.err.println("scaledImage is null");
      }
    }

    return null;
  }

  @Override
  public void process(List<ImageIcon> chunks) {
    for(ImageIcon icon: chunks) {
      model.addElement(icon);
    }
  }

}
