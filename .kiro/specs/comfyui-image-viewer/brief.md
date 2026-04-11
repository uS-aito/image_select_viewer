# Brief: comfyui-image-viewer

## Problem
ComfyUI で生成した PNG 画像を確認する際、ファイルマネージャーでは画像に埋め込まれたプロンプト情報を見ることができない。生成物を評価・管理するには、画像とプロンプトを同時に確認できる専用ビューワーが必要。

## Current State
- 左ペイン（サムネイルリスト）と中央ペイン（メインビュー）の基本骨格は実装済み
- 中央ペインは現状サムネイルアイコンをそのまま表示（フルサイズ未対応）
- 右ペイン（プロンプト表示）は未実装
- フォルダパスはハードコード（起動時ダイアログ未実装）
- PNG メタデータ読み取りは未実装

## Desired Outcome
- 起動時にフォルダ選択ダイアログが表示される
- 左ペインに選択フォルダ内の PNG サムネイルが一覧表示される
- 左ペインで画像を選択すると中央ペインにフルサイズで表示される
- 中央ペイン右端の `▶`/`◀` トグルボタンで右ペインを折りたたみ/展開できる
- 右ペインには選択画像の PNG `prompt` メタデータ（ComfyUI 埋め込み）が表示される

## Approach
JSplitPane（左+中央 vs 右）+ 中央ペイン右端トグルボタンで折りたたみを実装。
PNG メタデータは Java 標準の `ImageIO` + `IIOMetadata` で `tEXt` チャンクを読み取り、`prompt` キーの値を表示する。

## Scope
- **In**:
  - 起動時フォルダ選択ダイアログ（JFileChooser）
  - 左ペイン：PNG サムネイル一覧（既存コードの改修含む）
  - 中央ペイン：フルサイズ PNG 表示（スクロール対応）
  - 右ペイン：折りたたみ可能、PNG `prompt` メタデータ表示
  - トグルボタン（`▶`/`◀`）による右ペイン開閉
- **Out**:
  - ズーム・回転などの画像操作
  - `workflow` メタデータの表示
  - PNG 以外のフォーマット対応
  - 画像の削除・移動・リネームなどのファイル操作

## Boundary Candidates
- フォルダ選択と画像ロード（起動フロー）
- サムネイルリスト（左ペイン）
- フルサイズ画像表示（中央ペイン）
- 右ペイン折りたたみ制御
- PNG メタデータ読み取り（`prompt` チャンク）

## Out of Boundary
- 画像編集・変換機能
- ComfyUI との直接連携（API 呼び出し等）
- workflow JSON の可視化

## Upstream / Downstream
- **Upstream**: なし（スタンドアロンアプリ）
- **Downstream**: 将来的なズーム機能、お気に入り管理、バッチ処理などが考えられる

## Existing Spec Touchpoints
- **Extends**: なし（既存スペックなし）
- **Adjacent**: 既存の `MainFrame`, `ThumbnailList`, `ImageLoader` クラスを改修・拡張

## Constraints
- Java 21、Swing（JavaFX への移行なし）
- 追加ライブラリなし（Maven 依存関係の追加禁止）
- PNG のみ対応
