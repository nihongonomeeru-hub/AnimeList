package com.example.animelist.ui.model

import androidx.annotation.StringRes
import com.example.animelist.R

enum class AnimeSectionType(
    @StringRes val typeRes: Int
) {
    TOP_ANIME(R.string.top_anime),
    POPULAR_ANIME(R.string.popular_anime),
    LATEST_ANIME(R.string.latest_anime),

}