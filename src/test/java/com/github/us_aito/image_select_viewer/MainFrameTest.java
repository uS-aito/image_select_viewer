package com.github.us_aito.image_select_viewer;

import org.junit.jupiter.api.Test;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import static org.junit.jupiter.api.Assertions.*;

class MainFrameTest {

    @Test
    void createMainFrameはtitleを受け取りJFrameを返す() {
        JFrame frame = MainFrame.createMainFrame("テストタイトル");
        assertNotNull(frame, "JFrame が null でないこと");
        assertEquals("テストタイトル", frame.getTitle(), "タイトルが設定されていること");
    }

    @Test
    void createMainFrameはDISPOSE_ON_CLOSEまたはEXIT_ON_CLOSEが設定されている() {
        JFrame frame = MainFrame.createMainFrame("タイトル");
        int closeOp = frame.getDefaultCloseOperation();
        assertTrue(
            closeOp == JFrame.EXIT_ON_CLOSE || closeOp == JFrame.DISPOSE_ON_CLOSE,
            "ウィンドウの閉じる動作が設定されていること"
        );
    }

    @Test
    void createMainFrameはJMenuBarとFileメニューとOpenFolderメニュー項目を持つ() {
        JFrame frame = MainFrame.createMainFrame("テスト");
        JMenuBar menuBar = frame.getJMenuBar();
        assertNotNull(menuBar, "JMenuBar が設定されていること");

        // "File" メニューが存在すること
        JMenu fileMenu = null;
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            if ("File".equals(menuBar.getMenu(i).getText())) {
                fileMenu = menuBar.getMenu(i);
                break;
            }
        }
        assertNotNull(fileMenu, "File メニューが存在すること");

        // "Open Folder" メニュー項目が存在すること
        JMenuItem openFolderItem = null;
        for (int i = 0; i < fileMenu.getItemCount(); i++) {
            JMenuItem item = fileMenu.getItem(i);
            if (item != null && "Open Folder".equals(item.getText())) {
                openFolderItem = item;
                break;
            }
        }
        assertNotNull(openFolderItem, "Open Folder メニュー項目が存在すること");
    }
}
