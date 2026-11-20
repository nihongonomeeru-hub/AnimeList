package com.example.animelist.view_model

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.animelist.data.local.AppDatabase
import com.example.animelist.data.local.repository.FavouriteRepository
import com.example.animelist.data.local.repository.FavouriteRepositoryImpl
import com.example.animelist.ui.model.AnimeItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

class FavoriteViewModel(
    private val repository: FavouriteRepository
): ViewModel() {
    val favorites: StateFlow<List<AnimeItem>> = repository.getAllFavourites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = emptyList()
        )

    var _selectedGenre = MutableStateFlow<String?>(null)
        private set

    val selectedGenre: StateFlow<String?> = _selectedGenre.asStateFlow()

    fun isFavorite(animeItem: AnimeItem): Flow<Boolean> =
        repository.isFavorite(animeItem.id)

    val favoritesCombine: StateFlow<List<AnimeItem>> = combine(
        repository.getAllFavourites(),
        _selectedGenre
    ) { allFavorites, genre ->
        if (genre == null) {
            allFavorites
        } else {
            allFavorites.filter { anime ->
                anime.genres.any { it.equals(genre, ignoreCase = true) }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = emptyList()
    )

    fun toggleGenre(genre: String?) {
        _selectedGenre.update{ current ->
            if(current == genre) null else genre
        }
    }

    fun toggleFavorite(
        anime: AnimeItem,
        onResult: ((isAdded: Boolean) -> Unit)? = null
    ) {
        viewModelScope.launch() {
            repository.toggleFavorite(anime)
                .onSuccess { isAdded ->
                    onResult?.invoke(isAdded)
                }
        }
    }

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5_000L

        fun provideFactory(application: Application): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val dao = AppDatabase.getDatabase(application).animeDao()
                FavoriteViewModel(FavouriteRepositoryImpl(dao))
            }
        }
    }

}