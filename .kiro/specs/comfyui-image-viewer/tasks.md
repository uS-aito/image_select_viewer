# Implementation Plan

- [ ] 1. Foundation: ImageFile ドメインモデルの作成
- [x] 1.1 `ImageFile` record を作成する
  - PNG ファイルへの参照（`File`）とサムネイル（`ImageIcon`）を保持するイミュータブルモデルを作成する
  - Java record として実装し、コンストラクタの型安全性を保証する
  - `ImageFile.java` ファイルが作成され、コンパイルが通ること
  - _Requirements: 2.1, 2.4, 3.1, 5.1_

- [ ] 2. Core: サービス層の実装
- [ ] 2.1 (P) PNG プロンプトメタデータ読み取り機能を実装する
  - Java 標準 ImageIO + IIOMetadata を使用して PNG の tEXt チャンクを走査する
  - `"prompt"` キーを持つ tEXtEntry ノードを見つけてその値を返す
  - `"prompt"` チャンクが存在しない場合は `Optional.empty()` を返す
  - ファイル読み取りエラー時は `IOException` をスローする
  - `ImageInputStream` と `ImageReader` を try-finally で確実にクローズしてリソースリークを防止する
  - `prompt` チャンクを持つ PNG を渡すと値が返り、持たない PNG では `Optional.empty()` が返ること
  - _Requirements: 5.1, 5.2_
  - _Boundary: PngMetadataReader_

- [ ] 2.2 (P) サムネイル読み込みロジックを `ImageFile` モデル対応に変更する
  - `SwingWorker` の型パラメータを `SwingWorker<Void, ImageFile>` に変更する
  - `doInBackground()` 内で `ImageFile(file, thumbnail)` を生成して publish するよう変更する
  - コンストラクタ引数の `DefaultListModel` 型を `DefaultListModel<ImageFile>` に変更する
  - PNG 以外のファイル（`ImageIO.read` が null を返すもの）は引き続きスキップされること
  - `ImageLoader.java` がコンパイルエラーなく変更され、PNG ファイルが `ImageFile` としてモデルに追加されること
  - _Requirements: 2.1, 2.2, 2.3_
  - _Boundary: ImageLoader_
  - _Depends: 1.1_

- [ ] 3. Core: ThumbnailList の ImageFile 対応改修
- [ ] 3.1 サムネイルリストを `ImageFile` モデルで動作するよう変更する
  - `DefaultListModel<ImageIcon>` を `DefaultListModel<ImageFile>` に変更する
  - `JList<ImageIcon>` を `JList<ImageFile>` に変更する
  - カスタムセルレンダラーを追加して `imageFile.thumbnail()` をリストセルの `JLabel` に設定する
  - `getSelectedIcon()` を `getSelectedImageFile()` に変更し、戻り値型を `ImageFile` にする
  - PNG サムネイルがリストに表示され、選択時に `ImageFile` オブジェクトが取得できること
  - _Requirements: 2.1, 2.4_
  - _Depends: 1.1, 2.2_

- [ ] 4. Integration: メインウィンドウの統合
- [ ] 4.1 起動時フォルダ選択とフルサイズ画像表示を実装する
  - `Main.java` に `JFileChooser`（ディレクトリのみ選択可）を追加し、起動時にフォルダ選択ダイアログを表示する
  - キャンセル時は `System.exit(0)` でアプリを終了する
  - `MainFrame.createMainFrame` のシグネチャに `imagePath` 引数を追加してハードコードされたパスを削除する
  - サムネイル選択時に `ImageIO.read()` でフルサイズ PNG を読み込み `imageLabel` に設定する
  - 中央ペインを `JScrollPane` でラップしてスクロール対応にする
  - 起動時はダイアログが表示され、フォルダ確定後にサムネイルが一覧表示され、サムネイルクリックで中央ペインにフルサイズ画像が表示されること
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 2.4, 3.1, 3.2, 3.3_
  - _Depends: 3.1_

- [ ] 4.2 右ペインと折りたたみ機能を追加する
  - 中央ペイン右端のトグルボタンパネルに `▶` ボタンを配置する（常時表示）
  - 右ペイン（スクロール可能なテキストエリア）を追加し、起動時は `setVisible(false)` で非表示にする
  - トグルボタンクリックで `setVisible(true/false)` + `revalidate()` により右ペインを展開/折りたたむ
  - 展開時はボタンラベルを `◀`、折りたたみ時は `▶` に変更する
  - 右ペイン展開中に画像を選択したとき `PngMetadataReader.readPrompt()` を呼び出してテキストエリアに結果を表示する
  - `prompt` メタデータが存在しない場合はテキストエリアに「プロンプト情報がありません」と表示する
  - 右ペイン折りたたみ中に画像を選択してもプロンプト読み取り処理を実行しない
  - トグルボタンで右ペインが開閉し、画像選択時にプロンプトがスクロール可能なテキストエリアに表示されること
  - _Requirements: 4.1, 4.2, 4.3, 4.4, 5.1, 5.2, 5.3, 5.4_
  - _Depends: 4.1, 2.1_

- [ ] 5. Validation: 動作確認
- [ ]* 5.1 PngMetadataReader のユニットテストを実装する
  - `prompt` tEXt チャンクを持つテスト用 PNG で正しい値が返ることを確認する
  - `prompt` チャンクを持たない PNG で `Optional.empty()` が返ることを確認する
  - _Requirements: 5.1, 5.2_

- [ ] 5.2 アプリケーション全体の統合動作を確認する
  - 起動 → フォルダ選択 → サムネイル表示 → 画像選択 → フルサイズ表示の一連の動作が正常に動作すること
  - キャンセル時にアプリが終了することを確認する
  - トグルボタンのクリックで右ペインが開閉し、ボタンラベルが `▶` / `◀` に切り替わること
  - ComfyUI 生成画像（`prompt` チャンク付き）を選択したとき右ペインにプロンプトが表示されること
  - `prompt` チャンクのない PNG を選択したとき「プロンプト情報がありません」が表示されること
  - PNG 以外のファイルがフォルダ内に混在していてもサムネイルリストに表示されないこと
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 2.1, 2.2, 2.3, 2.4, 3.1, 3.2, 3.3, 4.1, 4.2, 4.3, 4.4, 5.1, 5.2, 5.3, 5.4_
