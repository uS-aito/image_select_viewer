package com.github.us_aito.image_select_viewer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.swing.JFrame;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class MainFrameTest {

    @TempDir
    Path tempDir;

    @Test
    void createMainFrameはtitleとpathを受け取りJFrameを返す() {
        String path = tempDir.toAbsolutePath().toString();
        JFrame frame = MainFrame.createMainFrame("テストタイトル", path);
        assertNotNull(frame, "JFrame が null でないこと");
        assertEquals("テストタイトル", frame.getTitle(), "タイトルが設定されていること");
    }

    @Test
    void createMainFrameはDISPOSE_ON_CLOSEまたはEXIT_ON_CLOSEが設定されている() {
        String path = tempDir.toAbsolutePath().toString();
        JFrame frame = MainFrame.createMainFrame("タイトル", path);
        int closeOp = frame.getDefaultCloseOperation();
        assertTrue(
            closeOp == JFrame.EXIT_ON_CLOSE || closeOp == JFrame.DISPOSE_ON_CLOSE,
            "ウィンドウの閉じる動作が設定されていること"
        );
    }
}
