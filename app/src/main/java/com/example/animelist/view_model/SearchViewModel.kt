package com.example.animelist.view_model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.animelist.data.local.repository.SearchRepository
import com.example.animelist.data.local.repository.SearchRepositoryImpl
import com.example.animelist.data.network.NetworkModule
import com.example.animelist.ui.model.AnimeItem
import com.example.animelist.view_model.uiState.SearchUIState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.IOException

class SearchViewModel(
    private val repository: SearchRepository = SearchRepositoryImpl(NetworkModule.apolloClient)
): ViewModel() {

    private var defaultAnimeList: List<AnimeItem> = emptyList()

    var uiState: SearchUIState by mutableStateOf(SearchUIState.Loading)
        private set

    var selectedGenre by mutableStateOf<String?>(null)
        private set


    private val _searchQuery = MutableStateFlow("")
    var searchQuery = _searchQuery.asStateFlow()
    private var fetchJob: Job? = null

    init {
        fetchTrendingAnime()
        _searchQuery
            .debounce(SEARCH_DEBOUNCE_MILLIS)
            .distinctUntilChanged()
            .onEach { text ->
                val query = text.trim()
                if (query.isEmpty()) {
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
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            uiState = SearchUIState.Loading

            repository.fetchTrendingAnime()
                .onSuccess { anime ->
                    defaultAnimeList = anime
                    if(_searchQuery.value.isBlank() && selectedGenre == null) {
                        uiState = SearchUIState.Default(defaultAnimeList)
                    }
                }
                .onFailure { throwable ->
                    uiState = SearchUIState.Error(throwable.toErrorMessage())
                }
        }
    }

    fun searchAnime(text: String) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            uiState = SearchUIState.Loading
            repository.searchAnime(text)
                .onSuccess { anime ->
                    uiState = SearchUIState.Success(anime)
                }
                .onFailure { throwable ->
                    uiState = SearchUIState.Error(throwable.toErrorMessage())
                }
        }
    }

    fun searchByGenre(genre: String) {
        if (selectedGenre == genre) {
            selectedGenre = null
            fetchJob?.cancel()
            uiState = SearchUIState.Default(defaultAnimeList)
            return
        }

        selectedGenre = genre
        _searchQuery.value = ""

        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            uiState = SearchUIState.Loading
            repository.searchByGenre(genre)
                .onSuccess { anime ->
                    if(selectedGenre == genre) {
                        uiState = SearchUIState.Success(anime)
                    }
                }
                .onFailure { throwable ->
                    uiState = SearchUIState.Error(throwable.toErrorMessage())
                }
        }
    }

    fun clearGenre() {
        fetchJob?.cancel()
        selectedGenre = null
    }

    fun updateQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun clearQuery() {
        fetchJob?.cancel()
        _searchQuery.value = ""
        uiState = SearchUIState.Default(defaultAnimeList)
    }

    private fun Throwable.toErrorMessage(): String =
        if (this is IOException || this is ApolloNetworkException || cause is IOException) {
            "インターネット接続を確認してください"
        } else {
            localizedMessage ?: "データの取得に失敗しました"
        }

    private companion object {
        private const val SEARCH_DEBOUNCE_MILLIS = 400L
    }
}