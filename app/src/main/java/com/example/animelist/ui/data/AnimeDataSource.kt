package com.example.animelist.ui.data

import com.example.animelist.R
import com.example.animelist.ui.model.AnimeItem
import com.example.animelist.ui.model.AnimeSection
import com.example.animelist.ui.model.AnimeSectionType

object AnimeDataSource {
    fun loadSections(): List<AnimeSection> {
        val topRatedList = listOf<AnimeItem>(
            AnimeItem(R.string.re_zero, R.drawable.re_zero, 7.8),
            AnimeItem(R.string.tensura, R.drawable.tensura, 8.5),
            AnimeItem(R.string.attack_on_titan, R.drawable.attack_on_titan, 8.0),
            AnimeItem(R.string.one_piece, R.drawable.one_piece, 7.0),
            AnimeItem(R.string.naruto, R.drawable.naruto, 8.9)
        )

        val popularList = listOf<AnimeItem>(
            AnimeItem(R.string.re_zero, R.drawable.re_zero, 7.8),
            AnimeItem(R.string.tensura, R.drawable.tensura, 8.5),
            AnimeItem(R.string.attack_on_titan, R.drawable.attack_on_titan, 8.0),
            AnimeItem(R.string.one_piece, R.drawable.one_piece, 7.0),
            AnimeItem(R.string.naruto, R.drawable.naruto, 8.9)
        )

        val latestList = listOf<AnimeItem>(
            AnimeItem(R.string.re_zero, R.drawable.re_zero, 7.8),
            AnimeItem(R.string.tensura, R.drawable.tensura, 8.5),
            AnimeItem(R.string.attack_on_titan, R.drawable.attack_on_titan, 8.0),
            AnimeItem(R.string.one_piece, R.drawable.one_piece, 7.0),
            AnimeItem(R.string.naruto, R.drawable.naruto, 8.9)
        )

        return listOf(
            AnimeSection(AnimeSectionType.TOP_ANIME, topRatedList),
            AnimeSection(AnimeSectionType.POPULAR_ANIME, popularList),
            AnimeSection(AnimeSectionType.LATEST_ANIME, latestList)
        )
    }
}
