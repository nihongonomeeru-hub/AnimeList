package com.example.animelist.ui.model

data class AnimeItem(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val rating: Double? = null,
    val description: String,
    val genres: List<String>?,
    val episodes: Int?,
    val year: Int?,
    val trailer: TrailerItem? = null,
    val status: String? = null
)
