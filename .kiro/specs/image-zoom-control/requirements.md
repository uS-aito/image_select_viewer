# Requirements Document

## Introduction
ComfyUI で生成した PNG 画像ビューワーの中央ペインにズームコントロールツールバーを追加する。現在は画像がウィンドウサイズに合わせてフィット表示されるのみで拡大縮小操作ができないため、ユーザーが任意の倍率で画像を確認できるようにする。

## Boundary Context
- **In scope**: 中央ペイン下部のズームツールバー（フィットボタン・倍率プルダウン・ズームスライダー）、3コントロール間の相互同期、画像の拡大縮小表示
- **Out of scope**: マウスホイールによるズーム、キーボードショートカット、ズーム状態の永続化、画像の回転・反転、フォルダ選択・メニューバー（comfyui-image-viewer スペックが担当）
- **Adjacent expectations**: 中央ペインの画像表示機能（comfyui-image-viewer スペック）が提供する JScrollPane + 画像表示の基盤が存在すること

## Requirements

### Requirement 1: ズームツールバーの常時表示

**Objective:** As a ComfyUI ユーザー, I want 中央ペインの下部にズームコントロールツールバーが常に表示される, so that 画像を閲覧中いつでもズーム操作にアクセスできる

#### Acceptance Criteria
1. The Image Viewer shall 中央ペインの下部にズームツールバーを常時表示する
2. The Image Viewer shall ズームツールバーにフィットボタン、倍率プルダウン、ズームスライダーを含める
3. The Image Viewer shall 画像が選択されていない状態でもズームツールバーを表示する

### Requirement 2: フィット表示ボタン

**Objective:** As a ComfyUI ユーザー, I want フィットボタンをクリックして画像をウィンドウサイズに合わせる, so that 画像全体をペイン内で確認できる

#### Acceptance Criteria
1. When ユーザーがフィットボタンをクリックする, the Image Viewer shall 画像をスクロールペインのサイズに収まる倍率で表示する
2. When ユーザーがフィットボタンをクリックする, the Image Viewer shall 倍率プルダウンとスライダーをフィット倍率に対応する値に更新する
3. The Image Viewer shall 画像を新規選択したとき自動的にフィット表示を適用する

### Requirement 3: 倍率プルダウン

**Objective:** As a ComfyUI ユーザー, I want プルダウンから倍率を選択して画像サイズを変更できる, so that 特定の倍率でピクセル精度の確認ができる

#### Acceptance Criteria
1. The Image Viewer shall 倍率プルダウンに 10%, 25%, 50%, 75%, 100%, 200%, 300%, 400%, 500%, 600%, 700%, 800% の選択肢を表示する
2. When ユーザーが倍率プルダウンから倍率を選択する, the Image Viewer shall 画像を選択した倍率で表示する
3. When ユーザーが倍率プルダウンから倍率を選択する, the Image Viewer shall ズームスライダーを選択した倍率に対応する位置に更新する
4. The Image Viewer shall 現在のズーム倍率を倍率プルダウンに表示する

### Requirement 4: ズームスライダー

**Objective:** As a ComfyUI ユーザー, I want スライダーを操作して画像を連続的に拡大縮小できる, so that 細かい倍率調整が直感的に行える

#### Acceptance Criteria
1. The Image Viewer shall ズームスライダーの操作範囲を 10% から 800% に合わせる
2. When ユーザーがズームスライダーを操作する, the Image Viewer shall 画像を操作中の倍率でリアルタイムに拡大縮小して表示する
3. When ユーザーがズームスライダーを操作する, the Image Viewer shall 倍率プルダウンを現在の倍率に最も近い選択肢に更新する
4. The Image Viewer shall ズームスライダーの両端に縮小ボタン（⊖）と拡大ボタン（⊕）を表示する
5. When ユーザーが縮小ボタン（⊖）をクリックする, the Image Viewer shall ズーム倍率をプルダウンの一段階小さい選択肢に変更する
6. When ユーザーが拡大ボタン（⊕）をクリックする, the Image Viewer shall ズーム倍率をプルダウンの一段階大きい選択肢に変更する

### Requirement 5: ズーム変更時のスクロール動作

**Objective:** As a ComfyUI ユーザー, I want ズームで拡大した画像をスクロールして閲覧できる, so that 拡大表示時に画像全体を確認できる

#### Acceptance Criteria
1. While 画像がスクロールペインより大きい倍率で表示されている, the Image Viewer shall スクロールバーを表示する
2. When ズームで画像がスクロールペインより小さくなる, the Image Viewer shall スクロールバーを非表示にする
