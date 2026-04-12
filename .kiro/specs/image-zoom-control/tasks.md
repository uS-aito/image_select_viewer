# Implementation Plan

- [x] 1. Foundation: ズームファクターと画像スケーリングの基盤実装
- [x] 1.1 zoomFactor フィールド・ZOOM_LEVELS 定数を追加し、updateImageDisplay をズーム対応に拡張する
  - `double[] zoomFactor = {0}` フィールドを MainFrame に追加する（0 = フィットモード、正値 = 固定倍率）
  - `static final int[] ZOOM_LEVELS = {10, 25, 50, 75, 100, 200, 300, 400, 500, 600, 700, 800}` 定数を追加する
  - `updateImageDisplay` を拡張し、`zoomFactor[0] <= 0` ならフィット計算、正値なら固定倍率でスケーリングするよう変更する
  - 固定倍率モードで `imageLabel.setPreferredSize(new Dimension(newW, newH))` と `imageLabel.revalidate()` を呼び出す
  - `mvn compile` が通り、既存テストが全て通過すること
  - _Requirements: 4.1, 5.1, 5.2_
  - _Boundary: MainFrame_

- [ ] 2. Core: ZoomToolbar パネルとフィットボタンの実装
- [ ] 2.1 ZoomToolbar パネルを中央ペイン下部に追加し、フィットボタンを実装する
  - フロー配置の `JPanel` を作成し、`centerWrapper` の `SOUTH` 領域に追加する
  - フィットボタン（□ アイコン）を生成し、クリック時に `zoomFactor[0] = 0` に設定して `updateImageDisplay.run()` を呼び出す
  - アプリ起動時に中央ペイン下部にツールバーパネルが常時表示されること
  - フィットボタンクリック後に画像がペインに収まること
  - _Requirements: 1.1, 1.2, 1.3, 2.1_
  - _Boundary: MainFrame_
  - _Depends: 1.1_

- [ ] 3. Core: 倍率プルダウンの実装
- [ ] 3.1 倍率プルダウンを実装し、選択時に固定倍率で画像を表示する
  - `ZOOM_LEVELS` 配列を "10%", "25%", ... "800%" 形式の文字列配列に変換して `JComboBox<String>` を生成する
  - プルダウン選択時に `zoomFactor[0] = selectedPct / 100.0` を設定して `updateImageDisplay.run()` を呼び出す
  - デフォルト選択を 100% に設定する
  - プルダウンで 200% を選択後、画像が拡大されスクロールバーが表示されること
  - _Requirements: 3.1, 3.2, 3.4_
  - _Boundary: MainFrame_
  - _Depends: 2.1_

- [ ] 4. Core: ズームスライダーと⊖/⊕ボタンの実装
- [ ] 4.1 ズームスライダーと縮小・拡大ボタンを実装する
  - `JSlider(10, 800, 100)` を生成してツールバーに追加する
  - スライダー両端に `JButton("⊖")` と `JButton("⊕")` を配置する
  - スライダーの `ChangeListener` でスライダー操作時に `zoomFactor[0] = value / 100.0` を設定して `updateImageDisplay.run()` を呼び出す
  - スライダーを操作すると画像がリアルタイムに拡大縮小されること
  - _Requirements: 4.1, 4.2, 4.4_
  - _Boundary: MainFrame_
  - _Depends: 2.1_

- [ ] 5. Core: コントロール間の相互同期実装
- [ ] 5.1 フィット倍率計算と最近接ズームレベル検索のユーティリティメソッドを実装する
  - `findNearestZoomLevelIndex(int pct)` を実装し、`ZOOM_LEVELS` 内で最も近い値のインデックスを返す
  - `stepZoom(JComboBox, JSlider, int delta)` を実装し、現在のプルダウンインデックスを `delta` 分移動する（0未満または最大を超えない）
  - `syncControlsToCurrentScale(JComboBox, JSlider)` を実装し、現在の `zoomFactor` または フィットスケールを計算してプルダウン・スライダーに反映する
  - `findNearestZoomLevelIndex(150)` が 100%(インデックス4) または 200%(インデックス5) を返すこと
  - _Requirements: 3.3, 3.4, 4.3, 4.5, 4.6_
  - _Boundary: MainFrame_
  - _Depends: 3.1, 4.1_

- [ ] 5.2 各コントロールのアクションリスナーに相互同期を組み込む
  - フィットボタン押下後に `syncControlsToCurrentScale()` を呼び出してプルダウン・スライダーを更新する
  - プルダウン選択後にスライダーを選択倍率に同期する（`zoomSlider.setValue(selectedPct)`）
  - スライダー操作後に `findNearestZoomLevelIndex()` を使用してプルダウンを最近接選択肢に同期する
  - ⊖/⊕ ボタン押下で `stepZoom()` を呼び出し、プルダウン・スライダー・画像を連動して更新する
  - いずれか1つのコントロールを操作すると他の2つが自動的に更新されること
  - _Requirements: 2.2, 3.3, 3.4, 4.3, 4.5, 4.6_
  - _Boundary: MainFrame_
  - _Depends: 5.1_

- [ ] 6. Integration: 画像選択時のフィット自動適用
- [ ] 6.1 サムネイル選択時に zoomFactor をリセットしてフィット表示を自動適用する
  - サムネイル選択リスナー内で `zoomFactor[0] = 0` に設定してからフルサイズ画像を読み込む
  - 画像読み込み後に `updateImageDisplay.run()` を呼び出し、その後 `syncControlsToCurrentScale()` を呼び出す
  - サムネイルを選択するたびに画像がフィット表示で表示され、プルダウン・スライダーもフィット倍率に更新されること
  - _Requirements: 2.3_
  - _Boundary: MainFrame_
  - _Depends: 5.2_

- [ ] 7. Validation: テストと動作確認
- [ ] 7.1 ズーム関連ユーティリティメソッドのユニットテストを実装する
  - `findNearestZoomLevelIndex()` が各境界値（10, 800）と中間値（150）に対して正しいインデックスを返すことをテストする
  - `stepZoom()` がインデックス 0 で下方向操作しても範囲外にならないことをテストする
  - `stepZoom()` が最大インデックスで上方向操作しても範囲外にならないことをテストする
  - `ZOOM_LEVELS` が 12 要素で期待値通りであることをテストする
  - `mvn test` が全テスト通過すること
  - _Requirements: 3.1, 4.5, 4.6_
  - _Boundary: MainFrame_

- [ ]* 7.2 ズームコントロールの統合動作を手動確認する
  - フィットボタン押下で画像がペインに収まり、プルダウン・スライダーが同期されること（2.1, 2.2）
  - プルダウンで 200% 選択後、スクロールバーが表示されること（3.2, 5.1）
  - スライダー操作でリアルタイムに画像が拡大縮小されること（4.2）
  - ⊖/⊕ で一段階ずつ倍率が変化し、0 未満・最大超えで変化しないこと（4.5, 4.6）
  - 画像未選択状態でもツールバーが表示されること（1.3）
  - _Requirements: 1.3, 2.1, 2.2, 2.3, 3.2, 4.2, 4.5, 4.6, 5.1, 5.2_
