package com.github.us_aito.image_select_viewer;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 右ペインとトグルボタンの動作を検証するテスト（タスク 4.2）
 */
class MainFrameRightPaneTest {

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

    /** コンテナ内の全 JScrollPane をリストアップする */
    private List<JScrollPane> findAllScrollPanes(Container container) {
        List<JScrollPane> panes = new ArrayList<>();
        for (Component c : container.getComponents()) {
            if (c instanceof JScrollPane sp) {
                panes.add(sp);
            }
            if (c instanceof Container cont) {
                panes.addAll(findAllScrollPanes(cont));
            }
        }
        return panes;
    }

    /** トグルボタン（▶ or ◀ ラベル）を取得するヘルパー */
    private JButton getToggleButton(JFrame frame) {
        return findAllButtons((Container) frame.getContentPane()).stream()
            .filter(b -> "\u25B6".equals(b.getText()) || "\u25C4".equals(b.getText()))
            .findFirst()
            .orElseThrow(() -> new AssertionError("トグルボタンが見つからない"));
    }

    /** JTextArea を持つ JScrollPane を返すヘルパー */
    private List<JScrollPane> getPromptScrollPanes(JFrame frame) {
        return findAllScrollPanes((Container) frame.getContentPane()).stream()
            .filter(sp -> sp.getViewport().getView() instanceof JTextArea)
            .toList();
    }

    @Test
    void toggleButton_initialLabelIsRightArrow() {
        JFrame frame = MainFrame.createMainFrame("テスト");
        JButton btn = getToggleButton(frame);
        assertEquals("\u25B6", btn.getText(), "起動時のトグルボタンラベルは ▶ であること");
    }

    @Test
    void promptPane_initiallyHidden() {
        JFrame frame = MainFrame.createMainFrame("テスト");
        List<JScrollPane> promptPanes = getPromptScrollPanes(frame);
        assertFalse(promptPanes.isEmpty(), "JTextArea を含む JScrollPane（右ペイン）が存在すること");
        assertTrue(promptPanes.stream().anyMatch(sp -> !sp.isVisible()),
            "起動時に右ペインは非表示であること");
    }

    @Test
    void toggleButton_clickOnceExpandsRightPane() {
        JFrame frame = MainFrame.createMainFrame("テスト");
        JButton btn = getToggleButton(frame);

        btn.doClick();

        // 右ペインが表示になること
        List<JScrollPane> promptPanes = getPromptScrollPanes(frame);
        assertTrue(promptPanes.stream().anyMatch(Component::isVisible),
            "クリック後に右ペインが表示になること");

        // ラベルが ◀ になること
        assertEquals("\u25C4", btn.getText(), "展開時のラベルは ◀ であること");
    }

    @Test
    void toggleButton_clickTwiceCollapsesRightPane() {
        JFrame frame = MainFrame.createMainFrame("テスト");
        JButton btn = getToggleButton(frame);

        btn.doClick(); // 展開
        btn.doClick(); // 折りたたみ

        // ラベルが ▶ に戻ること
        assertEquals("\u25B6", btn.getText(), "再折りたたみ後のラベルは ▶ であること");

        // 右ペインが非表示になること
        List<JScrollPane> promptPanes = getPromptScrollPanes(frame);
        assertTrue(promptPanes.stream().anyMatch(sp -> !sp.isVisible()),
            "再折りたたみ後に右ペインが非表示になること");
    }
}
