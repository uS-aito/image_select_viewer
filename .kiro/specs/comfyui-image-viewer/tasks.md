# Implementation Plan

## フェーズ1: 完了済みタスク（初期実装）

- [ ] 1. Foundation: ImageFile ドメインモデルの作成
- [x] 1.1 `ImageFile` record を作成する
  - PNG ファイルへの参照（`File`）とサムネイル（`ImageIcon`）を保持するイミュータブルモデルを作成する
  - Java record として実装し、コンストラクタの型安全性を保証する
  - `ImageFile.java` ファイルが作成され、コンパイルが通ること
  - _Requirements: 2.1, 2.4, 3.1, 5.1_

- [ ] 2. Core: サービス層の実装
- [x] 2.1 (P) PNG プロンプトメタデータ読み取り機能を実装する
  - Java 標準 ImageIO + IIOMetadata を使用して PNG の tEXt チャンクを走査する
  - `"prompt"` キーを持つ tEXtEntry ノードを見つけてその値を返す
  - `"prompt"` チャンクが存在しない場合は `Optional.empty()` を返す
  - ファイル読み取りエラー時は `IOException` をスローする
  - `ImageInputStream` と `ImageReader` を try-finally で確実にクローズしてリソースリークを防止する
  - `prompt` チャンクを持つ PNG を渡すと値が返り、持たない PNG では `Optional.empty()` が返ること
  - _Requirements: 5.1, 5.2_
  - _Boundary: PngMetadataReader_

- [x] 2.2 (P) サムネイル読み込みロジックを `ImageFile` モデル対応に変更する
  - `SwingWorker` の型パラメータを `SwingWorker<Void, ImageFile>` に変更する
  - `doInBackground()` 内で `ImageFile(file, thumbnail)` を生成して publish するよう変更する
  - コンストラクタ引数の `DefaultListModel` 型を `DefaultListModel<ImageFile>` に変更する
  - PNG 以外のファイル（`ImageIO.read` が null を返すもの）は引き続きスキップされること
  - `ImageLoader.java` がコンパイルエラーなく変更され、PNG ファイルが `ImageFile` としてモデルに追加されること
  - _Requirements: 2.1, 2.2, 2.3_
  - _Boundary: ImageLoader_
  - _Depends: 1.1_

- [ ] 3. Core: ThumbnailList の ImageFile 対応改修
- [x] 3.1 サムネイルリストを `ImageFile` モデルで動作するよう変更する
  - `DefaultListModel<ImageIcon>` を `DefaultListModel<ImageFile>` に変更する
  - `JList<ImageIcon>` を `JList<ImageFile>` に変更する
  - カスタムセルレンダラーを追加して `imageFile.thumbnail()` をリストセルの `JLabel` に設定する
  - `getSelectedIcon()` を `getSelectedImageFile()` に変更し、戻り値型を `ImageFile` にする
  - PNG サムネイルがリストに表示され、選択時に `ImageFile` オブジェクトが取得できること
  - _Requirements: 2.1, 2.4_
  - _Depends: 1.1, 2.2_

- [ ] 4. Integration: メインウィンドウの統合（初期実装）
- [x] 4.1 起動時フォルダ選択とフルサイズ画像表示を実装する
  - `Main.java` に `JFileChooser`（ディレクトリのみ選択可）を追加し、起動時にフォルダ選択ダイアログを表示する
  - キャンセル時は `System.exit(0)` でアプリを終了する
  - `MainFrame.createMainFrame` のシグネチャに `imagePath` 引数を追加してハードコードされたパスを削除する
  - サムネイル選択時に `ImageIO.read()` でフルサイズ PNG を読み込み `imageLabel` に設定する
  - 中央ペインを `JScrollPane` でラップしてスクロール対応にする
  - 起動時はダイアログが表示され、フォルダ確定後にサムネイルが一覧表示され、サムネイルクリックで中央ペインにフルサイズ画像が表示されること
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 2.4, 3.1, 3.2, 3.3_
  - _Depends: 3.1_

- [x] 4.2 右ペインと折りたたみ機能を追加する
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

---

## フェーズ2: UI改善（メニューバー・起動フロー変更）

- [x] 5. Foundation: ThumbnailList の動的フォルダ読み込み対応
- [x] 5.1 ThumbnailList に動的フォルダ読み込みメソッドを追加する
  - コンストラクタで `imagePath == null` の場合は `ImageLoader` を起動しない（空状態で初期化）
  - `model.clear()` 後に新しい `ImageLoader(model, path).execute()` を起動する `loadFolder(String path)` メソッドを追加する
  - `ThumbnailList` を `null` パスで生成した際にサムネイルが空のリストで表示されること
  - `loadFolder(newPath)` 呼び出し後にモデルがクリアされ新しいサムネイルが順次追加されること
  - _Requirements: 1.2, 2.4, 2.6_
  - _Boundary: ThumbnailList_

- [ ] 6. Core: MainFrame のメニューバーとフォルダ選択実装
- [x] 6.1 MainFrame に JMenuBar と File > Open Folder メニューを追加する
  - `createMainFrame` のシグネチャから `imagePath` 引数を削除し、空状態で起動するよう変更する
  - `JMenuBar` を生成し `File` メニューに `Open Folder` メニュー項目を追加してフレームに設定する
  - アプリ起動時にメニューバーが表示され `File > Open Folder` が選択可能なこと
  - _Requirements: 1.3, 2.1_
  - _Boundary: MainFrame_
  - _Depends: 5.1_

- [x] 6.2 OS 標準フォルダ選択ダイアログを実装する
  - `openFolderDialog()` private メソッドを追加し、`os.name` で macOS を検出する
  - macOS: `apple.awt.fileDialogForDirectories=true` を設定した `java.awt.FileDialog` でディレクトリ選択を行う
  - 非 macOS: `JFileChooser.DIRECTORIES_ONLY` を設定した `JFileChooser` でディレクトリ選択を行う
  - macOS でネイティブ Finder ダイアログが表示されること、キャンセル時に `null` が返ること
  - _Requirements: 2.2, 2.3, 2.5_
  - _Boundary: MainFrame_

- [x] 6.3 フォルダ確定後のサムネイル読み込みロジックを実装する
  - `loadImagesFromFolder(String path)` private メソッドを追加する
  - `thumbnailList.loadFolder(path)` を呼び出してサムネイルリストをリフレッシュする
  - `imageLabel.setIcon(null)` で現在表示中の画像をクリアする
  - `Open Folder` でフォルダ確定後にサムネイルリストが更新されること
  - 別フォルダ選択時に既存サムネイルがクリアされ新フォルダの PNG が読み込まれること
  - _Requirements: 2.4, 2.5, 2.6_
  - _Boundary: MainFrame_

- [x] 7. Integration: 起動フロー統合
- [x] 7.1 Main の起動フローを空状態起動に変更する
  - `Main.java` から `JFileChooser` の生成・表示・パス取得と `System.exit(0)` を削除する
  - `MainFrame.createMainFrame(title)` をパスなしで直接呼び出すよう変更する
  - アプリを起動した際にフォルダ選択ダイアログが表示されず空のメインウィンドウが開くこと
  - メニューバーが表示され `File > Open Folder` からフォルダ選択が起動できること
  - _Requirements: 1.1, 1.2, 1.3_
  - _Boundary: Main_
  - _Depends: 6.1_

- [ ] 8. Validation: 統合動作確認
- [ ] 8.1 フォルダ選択・表示・切り替えの統合動作を確認する
  - 起動時にフォルダ選択ダイアログが表示されず空ウィンドウが開くこと（1.1, 1.2, 1.3）
  - `File > Open Folder` でフォルダを選択後、PNG サムネイルが表示されること（2.4）
  - ダイアログキャンセル時に現在の表示状態が維持されること（2.5）
  - 別フォルダを選択した際にサムネイルがクリアされ再読み込みされること（2.6）
  - macOS でネイティブ Finder ダイアログが表示されること（2.2）
  - サムネイル選択でフルサイズ画像が中央ペインに表示され、スクロールが動作すること（3.4, 4.1, 4.2）
  - 右ペインのトグルボタンで折りたたみ/展開が動作すること（5.1, 5.2, 5.3, 5.4）
  - PNG prompt メタデータが右ペインに表示されること（6.1, 6.2, 6.3, 6.4）
  - _Requirements: 1.1, 1.2, 1.3, 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 3.1, 3.2, 3.3, 3.4, 4.1, 4.2, 4.3, 5.1, 5.2, 5.3, 5.4, 6.1, 6.2, 6.3, 6.4_

- [ ]* 8.2 PngMetadataReader のユニットテストを実装する
  - `prompt` tEXt チャンクを持つテスト用 PNG で正しい値が返ることを確認する
  - `prompt` チャンクを持たない PNG で `Optional.empty()` が返ることを確認する
  - _Requirements: 6.1, 6.2_
