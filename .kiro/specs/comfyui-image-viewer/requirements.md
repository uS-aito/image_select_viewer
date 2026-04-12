# Requirements Document

## Introduction
ComfyUI で生成した PNG 画像を閲覧・管理するための Java Swing デスクトップアプリケーション。起動時はフォルダ未選択の空状態で表示し、メニューバーの File > Open Folder から対象フォルダを選択できる。サムネイル一覧・フルサイズ表示・生成プロンプトの確認を単一ウィンドウで提供する。

## Boundary Context
- **In scope**: メニューバー、フォルダ選択（OS標準ダイアログ）、起動時空状態、PNG サムネイル一覧、フルサイズ PNG 表示、`prompt` メタデータの表示、右ペインの折りたたみ/展開
- **Out of scope**: ズーム・回転などの画像操作（`image-zoom-control` スペックが担当）、`workflow` メタデータの表示、PNG 以外の画像フォーマット、画像のファイル操作（削除・移動・リネーム）
- **Adjacent expectations**: ファイルシステムが対象フォルダへの読み取りアクセスを提供すること; PNG ファイルが ComfyUI 標準のメタデータ形式を持つことを前提とするが、持たない場合も適切に処理すること; ズームコントロールは `image-zoom-control` スペックが提供する

## Requirements

### Requirement 1: 起動時の空状態表示

**Objective:** As a ComfyUI ユーザー, I want アプリ起動直後はフォルダ未選択の空状態でウィンドウが開く, so that フォルダ選択を強制されることなくアプリを起動できる

#### Acceptance Criteria
1. When アプリケーションが起動する, the Image Viewer shall フォルダ選択ダイアログを表示せずメインウィンドウを空状態で表示する
2. The Image Viewer shall 起動直後（フォルダ未選択時）はサムネイルリストを空で表示する
3. The Image Viewer shall 起動直後（フォルダ未選択時）は中央ペインを空白で表示する

### Requirement 2: メニューバーによるフォルダ選択

**Objective:** As a ComfyUI ユーザー, I want メニューバーの File > Open Folder から任意のタイミングでフォルダを選択できる, so that 対象フォルダを柔軟に変更しながら画像を閲覧できる

#### Acceptance Criteria
1. The Image Viewer shall メニューバーを表示し、`File` メニューに `Open Folder` メニュー項目を含める
2. When ユーザーが `File > Open Folder` を選択する, the Image Viewer shall OS標準のフォルダ選択ダイアログを表示する
3. The Image Viewer shall フォルダ選択ダイアログでディレクトリのみを選択可能にする
4. When ユーザーがフォルダ選択ダイアログでフォルダを選択し確定する, the Image Viewer shall 選択されたフォルダ内の PNG ファイルをサムネイルリストに読み込む
5. When ユーザーがフォルダ選択ダイアログをキャンセルする, the Image Viewer shall ダイアログを閉じ現在の表示状態を維持する（アプリを終了しない）
6. When 既にフォルダが読み込まれている状態でユーザーが新しいフォルダを選択し確定する, the Image Viewer shall 現在のサムネイルリストをクリアし新しいフォルダの PNG ファイルを読み込む

### Requirement 3: サムネイルリスト

**Objective:** As a ComfyUI ユーザー, I want 選択フォルダ内の PNG 画像をサムネイル一覧で確認できる, so that 目的の画像を素早く見つけて選択できる

#### Acceptance Criteria
1. The Image Viewer shall 選択フォルダ内の PNG ファイルのみをサムネイルとして左ペインに表示する
2. When PNG ファイルの読み込みが開始される, the Image Viewer shall サムネイルをバックグラウンドで読み込み順次表示する
3. If あるファイルが PNG フォーマットでない, the Image Viewer shall そのファイルをサムネイルリストに表示しない
4. When ユーザーがサムネイルリスト内の画像を選択する, the Image Viewer shall 選択された画像を中央ペインに表示する

### Requirement 4: フルサイズ画像表示

**Objective:** As a ComfyUI ユーザー, I want 選択した画像をフルサイズで確認できる, so that 生成画像の詳細を確認できる

#### Acceptance Criteria
1. When ユーザーがサムネイルリストで画像を選択する, the Image Viewer shall 選択された PNG 画像をフルサイズで中央ペインに表示する
2. While 画像が中央ペインに表示されている and 画像がペインのサイズより大きい, the Image Viewer shall スクロールバーを表示する
3. The Image Viewer shall フォルダ未選択時および画像未選択時は中央ペインを空白で表示する

### Requirement 5: 右ペインの折りたたみ制御

**Objective:** As a ComfyUI ユーザー, I want 右ペインを折りたたみ/展開できる, so that 画像表示領域を柔軟に調整できる

#### Acceptance Criteria
1. The Image Viewer shall 中央ペインの右端にトグルボタンを常時表示する
2. When ユーザーが右ペインが閉じた状態でトグルボタンをクリックする, the Image Viewer shall 右ペインを展開しトグルボタンのラベルを `◀` に変更する
3. When ユーザーが右ペインが開いた状態でトグルボタンをクリックする, the Image Viewer shall 右ペインを折りたたみトグルボタンのラベルを `▶` に変更する
4. The Image Viewer shall アプリケーション起動時に右ペインを折りたたんだ状態で表示する

### Requirement 6: PNG メタデータ表示

**Objective:** As a ComfyUI ユーザー, I want 生成に使用したプロンプトを確認できる, so that 画像の生成パラメータを把握できる

#### Acceptance Criteria
1. While 右ペインが展開されている, When ユーザーが画像を選択する, the Image Viewer shall 選択された PNG ファイルの `prompt` メタデータを右ペインに表示する
2. While 右ペインが展開されている, If 選択された PNG ファイルに `prompt` メタデータが存在しない, the Image Viewer shall 右ペインにメタデータが存在しない旨のメッセージを表示する
3. While 右ペインが折りたたまれている, When ユーザーが画像を選択する, the Image Viewer shall 折りたたみ状態を維持し右ペインに情報を表示しない
4. When 右ペインが展開され画像が選択されている, the Image Viewer shall `prompt` メタデータをスクロール可能な形式で表示する
