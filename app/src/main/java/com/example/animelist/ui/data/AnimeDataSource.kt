package com.example.animelist.ui.data

import com.example.animelist.R
import com.example.animelist.ui.model.AnimeItem
import com.example.animelist.ui.model.AnimeSection
import com.example.animelist.ui.model.AnimeSectionType

object AnimeDataSource {
    fun loadSections(): List<AnimeSection> {
        val topRatedList = listOf<AnimeItem>(
            AnimeItem(1, R.string.re_zero, R.drawable.re_zero, 7.8, null, emptyList(), null),
            AnimeItem(2, R.string.tensura, R.drawable.tensura, 8.5, null, emptyList(), null),
            AnimeItem(3, R.string.attack_on_titan, R.drawable.attack_on_titan, 8.0, null, emptyList(), null),
            AnimeItem(4, R.string.one_piece, R.drawable.one_piece, 7.0, null, emptyList(), null),
            AnimeItem(5, R.string.naruto, R.drawable.naruto, 8.9, null, emptyList(), null)
        )

        val popularList = listOf<AnimeItem>(
            AnimeItem(1, R.string.re_zero, R.drawable.re_zero, 7.8, null, emptyList(), null),
            AnimeItem(2, R.string.tensura, R.drawable.tensura, 8.5, null, emptyList(), null),
            AnimeItem(3, R.string.attack_on_titan, R.drawable.attack_on_titan, 8.0, null, emptyList(), null),
            AnimeItem(4, R.string.one_piece, R.drawable.one_piece, 7.0, null, emptyList(), null),
            AnimeItem(5, R.string.naruto, R.drawable.naruto, 8.9, null, emptyList(), null)
        )

        val latestList = listOf<AnimeItem>(
            AnimeItem(1, R.string.re_zero, R.drawable.re_zero, 7.8, null, emptyList(), null),
            AnimeItem(2, R.string.tensura, R.drawable.tensura, 8.5, null, emptyList(), null),
            AnimeItem(3, R.string.attack_on_titan, R.drawable.attack_on_titan, 8.0, null, emptyList(), null),
            AnimeItem(4, R.string.one_piece, R.drawable.one_piece, 7.0, null, emptyList(), null),
            AnimeItem(5, R.string.naruto, R.drawable.naruto, 8.9, null, emptyList(), null)
        )

        val recommendList = listOf<AnimeItem>(
            AnimeItem(1, R.string.re_zero, R.drawable.re_zero, 7.8, null, emptyList(), null),
            AnimeItem(2, R.string.tensura, R.drawable.tensura, 8.5, null, emptyList(), null),
            AnimeItem(3, R.string.attack_on_titan, R.drawable.attack_on_titan, 8.0, null, emptyList(), null),
            AnimeItem(4, R.string.one_piece, R.drawable.one_piece, 7.0, null, emptyList(), null),
            AnimeItem(5, R.string.naruto, R.drawable.naruto, 8.9, null, emptyList(), null)
        )

        return listOf(
            AnimeSection(AnimeSectionType.TOP_ANIME, topRatedList),
            AnimeSection(AnimeSectionType.POPULAR_ANIME, popularList),
            AnimeSection(AnimeSectionType.LATEST_ANIME, latestList),
            AnimeSection(AnimeSectionType.RECOMMEND_ANIME, recommendList)
        )
    }
}
