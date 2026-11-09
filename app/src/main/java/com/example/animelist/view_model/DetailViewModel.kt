package com.example.animelist.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animelist.data.network.NetworkModule
import com.example.animelist.view_model.reposiroty.AnimeDetailRepository
import com.example.animelist.view_model.uiState.DetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class DetailViewModel(
    private val repository: AnimeDetailRepository = AnimeDetailRepository(NetworkModule.apolloClient)
): ViewModel() {
    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadAnimeDetail(animeId: Int) {
        _uiState.value = DetailUiState.Loading
        viewModelScope.launch {
            try {
                val anime = repository.getAnimeDetail(animeId)
                _uiState.value = DetailUiState.Success(anime)
            } catch (e: IOException) {
                _uiState.value = DetailUiState.Error("インターネット接続を確認してください")
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(e.localizedMessage ?: "データの取得に失敗しました")
            }
        }
    }
}