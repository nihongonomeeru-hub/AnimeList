package com.example.animelist.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.animelist.ui.model.AnimeItem

@Entity(tableName = "favorite_anime")
data class AnimeEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val imageUrl: String,
    val rating: Double?,
    val description: String?,
    val genres: List<String>,
    val episodes: Int?,
    val year: Int?
)

fun AnimeEntity.toAnimeItem(): AnimeItem = AnimeItem(
    id = id,
    stringResourceId = title,
    imageResourceId = imageUrl,
    rating = rating,
    description = description.toString(),
    genres = genres,
    episodes = episodes,
    year = year
)

fun AnimeItem.toEntity(): AnimeEntity = AnimeEntity(
    id = id,
    title = stringResourceId,
    imageUrl = imageResourceId,
    rating = rating,
    description = description,
    genres = genres,
    episodes = episodes,
    year = year
)