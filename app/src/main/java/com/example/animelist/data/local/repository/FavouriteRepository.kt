package com.example.animelist.data.local.repository

import com.example.animelist.data.local.AnimeDao
import com.example.animelist.data.local.entity.toAnimeItem
import com.example.animelist.data.local.entity.toEntity
import com.example.animelist.ui.model.AnimeItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

interface FavouriteRepository {
    fun getAllFavourites(): Flow<List<AnimeItem>>
    fun isFavorite(animeId: Int): Flow<Boolean>
    suspend fun toggleFavorite(anime: AnimeItem): Result<Boolean>
}

class FavouriteRepositoryImpl(
    private val dao: AnimeDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): FavouriteRepository {
    override fun getAllFavourites(): Flow<List<AnimeItem>> =
        dao.getAllFavorites().map { list -> list.map { it.toAnimeItem() } }

    override fun isFavorite(animeId: Int): Flow<Boolean> =
        dao.isFavorite(animeId)

    override suspend fun toggleFavorite(anime: AnimeItem): Result<Boolean> =
        withContext(ioDispatcher) {
            try {
                val alreadyFavourite = dao.isFavoriteOneShot(anime.id)
                if(alreadyFavourite) {
                    dao.deleteFavoriteById(anime.id)
                    Result.success(false)
                } else {
                    dao.insertFavorite(anime.toEntity())
                    Result.success(true)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}