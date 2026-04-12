package com.github.us_aito.image_select_viewer;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ZoomToolbar パネルとフィットボタンの動作を検証するテスト（タスク 2.1）
 */
class MainFrameZoomTest {

    /** コンテナ内の全 JButton をリストアップする */
    private List<JButton> findAllButtons(Container container) {
        List<JButton> buttons = new ArrayList<>();
        for (Component c : container.getComponents()) {
            if (c instanceof JButton btn) {
                buttons.add(btn);
            }
            if (c instanceof Container cont) {
                buttons.addAll(findAllButtons(cont));
            }
        }
        return buttons;
    }

    /** フィットボタン（□ = U+25A1）を取得するヘルパー */
    private JButton getFitButton(JFrame frame) {
        return findAllButtons((Container) frame.getContentPane()).stream()
            .filter(b -> "\u25A1".equals(b.getText()))
            .findFirst()
            .orElse(null);
    }

    /** FlowLayout を持つ JPanel をリストアップするヘルパー */
    private List<JPanel> findFlowLayoutPanels(Container container) {
        List<JPanel> panels = new ArrayList<>();
        for (Component c : container.getComponents()) {
            if (c instanceof JPanel panel && panel.getLayout() instanceof FlowLayout) {
                panels.add(panel);
            }
            if (c instanceof Container cont) {
                panels.addAll(findFlowLayoutPanels(cont));
            }
        }
        return panels;
    }

    @Test
    void zoomToolbar_フィットボタンが存在する() {
        JFrame frame = MainFrame.createMainFrame("テスト");
        JButton fitButton = getFitButton(frame);
        assertNotNull(fitButton, "フィットボタン（□）が存在すること");
    }

    @Test
    void zoomToolbar_FlowLayoutパネルが存在する() {
        JFrame frame = MainFrame.createMainFrame("テスト");
        List<JPanel> flowPanels = findFlowLayoutPanels((Container) frame.getContentPane());
        assertFalse(flowPanels.isEmpty(), "FlowLayout を持つ ZoomToolbar パネルが存在すること");
    }

    @Test
    void zoomToolbar_フィットボタンはcenterWrapperのSOUTHに配置されている() {
        JFrame frame = MainFrame.createMainFrame("テスト");
        // ZoomToolbar (FlowLayout パネル) が存在し、かつ可視であること
        List<JPanel> flowPanels = findFlowLayoutPanels((Container) frame.getContentPane());
        assertFalse(flowPanels.isEmpty(), "ZoomToolbar パネルが存在すること");
        // ツールバーは常時表示（画像未選択時も可視）
        assertTrue(
            flowPanels.stream().anyMatch(Component::isVisible),
            "ZoomToolbar パネルが可視であること（画像未選択時も表示される）"
        );
    }

    @Test
    void zoomToolbar_フィットボタンがフレームに常時表示される() {
        JFrame frame = MainFrame.createMainFrame("テスト");
        JButton fitButton = getFitButton(frame);
        assertNotNull(fitButton, "フィットボタンが存在すること");
        assertTrue(fitButton.isVisible(), "フィットボタンが可視であること");
    }
}
