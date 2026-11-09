package com.example.animelist.view_model.uiState

import com.example.animelist.ui.model.AnimeItem

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val anime: AnimeItem) : DetailUiState
    data class Error(val message: String) : DetailUiState
}