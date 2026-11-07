package com.example.animelist.view_model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.api.Optional
import com.example.animelist.data.network.NetworkModule
import com.example.animelist.graphql.SearchAnimeQuery
import com.example.animelist.graphql.fragment.AnimeCardFields
import com.example.animelist.ui.model.AnimeItem
import com.example.animelist.view_model.uiState.SearchUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okio.IOException

class SearchViewModel: ViewModel() {

    private var defaultAnimeList: List<AnimeItem> = emptyList()

    var uiState: SearchUIState by mutableStateOf(SearchUIState.Loading)
        private set

    var selectedGenre by mutableStateOf<String?>(null)
        private set


    private val _searchQuery = MutableStateFlow("")
    var searchQuery = _searchQuery.asStateFlow()

    init {
        fetchTrendingAnime()
        _searchQuery
            .debounce(400)
            .distinctUntilChanged()
            .onEach { text ->
                val query = text.trim()
                if(query.isEmpty()) {
                    if (selectedGenre == null) {
                        uiState = SearchUIState.Default(defaultAnimeList)
                    }
                } else {
                    selectedGenre = null
                    searchAnime(query)
                }
            }
            .launchIn(viewModelScope)
    }

    fun fetchTrendingAnime() {
        uiState = SearchUIState.Loading
        viewModelScope.launch {
            try {
                val response = NetworkModule.apolloClient
                    .query(
                        SearchAnimeQuery(
                            search = Optional.absent(),
                            page = Optional.present(1),
                            perPage = Optional.present(50),
                            genre = Optional.absent()
                        )
                    )
                    .execute()

                val data = response.data ?: throw Exception("データが取得できませんでした")
                val items = data.Page?.media?.mapNotNull { it?.animeCardFields }?.toAnimeItems().orEmpty()

                defaultAnimeList = items
                if (_searchQuery.value.isBlank()) {
                    uiState = SearchUIState.Default(defaultAnimeList)
                }
            } catch (e: IOException) {
                uiState = SearchUIState.Error("インターネット接続を確認してください")
            } catch (e: Exception) {
                uiState = SearchUIState.Error(e.localizedMessage ?: "データの取得に失敗しました")
            }
        }
    }

    fun searchAnime(text: String) {
        uiState = SearchUIState.Loading
        viewModelScope.launch {
            try {
                val response = NetworkModule.apolloClient
                    .query(
                        SearchAnimeQuery(
                            search = Optional.present(text),
                            page = Optional.present(1),
                            perPage = Optional.present(50),
                            genre = Optional.absent()
                        )
                    )
                    .execute()

                val data = response.data ?: throw Exception("データが取得できませんでした")
                val items = data.Page?.media?.mapNotNull { it?.animeCardFields }?.toAnimeItems().orEmpty()

                uiState = SearchUIState.Success(items)
            } catch (e: IOException) {
                uiState = SearchUIState.Error("インターネット接続を確認してください")
            } catch (e: Exception) {
                uiState = SearchUIState.Error(e.localizedMessage ?: "データの取得に失敗しました")
            }
        }
    }

    fun searchByGenre(genre: String) {
        if (selectedGenre == genre) {
            selectedGenre = null
            uiState = SearchUIState.Default(defaultAnimeList)
            return
        }

        selectedGenre = genre
        _searchQuery.value = ""

        uiState = SearchUIState.Loading
        viewModelScope.launch {
            try {
                val response = NetworkModule.apolloClient
                    .query(
                        SearchAnimeQuery(
                            search = Optional.absent(),
                            page = Optional.present(1),
                            perPage = Optional.present(50),
                            genre = Optional.present(genre)
                        )
                    )
                    .execute()

                val data = response.data ?: throw Exception("データが取得できませんでした")
                val items = data.Page?.media?.mapNotNull { it?.animeCardFields }?.toAnimeItems().orEmpty()

                if (selectedGenre == genre) {
                    uiState = SearchUIState.Success(items)
                }
            } catch (e: IOException) {
                uiState = SearchUIState.Error("インターネット接続を確認してください")
            } catch (e: Exception) {
                uiState = SearchUIState.Error(e.localizedMessage ?: "データの取得に失敗しました")
            }
        }
    }

    fun updateQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun clearQuery() {
        _searchQuery.value = ""
        uiState = SearchUIState.Default(defaultAnimeList)
    }

    private fun List<AnimeCardFields>.toAnimeItems(): List<AnimeItem> {
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
                genres = fields.genres?.filterNotNull().orEmpty(),
                episodes = fields.episodes,
                year = fields.seasonYear
            )
        }
    }
    }