package com.github.us_aito.image_select_viewer;

import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionListener;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.ListCellRenderer;
import java.awt.Component;
import java.awt.Dimension;

public class ThumbnailList {
  private DefaultListModel<ImageFile> model = new DefaultListModel<ImageFile>();
  private JList<ImageFile> imageList = new JList<>(model);
  private JScrollPane thumbnailPane;
  private ImageLoader imageLoader;

  public ThumbnailList(String ImagePath) {
    this.thumbnailPane = new JScrollPane();
    this.thumbnailPane.getViewport().setView(this.imageList);
    this.thumbnailPane.setPreferredSize(new Dimension(200, 800));

    this.imageList.setCellRenderer(new ListCellRenderer<ImageFile>() {
      @Override
      public Component getListCellRendererComponent(
          JList<? extends ImageFile> list,
          ImageFile value,
          int index,
          boolean isSelected,
          boolean cellHasFocus) {
        JLabel label = new JLabel();
        label.setHorizontalAlignment(JLabel.CENTER);
        if (value != null) {
          label.setIcon(value.thumbnail());
        }
        return label;
      }
    });

    this.imageLoader = new ImageLoader(ImagePath, this.model);
    this.imageLoader.execute();
  }

  public JScrollPane getThumbnailPane() {
    return this.thumbnailPane;
  }

  public ImageFile getSelectedImageFile() {
    return this.imageList.getSelectedValue();
  }

  public void addThumbnailSelectionListener(ListSelectionListener listener) {
    this.imageList.addListSelectionListener(listener);
  }
}
