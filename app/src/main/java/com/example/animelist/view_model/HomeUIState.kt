package com.example.animelist.view_model

import com.example.animelist.ui.model.AnimeSection

sealed interface HomeUIState {
    object Loading: HomeUIState
    data class Success(val animeList: List<AnimeSection>): HomeUIState
    data class Error(val message: String): HomeUIState
}