package com.example.animelist.ui.components.contents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.animelist.ui.components.AnimeCard
import com.example.animelist.ui.model.AnimeItem

@Composable
fun AnimeGridContent(
    animeList: List<AnimeItem>,
    onAnimeClick: (AnimeItem) -> Unit,
    modifier: Modifier = Modifier,
    state: LazyGridState = rememberLazyGridState(),
    onLongClick: ((AnimeItem) -> Unit)? = null,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        state = state,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = modifier.padding(bottom = 16.dp)
    ) {
        items(
            items = animeList,
            key = {anime -> anime.id}
        ) { anime ->
            AnimeCard(
                animeItem = anime,
                onAnimeClick = onAnimeClick,
                onLongClick = onLongClick?.let { action -> {action(anime) }}
            )
        }
    }
}