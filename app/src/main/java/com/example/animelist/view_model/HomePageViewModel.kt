package com.example.animelist.view_model

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.animelist.data.local.repository.HomeRepository
import com.example.animelist.data.local.repository.HomeRepositoryImpl
import com.example.animelist.data.network.NetworkModule
import com.example.animelist.ui.model.AnimeItem
import com.example.animelist.ui.model.AnimeSection
import com.example.animelist.view_model.uiState.HomeUIState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.LocalDate
import java.time.Year
import kotlin.collections.orEmpty

class HomePageViewModel(
    private val repository: HomeRepository = HomeRepositoryImpl(NetworkModule.apolloClient)
): ViewModel() {
    var uiState: HomeUIState by mutableStateOf(HomeUIState.Loading)
        private set
    var selectedAnime by mutableStateOf<AnimeItem?>(null)
        private set

    var selectedSection by mutableStateOf<AnimeSection?>(null)
        private set

    val listState = LazyListState()

    val dailyRecommendation: AnimeItem?
        get() = when(val state = uiState) {
            is HomeUIState.Success -> {
                val recSection = state.animeList.find { it.id == RECOMMENDATION_SECTION_ID }?.items.orEmpty()
                getRecommendationOfDay(recSection)
            }

            else -> null
        }

    private var fetchJob: Job? = null

    private val rawStates = mutableMapOf<Int, LazyListState>()

    fun getRawState(sectionId: Int): LazyListState  {
        return rawStates.getOrPut(sectionId) { LazyListState() }
    }

    init {
        fetchHomeSections()
    }

    fun fetchHomeSections() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            uiState = HomeUIState.Loading

            val currentYear = Year.now().value

            repository.getHomeSections(currentYear)
                .onSuccess { anime ->
                    uiState = HomeUIState.Success(anime)
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

                    uiState = HomeUIState.Error(message)
                }
        }
    }

    fun selectedAnime(anime: AnimeItem) {
        selectedAnime = anime
    }

    fun clearSelection() {
        selectedAnime = null
    }

    fun openSection(section: AnimeSection) {
        selectedSection = section
    }

    fun closeSection() {
        selectedSection = null
    }

    private companion object {
        private const val RECOMMENDATION_SECTION_ID = 6
    }
}

fun getRecommendationOfDay(animeList: List<AnimeItem>): AnimeItem? {
    if(animeList.isEmpty()) return null

    val dayOfMonth = LocalDate.now().dayOfMonth
    val dailyIndex = (dayOfMonth - 1) % animeList.size

    return animeList[dailyIndex]
}