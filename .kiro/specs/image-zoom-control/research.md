# Research Log: image-zoom-control

## Discovery Scope
- タイプ: Extension（既存 MainFrame への機能追加）
- 対象: ズームツールバーの追加と updateImageDisplay の拡張
- Discovery プロセス: Light Discovery

## Codebase Analysis

### 現行実装の主要事実
- `updateImageDisplay` Runnable（MainFrame 内）: ビューポートサイズへのフィットスケールを計算し `imageLabel.setIcon()` で表示
- `centerWrapper`（BorderLayout）: CENTER に `imageScrollPane`、LINE_END に `togglePanel`、SOUTH は空き
- `currentImage[0]`: BufferedImage の参照保持。ズーム計算に使用
- `imageLabel`: スケーリング済み画像を `ImageIcon` として表示
- `imageScrollPane`: `JScrollPane.SCROLLBARS_AS_NEEDED` がデフォルト動作

### 変更スコープ
変更対象ファイル: `MainFrame.java` のみ
変更なし: `ThumbnailList.java`, `ImageLoader.java`, `PngMetadataReader.java`, `ImageFile.java`

## Technology Decisions

### 画像スケーリングアプローチ

**採用: BufferedImage.getScaledInstance() の継続使用**
- 既存コードと同一パターンで一貫性を保てる
- `Image.SCALE_SMOOTH` により品質が確保されている
- 追加ライブラリ不要

**却下: Graphics2D.scale() / ZoomableImagePanel**
- 新しい JPanel サブクラスの作成が必要になり実装コストが増加
- 既存の `imageLabel` + `JScrollPane` の構造を変更する必要があり、リスクが高い
- 要件を満たすために必要な複雑さを超えている

### zoomFactor の型と表現

**採用: `double[] zoomFactor = {0}` — 0 = フィットモード、正値 = 固定倍率**
- 0 を「フィットモードのセンチネル値」として使用することで、フィット倍率を別途保持する必要がなくなる
- ラムダ/Runnable からアクセスするためにシングル要素配列パターン（既存の `currentImage[0]` と同一パターン）を使用

**却下: boolean isFitMode + double fixedZoom の2フィールド構成**
- シンプルな0判定で十分なため不要な複雑さ

## Design Decisions

### Synthesis 結果

**Generalization**: `updateImageDisplay` を zoomFactor 参照に一般化することで、フィット・固定倍率・将来の追加モードを同一 Runnable で処理できる。

**Build vs. Adopt**: 標準 Swing コンポーネントのみ使用。追加ライブラリなし。

**Simplification**:
- ZoomableImagePanel クラスは作成しない（不要な抽象化）
- ZoomToolbar クラスも作成しない（MainFrame 内のローカルパネルで十分）
- 変更ファイルは `MainFrame.java` の1ファイルのみ

### imageLabel.setPreferredSize() の必要性
固定倍率で画像がビューポートより大きくなった場合、`imageLabel` の preferred size を明示的に設定しないと `JScrollPane` がスクロールバーを表示しない。フィットモードでは不要だが、固定倍率モードでは必須。

## Integration Risk Assessment

| リスク | 影響 | 対策 |
|---|---|---|
| スライダー ChangeListener の高頻度発火 | 大きい画像でパフォーマンス低下 | 必要に応じて getValueIsAdjusting() でドラッグ完了時のみ更新に変更 |
| imageLabel の preferred size 未設定 | スクロールバーが表示されない | setPreferredSize() + revalidate() を updateImageDisplay 内で呼び出す |
| syncControlsToCurrentScale() のフィット倍率計算 | コントロール同期がずれる | updateImageDisplay と同じスケール計算ロジックを使用 |
