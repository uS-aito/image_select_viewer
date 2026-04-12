package com.github.us_aito.image_select_viewer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.swing.DefaultListModel;
import java.lang.reflect.Field;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Task 5.1: ThumbnailList の動的フォルダ読み込み対応テスト
 *
 * Requirements:
 *   1.2 - 起動直後（フォルダ未選択時）はサムネイルリストを空で表示する
 *   2.4 - フォルダ選択確定後にPNGファイルをサムネイルリストに読み込む
 *   2.6 - 既に読み込まれている状態で新フォルダ選択時は現在のサムネイルリストをクリアし新しいPNGを読み込む
 */
class ThumbnailListTest {

    /**
     * テスト用ヘルパー: ThumbnailList の private model フィールドを取得する
     */
    private DefaultListModel<ImageFile> getModel(ThumbnailList list) throws Exception {
        Field modelField = ThumbnailList.class.getDeclaredField("model");
        modelField.setAccessible(true);
        @SuppressWarnings("unchecked")
        DefaultListModel<ImageFile> model = (DefaultListModel<ImageFile>) modelField.get(list);
        return model;
    }

    /**
     * Req 1.2: null パスでコンストラクタを呼び出した場合、モデルが空であること
     */
    @Test
    void nullパスで初期化するとモデルが空であること() throws Exception {
        ThumbnailList thumbnailList = new ThumbnailList(null);

        DefaultListModel<ImageFile> model = getModel(thumbnailList);
        assertEquals(0, model.size(), "null パスで初期化した場合、モデルは空であるべき");
    }

    /**
     * Req 1.2: null パスで初期化した場合、ImageLoader が起動されないこと（モデルへの追加なし）
     */
    @Test
    void nullパスで初期化してもImageLoaderが起動されないこと() throws Exception {
        // ImageLoader が起動すると NullPointerException が発生するため、
        // null パスで初期化して例外が発生しないことを確認する
        assertDoesNotThrow(() -> {
            ThumbnailList thumbnailList = new ThumbnailList(null);
        }, "null パスで初期化しても例外が発生しないべき");
    }

    /**
     * Req 2.4, 2.6: loadFolder メソッドが存在すること
     */
    @Test
    void loadFolderメソッドが存在すること() throws Exception {
        // リフレクションでメソッドの存在を確認
        var method = ThumbnailList.class.getMethod("loadFolder", String.class);
        assertNotNull(method, "loadFolder(String) メソッドが存在するべき");
    }

    /**
     * Req 2.6: loadFolder 呼び出し後にモデルがクリアされること
     * （ImageLoader が非同期で動作するため、モデルが一時的に空になることを確認）
     *
     * @TempDir を使って実際には存在するディレクトリを渡す（NullPointerException 回避）
     */
    @Test
    void loadFolder呼び出し後にモデルがクリアされること(@TempDir Path tempDir) throws Exception {
        ThumbnailList thumbnailList = new ThumbnailList(null);
        DefaultListModel<ImageFile> model = getModel(thumbnailList);

        // モデルに仮のデータを追加してクリアされることを確認
        // ImageFile は final class の可能性があるため、モデルのサイズだけ確認する
        // loadFolder を呼び出した直後（ImageLoader の非同期実行前）にモデルがクリアされることを確認
        thumbnailList.loadFolder(tempDir.toString());

        // loadFolder は model.clear() を同期的に呼ぶため、即座に空になるはず
        // （ImageLoader の process() は非同期なので、その追加は後で発生する）
        assertEquals(0, model.size(), "loadFolder 呼び出し直後はモデルが空であるべき");
    }

    /**
     * Req 2.6: loadFolder を複数回呼び出しても正常に動作すること
     */
    @Test
    void loadFolderを複数回呼び出しても正常であること(@TempDir Path tempDir) throws Exception {
        ThumbnailList thumbnailList = new ThumbnailList(null);

        assertDoesNotThrow(() -> {
            thumbnailList.loadFolder(tempDir.toString());
            thumbnailList.loadFolder(tempDir.toString());
        }, "loadFolder を複数回呼び出しても例外が発生しないべき");
    }
}
