# AnimeList (アニメリストアプリ)

> Jetpack ComposeとモダンAndroidアーキテクチャ（MVVM / UDF）を採用した、アニメ作品の閲覧・検索・お気に入り管理を行うAndroidアプリケーションです。

---

## 📱 Screenshots (スクリーンショット)

| Home | Detail | My List |
| :---: | :---: | :---: |
| ![Home](art/home_page.png) | ![Detail](art/detail_page.png) | ![My List](art/my_list.png) |

| Details Added | Search | Search Result |
| :---: | :---: | :---: |
| ![Details Added](art/my_list_added_anime.png) | ![Search](art/search_page.png) | ![Search Result](art/search_result.png) |

---

## 🎬 Feature Demos (機能デモ)

| Home & Transitions | Category Browsing | Instant Search | Favorites (Room DB) |
| :---: | :---: | :---: | :---: |
| ![Home Demo](art/app1.gif) | ![Category Demo](art/app2.gif) | ![Search Demo](art/app3.gif) | ![Favorites Demo](art/app4.gif) |
| **ホーム・バナー遷移** | **カテゴリー一覧** | **インクリメンタル検索** | **マイリスト・DB永続化** |

---

## 🌟 Key Features (主な機能)

* **ホーム画面 (Home):**
  * 注目の作品（Hero Banner）およびカテゴリー別セクション一覧のカルーセル表示
  * セクション詳細画面へのスムーズなスライド遷移（`AnimatedVisibility`）
  * ボトムナビゲーション再タップ時のトップ自動スクロール（`animateScrollToItem`）
* **インタラクティブな詳細画面 (Shared Element / Zoom Effect):**
  * ポスターカードの位置に合わせた滑らかな拡大・縮小トランジション（`AnimatedContent`）
  * 作品情報（あらすじ、エピソード数、ジャンルタグ、評価スター）の確認
* **リアルタイム検索 (Search):**
  * 検索キーワードに応じたインクリメンタルフィルタリング
  * 画面遷移や画面回転時にも検索状態を保持する状態管理（`SearchViewModel`）
* **マイリスト管理 (Favorites):**
  * ワンタップでお気に入り追加・削除（ローカルストレージ / Room連携）
  * リストが空の場合のプレースホルダー表示
* **UX最適化:**
  * 誤終了防止のための「戻る」ボタン2度押し検知とトースト通知