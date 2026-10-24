package com.example.animelist.view_model

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.animelist.data.local.AppDatabase
import com.example.animelist.data.local.entity.toAnimeItem
import com.example.animelist.data.local.entity.toEntity
import com.example.animelist.ui.model.AnimeItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoriteViewModel(application: Application): AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).animeDao()

    val favorites: StateFlow<List<AnimeItem>> = dao.getAllFavorites()
        .map { list -> list.map { it.toAnimeItem() } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun isFavorite(animeItem: AnimeItem): Flow<Boolean> = dao.isFavorite(animeItem.id)

    fun toggleFavorite(anime: AnimeItem) {
        viewModelScope.launch(Dispatchers.IO) {
            val alreadyFavorite = dao.isFavoriteOneShot(anime.id)
            if (alreadyFavorite) {
                dao.deleteFavoriteById(anime.id)
            } else {
                dao.insertFavorite(anime.toEntity())
            }
        }
    }

}