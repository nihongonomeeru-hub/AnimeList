package com.example.animelist.view_model.uiState

import com.example.animelist.ui.model.AnimeItem
import com.example.animelist.ui.model.AnimeSection

sealed interface HomeUIState {
    object Loading: HomeUIState
    data class Success(
        val animeList: List<AnimeSection>,
        val dailyRecommendation: AnimeItem? = null
    ): HomeUIState
    data class Error(val message: String): HomeUIState
}