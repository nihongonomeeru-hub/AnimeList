package com.example.animelist.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.animelist.ui.model.AnimeItem

@Composable
fun AnimeCard(
    modifier: Modifier = Modifier,
    animeItem: AnimeItem,
    onAnimeClick: (AnimeItem) -> Unit
) {

    Box(
        modifier = modifier
            .width(135.dp)
            .aspectRatio(2f / 3f)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onAnimeClick(animeItem) }
    ) {
        AsyncImage(
            model = animeItem.imageResourceId,
            contentDescription = animeItem.stringResourceId,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(8.dp))
        )
        AnimeRate(
            score = animeItem.rating,
            modifier = Modifier.align(Alignment.TopEnd)
        )

    }
}