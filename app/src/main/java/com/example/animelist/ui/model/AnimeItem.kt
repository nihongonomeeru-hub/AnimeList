package com.example.animelist.ui.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class AnimeItem(
    @StringRes val stringResourceId: Int,
    @DrawableRes val imageResourceId: Int,
    val score: Double? = null
)
