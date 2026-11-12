package com.example.animelist.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animelist.data.network.NetworkModule
import com.example.animelist.data.local.repository.AnimeDetailRepository
import com.example.animelist.data.local.repository.AnimeDetailRepositoryImpl
import com.example.animelist.view_model.uiState.CharactersCastUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CharacterCastViewModel(
    private val repository: AnimeDetailRepository = AnimeDetailRepositoryImpl(NetworkModule.apolloClient)
): ViewModel() {

    private val _uiState = MutableStateFlow<CharactersCastUiState>(CharactersCastUiState.Loading)
    val uiState: StateFlow<CharactersCastUiState> = _uiState.asStateFlow()

    fun loadCharactersAndCast(animeId: Int) {
        viewModelScope.launch {
            _uiState.value = CharactersCastUiState.Loading
            repository.getCharacterAndCast(animeId)
                .onSuccess { data ->
                    _uiState.value = CharactersCastUiState.Success(data)
                }
                .onFailure { throwable ->
                    _uiState.value = CharactersCastUiState.Error(
                        throwable.message ?: "データが取得できませんでした")
                }
        }
    }
}