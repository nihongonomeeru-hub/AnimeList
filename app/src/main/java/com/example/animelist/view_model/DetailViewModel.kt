package com.example.animelist.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.animelist.data.network.NetworkModule
import com.example.animelist.data.local.repository.AnimeDetailRepository
import com.example.animelist.data.local.repository.AnimeDetailRepositoryImpl
import com.example.animelist.graphql.type.MediaType
import com.example.animelist.view_model.uiState.DetailUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class DetailViewModel(
    private val repository: AnimeDetailRepository = AnimeDetailRepositoryImpl(NetworkModule.apolloClient)
): ViewModel() {
    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    private var fetchJob: Job? = null
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadAnimeDetail(
        animeId: Int,
        mediaType: MediaType = MediaType.ANIME
    ) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            repository.getAnimeDetail(
                animeId,
                mediaType
            )
                .onSuccess { anime ->
                    _uiState.value = DetailUiState.Success(anime)
                }
                .onFailure { throwable ->
                    val message = if(
                        throwable is IOException ||
                        throwable is ApolloNetworkException ||
                        throwable.cause is IOException
                    ) {
                        "インターネット接続を確認してください"
                    } else {
                        throwable.localizedMessage ?: "データの取得に失敗しました"
                    }

                    _uiState.value = DetailUiState.Error(message)
                }
        }
    }
}