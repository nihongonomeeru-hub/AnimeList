# AnimeList (アニメリストアプリ)

> Jetpack ComposeとモダンAndroidアーキテクチャ（MVVM / UDF）を採用した、アニメ作品の閲覧・検索・お気に入り管理を行うAndroidアプリケーションです。

---

## 📱 Screenshots (スクリーンショット)

|               Home                |                Detail                 |             My List              |
|:---------------------------------:|:-------------------------------------:|:--------------------------------:|
| ![Home Screen](art/home_page.png) | ![Detail Screen](art/detail_page.png) | ![My List Page](art/my_list.png) |

|                 Details Added                 |               Search                |              Search Result              |
|:---------------------------------------------:|:-----------------------------------:|:---------------------------------------:|
| ![Anime Details](art/my_list_added_anime.png) | ![Search Page](art/search_page.png) | ![Search Result](art/search_result.png) |

|          App Gif          |                App Gif                |
|:-------------------------:|:-------------------------------------:|
| ![App Vieo](art/app1.gif) |       ![App Vieo](art/app2.gif)       |
|          App Gif          |                App Gif                |
|           :---:           |                 :---:                 |
| ![App Vieo](art/app3.gif) |       ![App Vieo](art/app4.gif)       |

---

## 🌟 Key Features (主な機能)

* **ホーム画面 (Home)**:
    * 注目の作品（Hero Banner）およびカテゴリー別セクション一覧のカルーセル表示
    * セクション詳細画面へのスムーズなスライド遷移（`AnimatedVisibility`）
    * ボトムナビゲーション再タップ時のトップ自動スクロール（`animateScrollToItem`）
* **インタラクティブな詳細画面 (Shared Element / Zoom Effect)**:
    * ポスターカードの位置に合わせた滑らかな拡大・縮小トランジション（`AnimatedContent`）
    * 作品情報（あらすじ、エピソード数、ジャンルタグ、評価スター）の確認
* **リアルタイム検索 (Search)**:
    * 検索キーワードに応じたインクリメンタルフィルタリング
    * 画面遷移や画面回転時にも検索状態を保持する状態管理（`SearchViewModel`）
* **マイリスト管理 (Favorites)**:
    * ワンタップでお気に入り追加・削除（ローカルストレージ / Room連携）
    * リストが空の場合のプレースホルダー表示
* **UX最適化**:
    * 誤終了防止のための「戻る」ボタン2度押し検知とトースト通知

---

## 🛠 Tech Stack & Architecture (技術スタック・設計)

### Architecture
* **MVVM (Model-View-ViewModel)**
* **UDF (Unidirectional Data Flow)** による予測可能でクリーンな状態管理
* **Stateful / Stateless の分離**: UIコンポーネント（Content）をViewModelから疎結合にし、プレビュー・テスト容易性を確保

### Libraries & Tools
* **Language**: Kotlin 100%
* **UI**: Jetpack Compose, Material 3
* **Asynchronous**: Kotlin Coroutines & StateFlow
* **Lifecycle**: `collectAsStateWithLifecycle`
* **Local Storage**: Room Database / SQLite
* **Image Loading**: Coil (AsyncImage)
* **Animation**: Compose Animation (`AnimatedContent`, `AnimatedVisibility`)

---

## 📂 Project Structure (パッケージ構成)

```text
com.example.animelist/
├── data/              # Room Entity, DAO, Repository
├── model/             # UI用データクラス (AnimeItem, Section など)
├── view_model/        # 各画面のViewModel (HomePageViewModel, SearchViewModel, FavoriteViewModel)
└── ui/
    ├── components/    # 共通UIコンポーネント (AnimeCard, HeroBanner, AppBottomBar)
    ├── navigation/    # ナビゲーション定義 (BottomNavItem)
    └── screens/       # 各画面 (MainScreen, DetailScreen, main_screens/*)