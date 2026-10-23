package com.example.animelist.view_model

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.animelist.ui.model.AnimeItem

class FavoriteViewModel: ViewModel() {
    val favoriteAnime = mutableStateListOf<AnimeItem>()

    fun toggleFavorite(animeItem: AnimeItem) {
        val existingIndex = favoriteAnime.indexOfFirst{it.id == animeItem.id}
        if(existingIndex != -1) {
            favoriteAnime.removeAt(existingIndex)
        } else {
            favoriteAnime.add(animeItem)
        }
    }

    fun isFavorite(animeItem: AnimeItem): Boolean {
        return favoriteAnime.any{it.id == animeItem.id}
    }
}