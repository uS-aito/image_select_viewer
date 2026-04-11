package com.github.us_aito.image_select_viewer;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;

public class PngMetadataReader {

    public static Optional<String> readPrompt(File file) throws IOException {
        ImageInputStream iis = ImageIO.createImageInputStream(file);
        if (iis == null) {
            throw new IOException("ImageIO could not create ImageInputStream for: " + file);
        }

        ImageReader reader = null;
        try {
            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("png");
            if (!readers.hasNext()) {
                throw new IOException("No PNG ImageReader available");
            }
            reader = readers.next();
            reader.setInput(iis, true, false);

            IIOMetadata metadata = reader.getImageMetadata(0);
            Node root = metadata.getAsTree("javax_imageio_png_1.0");

            NodeList children = root.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);
                if ("tEXt".equals(node.getNodeName())) {
                    NodeList entries = node.getChildNodes();
                    for (int j = 0; j < entries.getLength(); j++) {
                        Node entry = entries.item(j);
                        if ("tEXtEntry".equals(entry.getNodeName())) {
                            NamedNodeMap attrs = entry.getAttributes();
                            Node keyword = attrs.getNamedItem("keyword");
                            if (keyword != null && "prompt".equals(keyword.getNodeValue())) {
                                Node value = attrs.getNamedItem("value");
                                return Optional.ofNullable(value != null ? value.getNodeValue() : null);
                            }
                        }
                    }
                }
            }
            return Optional.empty();
        } finally {
            if (reader != null) {
                reader.dispose();
            }
            iis.close();
        }
    }
}
