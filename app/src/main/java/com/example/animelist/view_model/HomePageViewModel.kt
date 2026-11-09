package com.example.animelist.view_model

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.api.Optional
import com.example.animelist.data.network.NetworkModule
import com.example.animelist.graphql.GetAnimeListQuery
import com.example.animelist.ui.model.AnimeItem
import com.example.animelist.ui.model.AnimeSection
import com.example.animelist.ui.model.TrailerItem
import com.example.animelist.view_model.uiState.HomeUIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.IOException
import java.time.Year
import kotlin.collections.map
import kotlin.collections.orEmpty

class HomePageViewModel: ViewModel() {
    var uiState: HomeUIState by mutableStateOf(HomeUIState.Loading)
        private set
    var selectedAnime by mutableStateOf<AnimeItem?>(null)
        private set

    var selectedSection by mutableStateOf<AnimeSection?>(null)
        private set

    val listState = LazyListState()

    val dailyRecommendation: AnimeItem?
        get() = when(val state = uiState) {
            is HomeUIState.Success -> {
                val recSection = state.animeList.find { it.id == 6 }?.items.orEmpty()
                getRecommendationOfDay(recSection)
            }

            else -> null
        }

    private val rawStates = mutableMapOf<Int, LazyListState>()

    fun getRawState(sectionId: Int): LazyListState  {
        return rawStates.getOrPut(sectionId) { LazyListState() }
    }

    init {
        fetchHomeSections()
    }

    fun fetchHomeSections() {
        val currentYear = Year.now().value
        uiState = HomeUIState.Loading
        viewModelScope.launch {
            try {
                val sections = withContext(Dispatchers.IO) {
                    val response = NetworkModule.apolloClient
                        .query(query = GetAnimeListQuery(year = Optional.present(currentYear)))
                        .execute()

                    val data = response.data ?: throw Exception("データが取得できませんでした")

                    listOf(
                        AnimeSection(
                            id = 1,
                            title = "人気アニメ",
                            items = data.popular?.media?.mapNotNull { it?.animeCardFields }.orEmpty().toAnimeItems(
                                defaultGenres = listOf("人気", "アクション", "ドラマ")
                            )
                        ),
                        AnimeSection(
                            id = 2,
                            title = "高評価アニメ",
                            items = data.topRated?.media?.mapNotNull { it?.animeCardFields }.orEmpty().toAnimeItems(
                                defaultGenres = listOf("名作", "高評価")
                            )
                        ),
                        AnimeSection(
                            id = 3,
                            title = "今年のアニメ",
                            items = data.this_year?.media?.mapNotNull { it?.animeCardFields }.orEmpty().toAnimeItems(
                                defaultGenres = listOf("放送中", "最新作")
                            )
                        ),
                        AnimeSection(
                            id = 4,
                            title = "放送中のアニメ",
                            items =data.airing?.media?.mapNotNull { it?.animeCardFields }.orEmpty().toAnimeItems(
                                defaultGenres = listOf("放送中", "最新作")
                            )
                        ),
                        AnimeSection(
                            id = 5,
                            title = "今期のアニメ",
                            items = data.season?.media?.mapNotNull { it?.animeCardFields }.orEmpty().toAnimeItems(
                                defaultGenres = listOf("今期", "最新作")
                            )
                        ),
                        AnimeSection(
                            id = 6,
                            title = "おすすめ",
                            items = data.recommendations?.media?.mapNotNull { it?.animeCardFields }.orEmpty().toAnimeItems(
                                defaultGenres = listOf("今期", "最新作")
                            )
                        ),
                        AnimeSection(
                            id = 7,
                            title = "アクション特集",
                            items = data.action?.media?.mapNotNull { it?.animeCardFields }.orEmpty().toAnimeItems(
                                defaultGenres = listOf("バトル", "冒険", "少年")
                            )
                        ),
                        AnimeSection(
                            id = 8,
                            title = "コメディ",
                            items = data.comedy?.media?.mapNotNull { it?.animeCardFields }.orEmpty().toAnimeItems(
                                defaultGenres = listOf("コメディ")
                            )
                        )
                    )
                }

                uiState = HomeUIState.Success(sections)
            } catch (e: IOException) {
                uiState = HomeUIState.Error("インターネット接続を確認してください")
            } catch (e: Exception) {
                uiState = HomeUIState.Error(e.localizedMessage ?: "データの取得に失敗しました")
            }
        }
    }

    private fun List<com.example.animelist.graphql.fragment.AnimeCardFields>.toAnimeItems(
        defaultGenres: List<String> = listOf("アニメ")
    ): List<AnimeItem> {
        return this.map { fields ->
            val parsedRating = fields.weightedScore?.let { score ->
                String.format(java.util.Locale.US, "%.1f", score / 10.0).toDoubleOrNull()
            }

            val titleName = fields.title?.native
                ?: fields.title?.romaji
                ?: fields.title?.english
                ?: "タイトル不明"

            AnimeItem(
                id = fields.id,
                stringResourceId = titleName,
                imageResourceId = fields.coverImage?.large ?: fields.coverImage?.medium ?: "",
                rating = parsedRating,
                description = fields.description?.trim().orEmpty(),
                genres = fields.genres?.filterNotNull()?.takeIf { it.isNotEmpty() } ?: defaultGenres,
                episodes = fields.episodes,
                year = fields.seasonYear
            )
        }
    }

    fun selectedAnime(anime: AnimeItem) {
        selectedAnime = anime
    }

    fun clearSelection() {
        selectedAnime = null
    }

    fun openSection(section: AnimeSection) {
        selectedSection = section
    }

    fun closeSection() {
        selectedSection = null
    }
}

fun getRecommendationOfDay(animeList: List<AnimeItem>): AnimeItem? {
    if(animeList.isEmpty()) return null

    val dayOfMonth = java.time.LocalDate.now().dayOfMonth
    val dailyIndex = (dayOfMonth - 1) % animeList.size

    return animeList[dailyIndex]
}