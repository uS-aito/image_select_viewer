package com.github.us_aito.image_select_viewer;

import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import java.awt.Dimension;

public class ThumbnailList {
  public static JScrollPane createThumbnailListPanel(String ImagePath) {
    DefaultListModel<ImageIcon> model = new DefaultListModel<ImageIcon>();
    JList<DefaultListModel<ImageIcon>> imageList = new JList(model);

    JScrollPane thumbnailPane = new JScrollPane();
    thumbnailPane.getViewport().setView(imageList);
    thumbnailPane.setPreferredSize(new Dimension(200, 800));

    ImageLoader imageLoader = new ImageLoader(ImagePath, model);
    imageLoader.execute();

    return thumbnailPane;
  }
}
