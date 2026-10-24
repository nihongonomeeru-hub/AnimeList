package com.example.animelist.view_model

import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animelist.ui.model.AnimeItem
import com.example.animelist.ui.model.AnimeSection
import kotlinx.coroutines.launch

class HomePageViewModel: ViewModel() {
    var selectedAnime by mutableStateOf<AnimeItem?>(null)
        private set

    val listState = LazyListState()

    private val rawStates = mutableMapOf<Int, LazyListState>()

    fun getRawState(sectionId: Int): LazyListState  {
        return rawStates.getOrPut(sectionId) { LazyListState() }
    }

    fun selectedAnime(anime: AnimeItem) {
        selectedAnime = anime
    }

    fun clearSelection() {
       selectedAnime = null
    }

    var selectedSection by mutableStateOf<AnimeSection?>(null)

    fun openSection(section: AnimeSection) {
        selectedSection = section
    }

    fun closeSection() {
        selectedSection = null
    }


}