package com.example.animelist.view_model

import android.app.Application
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val repository: FavouriteRepository
): ViewModel() {
    val favorites: StateFlow<List<AnimeItem>> = repository.getAllFavourites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = emptyList()
        )

    fun isFavorite(animeItem: AnimeItem): Flow<Boolean> =
        repository.isFavorite(animeItem.id)

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