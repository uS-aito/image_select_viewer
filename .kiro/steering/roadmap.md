# Roadmap

## Overview
ComfyUI Image Viewer アプリケーションの UI 改善。既存の `comfyui-image-viewer` スペックで実装済みの基盤の上に、フォルダ選択のUXをメニューバー駆動に変更し、中央ペインにズームコントロールを追加する。

## Approach Decision
- **Chosen**: クロスプラットフォームOS標準ダイアログ + カスタムZoomableImagePanel
- **Why**: OSネイティブのファイル選択UIを提供しつつ、高性能なズームをGraphics2Dで実現する
- **Rejected alternatives**: ImageIconスケーリング（大画像で遅い）、AffineTransformOpプリスケール（複雑さに対してメリット小）

## Scope
- **In**: メニューバー追加、起動時の空状態表示、File > Open Folder、中央ペインのズームツールバー
- **Out**: 画像の回転・編集、PNG以外のフォーマット対応、ズーム状態の永続化

## Constraints
- Java Swing / AWT のみ使用（追加ライブラリなし）
- macOS では `java.awt.FileDialog`、その他では `JFileChooser` を使用
- 既存実装（タスク 1.1〜4.2 完了済み）との互換性を維持する

## Boundary Strategy
- **Why this split**: フォルダ選択UXの変更とズーム機能は責務が独立しているため、別スペックで管理する
- **Shared seams to watch**: `MainFrame` は両スペックが変更する中心クラス。中央ペインの構造変更はズームスペックが担当し、既存スペック更新はメニューバー追加のみに留める

## Existing Spec Updates
- [ ] comfyui-image-viewer -- Req 1（フォルダ選択）をメニューバー駆動に変更。起動時は空状態を表示し、File > Open Folder でOS標準ダイアログを開く。Dependencies: none

## Direct Implementation Candidates
（なし）

## Specs (dependency order)
- [ ] image-zoom-control -- 中央ペインにフィットボタン・倍率プルダウン・スライダーからなるズームツールバーを追加する。Dependencies: comfyui-image-viewer (existing spec update)
