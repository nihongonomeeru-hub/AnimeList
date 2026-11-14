package com.example.animelist.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.animelist.data.local.repository.PersonDetailRepository
import com.example.animelist.data.local.repository.PersonDetailRepositoryImpl
import com.example.animelist.data.network.NetworkModule
import com.example.animelist.view_model.uiState.CharacterDetailUiState
import com.example.animelist.view_model.uiState.StaffDetailUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class PersonDetailViewModel(
    private val personDetailRepository: PersonDetailRepository = PersonDetailRepositoryImpl(
        NetworkModule.apolloClient
    )
): ViewModel() {
    private val _characterState = MutableStateFlow<CharacterDetailUiState>(CharacterDetailUiState.Loading)
    val characterState: StateFlow<CharacterDetailUiState> = _characterState.asStateFlow()
    private var characterJob: Job? = null

    private val _staffState = MutableStateFlow<StaffDetailUiState>(StaffDetailUiState.Loading)
    val staffState: StateFlow<StaffDetailUiState> = _staffState.asStateFlow()
    private var staffJob: Job? = null

    fun loadCharacterDetail(characterId: Int) {
        characterJob?.cancel()
        characterJob = viewModelScope.launch {
            _characterState.value = CharacterDetailUiState.Loading
            personDetailRepository.getCharacterDetail(characterId)
                .onSuccess { character ->
                    _characterState.value = CharacterDetailUiState.Success(character)
                }
                .onFailure { throwable ->
                    _characterState.value = CharacterDetailUiState.Error(throwable.toUserMessage())
                }
        }
    }

    fun loadStaffDetail(staffId: Int) {
        staffJob?.cancel()
        staffJob = viewModelScope.launch {
            _staffState.value = StaffDetailUiState.Loading
            personDetailRepository.getStaffDetail(staffId)
                .onSuccess { staff ->
                    _staffState.value = StaffDetailUiState.Success(staff)
                }
                .onFailure { throwable ->
                    _staffState.value = StaffDetailUiState.Error(throwable.toUserMessage())
                }
        }
    }

    private fun Throwable.toUserMessage(): String =
        if (this is IOException || this is ApolloNetworkException || cause is IOException) {
            "インターネット接続を確認してください"
        } else {
            localizedMessage ?: "データの取得に失敗しました"
        }
}
