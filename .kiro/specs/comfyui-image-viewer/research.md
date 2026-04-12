# Research Log: comfyui-image-viewer (UI改善)

## Discovery Scope
- タイプ: Extension（既存システムへの変更）
- 対象: 起動フロー変更 + メニューバー追加
- Discovery プロセス: Light Discovery

## Codebase Analysis

### 現行実装の主要事実
- `Main.java`: 起動時 `JFileChooser` でフォルダ選択、キャンセル時 `System.exit(0)`
- `MainFrame.createMainFrame(String title, String imagePath)`: `imagePath` を必須引数として受け取り `ThumbnailList` を即時生成
- `ThumbnailList` コンストラクタ: `imagePath` を受け取り `ImageLoader.execute()` を即時呼び出し
- メニューバーは現在存在しない
- 既存の `updateImageDisplay()` はウィンドウリサイズ時にフィット表示を行うが、これは本スペックでは変更しない

### 変更スコープ
変更対象ファイル: `Main.java`, `MainFrame.java`, `ThumbnailList.java`
変更なし: `ImageLoader.java`, `PngMetadataReader.java`, `ImageFile.java`

## Technology Decisions

### OS標準ダイアログの実装方針

**macOS**: `java.awt.FileDialog` + `System.setProperty("apple.awt.fileDialogForDirectories", "true")`
- 理由: `JFileChooser` は macOS で Java 独自のUIを表示するため、ネイティブ Finder ダイアログが使えない
- `FileDialog` は AWT 経由で macOS の標準 NSOpenPanel を呼び出す
- `System.setProperty` はダイアログを表示する直前に設定し、表示後に元に戻す（グローバルプロパティのため）

**非macOS（Windows, Linux）**: `JFileChooser` + `DIRECTORIES_ONLY`
- Windows では OS に近いルック＆フィールが得られる
- Linux ではシステムのルック＆フィールに依存するが許容範囲内

**却下した代替案**: プラットフォーム固有ネイティブダイアログライブラリ（JavaFX FileChooser 等）
- 追加ライブラリ不要という制約に違反するため却下

## Design Decisions

### Synthesis 結果

**Generalization**: フォルダ読み込みロジックを `ThumbnailList.loadFolder(String)` に集約。起動時（null パス）と実行時（メニュー選択）の両方を統一インターフェースで扱う。

**Build vs. Adopt**: 追加ライブラリなし。Java AWT/Swing の標準コンポーネントのみで実現。

**Simplification**: OS判定ロジックを `MainFrame` の private メソッドに閉じ込め、ダイアログ抽象化レイヤーは作らない。現スペックで必要な実装は単純な if-else で十分。

### SwingWorker ライフサイクルリスク
- フォルダを素早く切り替えた場合、旧 `ImageLoader`（SwingWorker）が未完了のまま新 `ImageLoader` が起動する可能性がある
- `model.clear()` の直後に旧ローダーが `process()` を呼び出すと旧エントリーが追加されうる
- 許容判断: 旧ローダーが追加したエントリーは次の `model.clear()` でクリアされるため UI への長期的な影響はない。複雑なキャンセル機構は現スペックのスコープ外とする

## Integration Risk Assessment

| リスク | 影響 | 対策 |
|---|---|---|
| SwingWorker 競合 | 一時的に旧画像が表示される | model.clear() で自己修復。許容範囲内 |
| macOS System.setProperty グローバル汚染 | 他のダイアログに影響する可能性 | ダイアログ表示前後に設定・リセット |
| createMainFrame シグネチャ変更 | 既存呼び出し元が壊れる | Main.java のみが呼び出し元のため影響範囲は限定的 |
