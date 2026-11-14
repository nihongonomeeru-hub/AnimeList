package com.example.animelist.view_model.uiState

import com.example.animelist.ui.model.AnimeItem
import com.example.animelist.ui.model.CharacterDetailUIModel
import com.example.animelist.ui.model.StaffDetailUIModel

sealed interface CharacterDetailUiState {
    data object Loading : CharacterDetailUiState
    data class Success(val character: CharacterDetailUIModel) : CharacterDetailUiState
    data class Error(val message: String) : CharacterDetailUiState
}

sealed interface StaffDetailUiState {
    data object Loading : StaffDetailUiState
    data class Success(val staff: StaffDetailUIModel) : StaffDetailUiState
    data class Error(val message: String) : StaffDetailUiState
}