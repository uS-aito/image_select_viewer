# Research & Design Decisions

## Summary
- **Feature**: `comfyui-image-viewer`
- **Discovery Scope**: Extension（既存 Swing 実装への機能追加）
- **Key Findings**:
  - Java 標準 `ImageIO` + `IIOMetadata` で PNG tEXt チャンクの読み取りが可能（追加ライブラリ不要）
  - `BorderLayout` + `setVisible(false)` + `revalidate()` で右ペイン折りたたみを実装可能
  - 既存の `ThumbnailList` は `ImageIcon` のみ保持していてファイル参照がないため、`ImageFile` record への変更が必要

## Research Log

### PNG tEXt メタデータ読み取り

- **Context**: ComfyUI が PNG の `tEXt` チャンクに `prompt` キーで生成プロンプトを保存している。Java でこれを読み取る方法を調査。
- **Sources Consulted**: Java ImageIO API ドキュメント、javax.imageio.metadata パッケージ
- **Findings**:
  - `ImageIO.getImageReadersByFormatName("png")` で PNG リーダーを取得
  - `reader.getImageMetadata(0).getAsTree("javax_imageio_png_1.0")` でメタデータ DOM を取得
  - `tEXt` チャンクは DOM ツリー内の `tEXtEntry` ノードとして表現され、`keyword` 属性と `value` 属性を持つ
  - `keyword="prompt"` のノードを検索することで値を取得できる
- **Implications**: 追加ライブラリ不要。ステートレスなユーティリティクラス `PngMetadataReader` として実装。

### 折りたたみパネルの実装方法

- **Context**: 右ペインを `▶`/`◀` ボタンで折りたたみ/展開する実装方法を検討。
- **Findings**:
  - `JSplitPane.setOneTouchExpandable(true)`: 組み込み機能だが矢印ボタンの外観がカスタマイズできない
  - `setVisible(false/true)` + `revalidate()`: `BorderLayout` はコンポーネントの `isVisible()` を layout 時に参照するため、`setVisible(false)` でそのコンポーネントの占有領域がなくなる
- **Implications**: カスタム `▶`/`◀` ボタン要件に対しては `setVisible` + `revalidate` が最適。`JSplitPane` は不要。

## Architecture Pattern Evaluation

| Option | Description | Strengths | Risks / Limitations | Notes |
|--------|-------------|-----------|---------------------|-------|
| setVisible + revalidate | JComponent.setVisible(false) で非表示、revalidate でレイアウト再計算 | シンプル、標準 Swing | なし | 採用 |
| JSplitPane.setOneTouchExpandable | 組み込み折りたたみ機能 | コード量が少ない | ボタン外観をカスタマイズできない | 不採用 |
| カスタムアニメーション | タイマーで幅を段階的に変化 | 見た目がスムーズ | 実装が複雑、要件外 | 不採用 |

## Design Decisions

### Decision: `ImageFile` record を共通ドメインモデルとして導入

- **Context**: サムネイルリストは `ImageIcon` のみ管理し、フルサイズ表示やメタデータ読み取りに必要なファイル参照を持っていない
- **Alternatives Considered**:
  1. `ImageIcon` の subDescription に File をマップで管理
  2. `ImageFile` record で `File` + `ImageIcon` を束ねる
- **Selected Approach**: `ImageFile` record（Java 21 record）
- **Rationale**: イミュータブルで型安全。サムネイル表示・フルサイズ読み込み・メタデータ読み取りの3ユースケースを単一参照で賄える
- **Trade-offs**: `ThumbnailList` と `ImageLoader` の型変更が必要
- **Follow-up**: セルレンダラーを `ThumbnailList` 内の匿名クラスとして実装

### Decision: セルレンダラーを匿名クラスで実装

- **Context**: `JList<ImageFile>` にカスタムレンダラーが必要
- **Alternatives Considered**:
  1. 独立した `ImageFileCellRenderer` クラスとして実装
  2. `ThumbnailList` 内の匿名クラス
- **Selected Approach**: 匿名クラス（`ThumbnailList` 内）
- **Rationale**: 単一の使用箇所のみ。独立クラスは不要な抽象化になる
- **Trade-offs**: `ThumbnailList` 内のコードが若干増えるが許容範囲

## Risks & Mitigations
- PNG メタデータ形式の差異 — ComfyUI 以外のツールで生成した PNG は `prompt` チャンクを持たない場合がある → `Optional.empty()` で graceful に処理し、ユーザーへメッセージ表示
- フルサイズ画像読み込みのパフォーマンス — 大きな PNG（4K等）の読み込みに時間がかかる可能性がある → 現仕様では許容（ズーム機能も対象外のため、スクロールで対応）
