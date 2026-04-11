package com.github.us_aito.image_select_viewer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PngMetadataReaderTest {

    @TempDir
    Path tempDir;

    // PNG に tEXt チャンク "prompt" を埋め込んで書き出すヘルパー
    private File createPngWithPrompt(String promptValue) throws IOException {
        File file = tempDir.resolve("with_prompt.png").toFile();
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

        ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();
        IIOMetadata metadata = writer.getDefaultImageMetadata(
                ImageTypeSpecifier.createFromRenderedImage(img),
                writer.getDefaultWriteParam());

        String nativeFormat = "javax_imageio_png_1.0";
        IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(nativeFormat);

        IIOMetadataNode tEXt = new IIOMetadataNode("tEXt");
        IIOMetadataNode entry = new IIOMetadataNode("tEXtEntry");
        entry.setAttribute("keyword", "prompt");
        entry.setAttribute("value", promptValue);
        tEXt.appendChild(entry);
        root.appendChild(tEXt);

        metadata.setFromTree(nativeFormat, root);

        try (ImageOutputStream ios = ImageIO.createImageOutputStream(file)) {
            writer.setOutput(ios);
            writer.write(null, new IIOImage(img, null, metadata), null);
        } finally {
            writer.dispose();
        }
        return file;
    }

    // tEXt チャンクなし (通常の PNG) を書き出すヘルパー
    private File createPngWithoutPrompt() throws IOException {
        File file = tempDir.resolve("no_prompt.png").toFile();
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        ImageIO.write(img, "png", file);
        return file;
    }

    @Test
    void promptChunkが存在する場合はその値を返す() throws IOException {
        String expected = "{\"positive\": \"a cat\", \"negative\": \"\"}";
        File png = createPngWithPrompt(expected);

        Optional<String> result = PngMetadataReader.readPrompt(png);

        assertTrue(result.isPresent(), "prompt チャンクが存在するので Optional は非空であるべき");
        assertEquals(expected, result.get());
    }

    @Test
    void promptChunkが存在しない場合はOptionalEmptyを返す() throws IOException {
        File png = createPngWithoutPrompt();

        Optional<String> result = PngMetadataReader.readPrompt(png);

        assertTrue(result.isEmpty(), "prompt チャンクがないので Optional.empty() であるべき");
    }

    @Test
    void 存在しないファイルを渡すとIOExceptionをスローする() {
        File nonExistent = tempDir.resolve("ghost.png").toFile();

        assertThrows(IOException.class, () -> PngMetadataReader.readPrompt(nonExistent));
    }
}
