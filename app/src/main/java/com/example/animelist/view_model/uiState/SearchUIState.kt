package com.example.animelist.view_model.uiState

import com.example.animelist.ui.model.AnimeItem

sealed interface SearchUIState {

    object Loading: SearchUIState
    data class Success(val animeList: List<AnimeItem>): SearchUIState

    data class Default(val trendingAnime: List<AnimeItem>): SearchUIState
    data class Error(val message: String): SearchUIState
}