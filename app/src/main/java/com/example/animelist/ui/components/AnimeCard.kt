package com.example.animelist.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.animelist.R
import com.example.animelist.ui.model.AnimeItem
import com.example.animelist.view_model.FavoriteViewModel

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
        Image(
            painter = painterResource(animeItem.imageResourceId),
            contentDescription = stringResource(animeItem.stringResourceId),
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