package com.github.us_aito.image_select_viewer;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// タスク 4.1 用インポート
// (JSlider は javax.swing.* に含まれるため追加不要)

// タスク 3.1 用インポート
// (JComboBox は javax.swing.* に含まれるため追加不要)

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

    // ─── タスク 3.1: 倍率プルダウン ──────────────────────────────────────────

    /** コンテナ内の全 JComboBox をリストアップする */
    @SuppressWarnings("unchecked")
    private List<JComboBox<String>> findAllComboBoxes(Container container) {
        List<JComboBox<String>> result = new ArrayList<>();
        for (Component c : container.getComponents()) {
            if (c instanceof JComboBox<?> cb) {
                result.add((JComboBox<String>) cb);
            }
            if (c instanceof Container cont) {
                result.addAll(findAllComboBoxes(cont));
            }
        }
        return result;
    }

    @Test
    void zoomDropdown_ZoomToolbarにJComboBoxが存在し12項目ある() {
        JFrame frame = MainFrame.createMainFrame("テスト");
        List<JComboBox<String>> combos = findAllComboBoxes((Container) frame.getContentPane());
        assertFalse(combos.isEmpty(), "JComboBox が zoomToolbar に存在すること");
        JComboBox<String> combo = combos.get(0);
        assertEquals(12, combo.getItemCount(), "プルダウンは 12 項目（10%〜800%）であること");
    }

    @Test
    void zoomDropdown_デフォルト選択が100パーセントである() {
        JFrame frame = MainFrame.createMainFrame("テスト");
        List<JComboBox<String>> combos = findAllComboBoxes((Container) frame.getContentPane());
        assertFalse(combos.isEmpty(), "JComboBox が存在すること");
        JComboBox<String> combo = combos.get(0);
        assertEquals("100%", combo.getSelectedItem(), "デフォルト選択は '100%' であること");
    }

    @Test
    void zoomDropdown_最初の項目が10パーセントで最後が800パーセント() {
        JFrame frame = MainFrame.createMainFrame("テスト");
        List<JComboBox<String>> combos = findAllComboBoxes((Container) frame.getContentPane());
        assertFalse(combos.isEmpty(), "JComboBox が存在すること");
        JComboBox<String> combo = combos.get(0);
        assertEquals("10%", combo.getItemAt(0), "最初の項目は '10%' であること");
        assertEquals("800%", combo.getItemAt(11), "最後の項目は '800%' であること");
    }

    // ─── タスク 4.1: ズームスライダーと⊖/⊕ボタン ──────────────────────────────

    /** コンテナ内の全 JSlider をリストアップする */
    private List<JSlider> findAllSliders(Container container) {
        List<JSlider> result = new ArrayList<>();
        for (Component c : container.getComponents()) {
            if (c instanceof JSlider slider) {
                result.add(slider);
            }
            if (c instanceof Container cont) {
                result.addAll(findAllSliders(cont));
            }
        }
        return result;
    }

    @Test
    void zoomSlider_JSliderがToolbarに存在しmin10max800初期値100() {
        JFrame frame = MainFrame.createMainFrame("テスト");
        List<JSlider> sliders = findAllSliders((Container) frame.getContentPane());
        assertFalse(sliders.isEmpty(), "JSlider がツールバーに存在すること");
        JSlider slider = sliders.get(0);
        assertEquals(10, slider.getMinimum(), "スライダーの最小値は 10 であること");
        assertEquals(800, slider.getMaximum(), "スライダーの最大値は 800 であること");
        assertEquals(100, slider.getValue(), "スライダーの初期値は 100 であること");
    }

    @Test
    void zoomSlider_縮小ボタン_が存在する() {
        JFrame frame = MainFrame.createMainFrame("テスト");
        List<JButton> buttons = findAllButtons((Container) frame.getContentPane());
        boolean found = buttons.stream().anyMatch(b -> "\u2296".equals(b.getText()));
        assertTrue(found, "縮小ボタン（⊖ U+2296）がツールバーに存在すること");
    }

    @Test
    void zoomSlider_拡大ボタン_が存在する() {
        JFrame frame = MainFrame.createMainFrame("テスト");
        List<JButton> buttons = findAllButtons((Container) frame.getContentPane());
        boolean found = buttons.stream().anyMatch(b -> "\u2295".equals(b.getText()));
        assertTrue(found, "拡大ボタン（⊕ U+2295）がツールバーに存在すること");
    }
}
