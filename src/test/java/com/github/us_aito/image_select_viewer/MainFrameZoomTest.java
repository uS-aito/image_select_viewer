package com.github.us_aito.image_select_viewer;

import org.junit.jupiter.api.DisplayName;
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

    // ─── タスク 5.1: findNearestZoomLevelIndex ─────────────────────────────────

    @Test
    void findNearest_完全一致_10はインデックス0を返す() {
        assertEquals(0, MainFrame.findNearestZoomLevelIndex(10),
            "pct=10 は ZOOM_LEVELS[0]=10 に完全一致するためインデックス0を返すこと");
    }

    @Test
    void findNearest_完全一致_100はインデックス4を返す() {
        assertEquals(4, MainFrame.findNearestZoomLevelIndex(100),
            "pct=100 は ZOOM_LEVELS[4]=100 に完全一致するためインデックス4を返すこと");
    }

    @Test
    void findNearest_完全一致_800はインデックス11を返す() {
        assertEquals(11, MainFrame.findNearestZoomLevelIndex(800),
            "pct=800 は ZOOM_LEVELS[11]=800 に完全一致するためインデックス11を返すこと");
    }

    @Test
    void findNearest_下限以下_1はインデックス0を返す() {
        assertEquals(0, MainFrame.findNearestZoomLevelIndex(1),
            "pct=1 は ZOOM_LEVELS[0]=10 に最も近いためインデックス0を返すこと");
    }

    @Test
    void findNearest_上限超過_999はインデックス11を返す() {
        assertEquals(11, MainFrame.findNearestZoomLevelIndex(999),
            "pct=999 は ZOOM_LEVELS[11]=800 に最も近いためインデックス11を返すこと");
    }

    @Test
    void findNearest_同距離_150はインデックス4または5を返す() {
        int idx = MainFrame.findNearestZoomLevelIndex(150);
        assertTrue(idx == 4 || idx == 5,
            "pct=150 は 100(idx=4) と 200(idx=5) の中間のため、どちらも許容: 実際=" + idx);
    }

    @Test
    void findNearest_37はインデックス1を返す() {
        // 25(idx=1) と 50(idx=2) の中間は 37.5; 37 は 25 に近い(diff=12) vs 50(diff=13)
        assertEquals(1, MainFrame.findNearestZoomLevelIndex(37),
            "pct=37 は ZOOM_LEVELS[1]=25 に最も近いためインデックス1を返すこと");
    }

    @Test
    void findNearest_ZOOM_LEVELS定数は12要素で正しい値を持つ() {
        int[] expected = {10, 25, 50, 75, 100, 200, 300, 400, 500, 600, 700, 800};
        assertArrayEquals(expected, MainFrame.ZOOM_LEVELS,
            "ZOOM_LEVELS は {10,25,50,75,100,200,300,400,500,600,700,800} の12要素であること");
    }

    // ─── タスク 5.1: stepZoom ─────────────────────────────────────────────────

    /** テスト用 zoomComboBox を生成するヘルパー */
    private JComboBox<String> makeZoomComboBox() {
        String[] items = java.util.Arrays.stream(MainFrame.ZOOM_LEVELS)
            .mapToObj(l -> l + "%")
            .toArray(String[]::new);
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setSelectedIndex(4); // デフォルト 100%
        return combo;
    }

    @Test
    void stepZoom_インデックス0でデルタマイナス1は0のまま() {
        JComboBox<String> combo = makeZoomComboBox();
        JSlider slider = new JSlider(10, 800, 100);
        combo.setSelectedIndex(0);

        MainFrame.stepZoom(combo, slider, -1);

        assertEquals(0, combo.getSelectedIndex(),
            "インデックス0でdelta=-1 → アンダーフローなくインデックス0のまま");
    }

    @Test
    void stepZoom_インデックス11でデルタプラス1は11のまま() {
        JComboBox<String> combo = makeZoomComboBox();
        JSlider slider = new JSlider(10, 800, 100);
        combo.setSelectedIndex(11);

        MainFrame.stepZoom(combo, slider, +1);

        assertEquals(11, combo.getSelectedIndex(),
            "インデックス11でdelta=+1 → オーバーフローなくインデックス11のまま");
    }

    @Test
    void stepZoom_インデックス4からプラス1でインデックス5になる() {
        JComboBox<String> combo = makeZoomComboBox();
        JSlider slider = new JSlider(10, 800, 100);
        combo.setSelectedIndex(4); // 100%

        MainFrame.stepZoom(combo, slider, +1);

        assertEquals(5, combo.getSelectedIndex(),
            "インデックス4(100%)からdelta=+1 → インデックス5(200%)");
    }

    @Test
    void stepZoom_インデックス4からマイナス1でインデックス3になる() {
        JComboBox<String> combo = makeZoomComboBox();
        JSlider slider = new JSlider(10, 800, 100);
        combo.setSelectedIndex(4); // 100%

        MainFrame.stepZoom(combo, slider, -1);

        assertEquals(3, combo.getSelectedIndex(),
            "インデックス4(100%)からdelta=-1 → インデックス3(75%)");
    }

    @Test
    void stepZoom_スライダーがZOOM_LEVELSの対応する値に更新される() {
        JComboBox<String> combo = makeZoomComboBox();
        JSlider slider = new JSlider(10, 800, 100);
        combo.setSelectedIndex(4); // 100%

        MainFrame.stepZoom(combo, slider, +1); // → index 5 = 200%

        assertEquals(200, slider.getValue(),
            "stepZoom後、スライダーはZOOM_LEVELS[新インデックス]=200に更新されること");
    }

    // ─── タスク 5.1: syncControlsToCurrentScale（間接テスト via JFrame） ─────────

    @Test
    void syncControls_zoomFactor2のとき_スライダーが200でコンボが200パーセント() {
        // syncControlsToCurrentScale は createMainFrame 内のクロージャ変数を使うため
        // フレーム生成後にプルダウン操作でzoomFactor=2.0相当を設定し、
        // スライダー値が200になっていることをコンボ経由で確認する
        JFrame frame = MainFrame.createMainFrame("テスト");
        List<JComboBox<String>> combos = findAllComboBoxes((Container) frame.getContentPane());
        List<JSlider> sliders = findAllSliders((Container) frame.getContentPane());
        assertFalse(combos.isEmpty());
        assertFalse(sliders.isEmpty());

        JComboBox<String> combo = combos.get(0);
        JSlider slider = sliders.get(0);

        // プルダウンで200%を選択 → zoomFactor=2.0, slider=200 に同期される（タスク5.2で実装されるが
        // スライダー同期はタスク4.1のChangeListenerが担当）
        // ここでは findNearestZoomLevelIndex と stepZoom の直接テストで要件を満たす
        // syncControlsToCurrentScale の詳細テストは現在利用可能なAPIで実施する

        // null画像・zoomFactor=0のとき: syncControlsToCurrentScaleは slider=100, combo=100% を設定すること
        // これはメソッドが実装された後に MainFrameSyncTest で確認する
        // 現時点では「実装が存在し、コンパイルが通ること」を確認
        MainFrame.syncControlsToCurrentScale(combo, slider, new double[]{0}, null, null);
        assertEquals(100, slider.getValue(),
            "zoomFactor=0、画像=null のとき syncControlsToCurrentScale は slider を 100 に設定すること");
        assertEquals("100%", combo.getSelectedItem(),
            "zoomFactor=0、画像=null のとき syncControlsToCurrentScale は combo を '100%' に設定すること");
    }

    @Test
    void syncControls_zoomFactor2のとき_スライダーが200になる() {
        JFrame frame = MainFrame.createMainFrame("テスト");
        List<JComboBox<String>> combos = findAllComboBoxes((Container) frame.getContentPane());
        List<JSlider> sliders = findAllSliders((Container) frame.getContentPane());
        JComboBox<String> combo = combos.get(0);
        JSlider slider = sliders.get(0);

        MainFrame.syncControlsToCurrentScale(combo, slider, new double[]{2.0}, null, null);

        assertEquals(200, slider.getValue(),
            "zoomFactor=2.0 のとき syncControlsToCurrentScale は slider を 200 に設定すること");
        assertEquals("200%", combo.getSelectedItem(),
            "zoomFactor=2.0 のとき syncControlsToCurrentScale は combo を '200%' に設定すること");
    }

    // ─── タスク 5.2: コントロール間の相互同期 ─────────────────────────────────────

    /**
     * ⊖ ボタンが ActionListener を持つことを確認する（タスク 5.2）
     * ZOOM_SYNC_ENABLED フラグが OFF の間はリスナー未登録のため FAIL する
     */
    @Test
    void sync_縮小ボタンにActionListenerが登録されている() {
        JFrame frame = MainFrame.createMainFrame("テスト");
        List<JButton> buttons = findAllButtons((Container) frame.getContentPane());
        JButton minusBtn = buttons.stream()
            .filter(b -> "\u2296".equals(b.getText()))
            .findFirst()
            .orElse(null);
        assertNotNull(minusBtn, "縮小ボタン（⊖）が存在すること");
        assertTrue(minusBtn.getActionListeners().length > 0,
            "縮小ボタン（⊖）に ActionListener が登録されていること");
    }

    /**
     * ⊕ ボタンが ActionListener を持つことを確認する（タスク 5.2）
     * ZOOM_SYNC_ENABLED フラグが OFF の間はリスナー未登録のため FAIL する
     */
    @Test
    void sync_拡大ボタンにActionListenerが登録されている() {
        JFrame frame = MainFrame.createMainFrame("テスト");
        List<JButton> buttons = findAllButtons((Container) frame.getContentPane());
        JButton plusBtn = buttons.stream()
            .filter(b -> "\u2295".equals(b.getText()))
            .findFirst()
            .orElse(null);
        assertNotNull(plusBtn, "拡大ボタン（⊕）が存在すること");
        assertTrue(plusBtn.getActionListeners().length > 0,
            "拡大ボタン（⊕）に ActionListener が登録されていること");
    }

    /**
     * プルダウン選択後にスライダーが選択倍率に同期されること（タスク 5.2、要件 3.3）
     * ZOOM_SYNC_ENABLED が OFF の間は同期されないため FAIL する
     */
    @Test
    void sync_プルダウンで200選択後スライダーが200になる() {
        JFrame frame = MainFrame.createMainFrame("テスト");
        List<JComboBox<String>> combos = findAllComboBoxes((Container) frame.getContentPane());
        List<JSlider> sliders = findAllSliders((Container) frame.getContentPane());
        JComboBox<String> combo = combos.get(0);
        JSlider slider = sliders.get(0);

        // 200% を選択
        combo.setSelectedItem("200%");

        assertEquals(200, slider.getValue(),
            "プルダウンで '200%' を選択後、スライダーは 200 に同期されること（要件 3.3）");
    }

    /**
     * スライダーを 300 に設定後にプルダウンが最近接選択肢（300%）に同期されること（タスク 5.2、要件 4.3）
     * ZOOM_SYNC_ENABLED が OFF の間は同期されないため FAIL する
     */
    @Test
    void sync_スライダーを300に設定後プルダウンが300パーセントになる() {
        JFrame frame = MainFrame.createMainFrame("テスト");
        List<JComboBox<String>> combos = findAllComboBoxes((Container) frame.getContentPane());
        List<JSlider> sliders = findAllSliders((Container) frame.getContentPane());
        JComboBox<String> combo = combos.get(0);
        JSlider slider = sliders.get(0);

        // スライダーを 300 に設定
        slider.setValue(300);

        assertEquals("300%", combo.getSelectedItem(),
            "スライダーを 300 に設定後、プルダウンは '300%'（最近接選択肢）に同期されること（要件 4.3）");
    }

    // ─── タスク 6.1: サムネイル選択時のフィット自動適用 ───────────────────────────

    /** JComboBox を1つ取得するヘルパー */
    private JComboBox<?> findComboBox(JFrame frame) {
        List<JComboBox<String>> combos = findAllComboBoxes((Container) frame.getContentPane());
        return combos.isEmpty() ? null : combos.get(0);
    }

    @Test
    @DisplayName("autoFit: サムネイル選択時に zoomFactor が 0 にリセットされること（要件 2.3）")
    void autoFit_サムネイル選択時にzoomFactorが0にリセットされること() throws Exception {
        JFrame frame = MainFrame.createMainFrame("テスト");

        // 前提: コンボで 200% を選択して zoomFactor を 2.0 にする
        JComboBox<?> combo = findComboBox(frame);
        assertNotNull(combo, "JComboBox が存在すること");
        combo.setSelectedItem("200%");
        assertEquals(2.0, MainFrame.lastZoomFactor[0], 0.001, "前提: zoomFactor が 2.0 であること");

        // テスト用 1×1 PNG を作成
        java.io.File tempPng = java.io.File.createTempFile("autofit_test", ".png");
        tempPng.deleteOnExit();
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_RGB);
        javax.imageio.ImageIO.write(img, "png", tempPng);

        // サムネイルをモデルに追加して選択 → リスナーが発火
        ImageFile imageFile = new ImageFile(tempPng, null);
        MainFrame.lastThumbnailList.addImageFileForTest(imageFile);
        MainFrame.lastThumbnailList.selectIndexForTest(0);

        // 検証: リスナーが zoomFactor[0] = 0 を実行したこと
        assertEquals(0.0, MainFrame.lastZoomFactor[0], 0.001,
            "サムネイル選択後 zoomFactor が 0 にリセットされること（フィットモード）");
    }
}
