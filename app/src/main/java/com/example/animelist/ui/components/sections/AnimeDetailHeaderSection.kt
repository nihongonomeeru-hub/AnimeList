package com.example.animelist.ui.components.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.animelist.R
import com.example.animelist.ui.model.AnimeItem

private val TopScrimBrush =  Brush.verticalGradient(
    colors = listOf(
        Color(0x99000000),
        Color.Transparent
    )
)

private val BottomScrimBrush = Brush.verticalGradient(
    colors = listOf(
        Color.Transparent,
        Color(0x660F0F0F),
        Color(0xFF0F0F0F)
    ),
    startY = 100f
)

@Composable
fun AnimeDetailHeaderSection(
    animeItem: AnimeItem,
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(3f / 4f)
    ) {

        AsyncImage(
            model = animeItem.imageUrl,
            contentDescription = animeItem.title,
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    TopScrimBrush
                )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    BottomScrimBrush
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.5f),
                modifier = Modifier.size(40.dp)
            ) {
                IconButton(
                    onClick = onBackClick
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = Color.White
                    )
                }
            }
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.5f),
                modifier = Modifier.size(40.dp)
            ) {
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if(isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = stringResource(R.string.to_mylist),
                        tint = if(isFavorite) Color.Red else Color.Gray
                    )
                }
            }
        }
    }
}