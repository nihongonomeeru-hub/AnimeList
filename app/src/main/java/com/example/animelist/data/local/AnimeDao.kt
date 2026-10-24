package com.example.animelist.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.animelist.data.local.entity.AnimeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeDao {

    @Query("SELECT * FROM favorite_anime")
    fun getAllFavorites(): Flow<List<AnimeEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_anime WHERE id = :id)")
    fun isFavorite(id: Int): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_anime WHERE id = :id)")
    suspend fun isFavoriteOneShot(id: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(anime: AnimeEntity)


    @Query("DELETE FROM favorite_anime WHERE id = :id")
    suspend fun deleteFavoriteById(id: Int)
}