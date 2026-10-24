package com.example.animelist.view_model

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animelist.data.network.NetworkModule
import com.example.animelist.data.network.model.KitsuAnimeDto
import com.example.animelist.ui.model.AnimeItem
import com.example.animelist.ui.model.AnimeSection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.IOException

class HomePageViewModel: ViewModel() {

    val animeList: List<AnimeItem>
        get() = when (val state = uiState) {
            is HomeUIState.Success -> state.animeList.flatMap { it.items }.distinctBy { it.id }
            else -> emptyList()
        }

    var uiState: HomeUIState by mutableStateOf(HomeUIState.Loading)
        private set
    var selectedAnime by mutableStateOf<AnimeItem?>(null)
        private set

    var selectedSection by mutableStateOf<AnimeSection?>(null)
        private set

    val listState = LazyListState()

    private val rawStates = mutableMapOf<Int, LazyListState>()

    fun getRawState(sectionId: Int): LazyListState  {
        return rawStates.getOrPut(sectionId) { LazyListState() }
    }

    init {
        fetchHomeSections()
    }

    fun fetchHomeSections() {
        uiState = HomeUIState.Loading
        viewModelScope.launch {
            try {
                val sections = withContext(Dispatchers.IO) {
                    val popularDeferred = async { NetworkModule.apiService.getPopularAnime() }
                    val topRatedDeferred = async { NetworkModule.apiService.getTopRatedAnime() }
                    val airingDeferred = async { NetworkModule.apiService.getAiringAnime() }
                    val actionDeferred = async { NetworkModule.apiService.getActionAnime() }
                    val comedyDeferred = async { NetworkModule.apiService.getComedyAnime() }

                    listOf(
                        AnimeSection(
                            id = 1,
                            title = "人気アニメ",
                            items = popularDeferred.await().data.toAnimeItems(
                                defaultGenres = listOf("人気", "アクション", "ドラマ")
                            )
                        ),
                        AnimeSection(
                            id = 2,
                            title = "高評価アニメ",
                            items = topRatedDeferred.await().data.toAnimeItems(
                                defaultGenres = listOf("名作", "高評価")
                            )
                        ),
                        AnimeSection(
                            id = 3,
                            title = "今期のアニメ",
                            items = airingDeferred.await().data.toAnimeItems(
                                defaultGenres = listOf("放送中", "最新作")
                            )
                        ),
                        AnimeSection(
                            id = 4,
                            title = "アクション特集",
                            items = actionDeferred.await().data.toAnimeItems(
                                defaultGenres = listOf("バトル", "冒険", "少年")
                            )
                        ),
                        AnimeSection(
                            id = 5,
                            title = "コメディ",
                            items = comedyDeferred.await().data.toAnimeItems(
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

    private fun List<KitsuAnimeDto>.toAnimeItems(defaultGenres: List<String> = listOf("アニメ")): List<AnimeItem> {
        return this.map { dto ->
            val attr = dto.attributes

            val parsedRating = attr?.averageRating?.toDoubleOrNull()?.let { score ->
                String.format(java.util.Locale.US, "%.1f", score / 10.0).toDoubleOrNull()
            }

            val textDesc = (attr?.synopsis ?: attr?.synopsis)?.trim()
                ?.takeIf { it.isNotBlank() }



            val parsedYear = attr?.startDate
                ?.take(4)
                ?.toIntOrNull()

            AnimeItem(
                id = dto.id?.toIntOrNull() ?: 0,
                stringResourceId = attr?.titles?.jaJp ?: attr?.canonicalTitle ?: "タイトル不明",
                imageResourceId = attr?.posterImage?.large ?: attr?.posterImage?.medium ?: "",
                rating = parsedRating,
                description = textDesc.toString(),
                genres = defaultGenres,
                episodes = attr?.episodeCount,
                year = parsedYear
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