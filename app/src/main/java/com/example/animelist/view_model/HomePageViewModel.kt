package com.example.animelist.view_model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.animelist.ui.model.AnimeItem

class HomePageViewModel: ViewModel() {
    var selectedAnime by mutableStateOf<AnimeItem?>(null)
        private set

    fun selectedAnime(anime: AnimeItem) {
        selectedAnime = anime
    }

    fun clearSelection() {
       selectedAnime = null
    }
}