package com.example.animelist.view_model.uiState

import com.example.animelist.ui.model.CharacterVoiceActorUIModel

sealed interface CharactersCastUiState {
    data object Loading: CharactersCastUiState
    data class Success(
        val castList: List<CharacterVoiceActorUIModel>
    ): CharactersCastUiState
    data class Error(val message: String): CharactersCastUiState
}