package com.example.animelist.ui.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class AnimeItem(
    val id: Int,
    @StringRes val stringResourceId: Int,
    @DrawableRes val imageResourceId: Int,
    val rating: Double? = null,
    val description: String?,
    val genres: List<String>,
    val episodes: Int?
)
