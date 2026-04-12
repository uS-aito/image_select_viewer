# Brief: image-zoom-control

## Problem
ComfyUI で生成した画像を閲覧する際、画像のサイズがペインに対して大きすぎたり小さすぎたりするため、詳細確認やレイアウト把握が困難。現在は画像をフルサイズで表示してスクロールするしかなく、画像全体を俯瞰したり特定部分を拡大したりすることができない。

## Current State
- 中央ペインは `JLabel` + `ImageIcon` で実装されており、フルサイズのPNG画像を固定表示している
- `JScrollPane` でラップされているためスクロールは可能
- ズーム操作の手段がなく、スケール変更の実装もない

## Desired Outcome
- 中央ペインの下部に常時表示されるツールバー（アンダーバー）がある
- ツールバーから「フィット表示」「倍率指定」「スライダー操作」の3通りでズームを制御できる
- ズーム変更時、画像はスクロール可能なペイン内でスムーズにスケール変更される

## Approach
カスタム `ZoomableImagePanel`（`JPanel` サブクラス）を実装し、`paintComponent` 内で `Graphics2D.scale()` を使用して現在のズーム倍率で画像を描画する。ツールバーは `JPanel` として中央ペイン下部に配置し、フィットボタン・JComboBox（倍率プルダウン）・JSlider（連続操作）・ズームイン/アウトボタンで構成する。

## Scope
- **In**:
  - 中央ペインを `JLabel` から `ZoomableImagePanel` に置き換え
  - フィットボタン（ウィンドウサイズに画像を収める）
  - 倍率プルダウン（10%, 25%, 50%, 75%, 100%, 200%, 300%, 400%, 500%, 600%, 700%, 800%）
  - ズームスライダー（⊖ ボタン / スライダー / ⊕ ボタン）
  - ツールバーの3コントロールの相互同期（いずれかを操作すると他も更新）
- **Out**:
  - マウスホイールによるズーム
  - ズーム状態の永続化（アプリ再起動時にリセット）
  - 画像の回転・反転
  - キーボードショートカットによるズーム

## Boundary Candidates
- **ZoomableImagePanel**: ズームロジックと画像描画を担う独立コンポーネント。画像の保持・倍率の保持・スケール描画を責務とする
- **ZoomToolbar**: ツールバーUIコンポーネント。フィット/プルダウン/スライダーの3コントロールを保持し、ズーム値の変化を `ZoomableImagePanel` に通知する
- **MainFrame の統合**: `ZoomableImagePanel` と `ZoomToolbar` を中央エリアに組み込む。画像選択時に `ZoomableImagePanel.setImage()` を呼ぶ

## Out of Boundary
- フォルダ選択・メニューバーは `comfyui-image-viewer` 既存スペック更新が担当する
- 右ペイン（プロンプト表示）には変更を加えない
- サムネイルリストには変更を加えない

## Upstream / Downstream
- **Upstream**: `comfyui-image-viewer` スペック（メニューバーUX変更）— `MainFrame` の基本構造に依存
- **Downstream**: なし（このスペックが完了後の依存スペックなし）

## Existing Spec Touchpoints
- **Extends**: `comfyui-image-viewer`（中央ペインの実装を `JLabel` から `ZoomableImagePanel` に変更）
- **Adjacent**: `comfyui-image-viewer` の右ペイン折りたたみ（トグルボタンは中央ペイン右端に配置されており、レイアウト変更時に競合しないよう注意）

## Constraints
- Java Swing のみ（追加ライブラリなし）
- 既存の `JScrollPane` 構造との共存が必要
- スライダーの範囲はプルダウンの最小/最大（10%〜800%）に合わせる
- デフォルトズームはフィット表示（画像選択時に自動でフィット）
