package com.example.animelist.ui.model

data class AnimeItem(
    val id: Int,
    val stringResourceId: String,
    val imageResourceId: String,
    val rating: Double? = null,
    val description: String,
    val genres: List<String>,
    val episodes: Int?,
    val year: Int?
)
