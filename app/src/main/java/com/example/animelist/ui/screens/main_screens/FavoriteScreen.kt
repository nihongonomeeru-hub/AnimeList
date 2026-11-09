package com.example.animelist.ui.screens.main_screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.animelist.R
import com.example.animelist.ui.components.FavoriteListItem
import com.example.animelist.ui.model.AnimeItem
import com.example.animelist.view_model.FavoriteViewModel

@Composable
fun FavoritesScreen(
    modifier: Modifier= Modifier,
    favViewModel: FavoriteViewModel = viewModel(),
    onAnimeClick: (AnimeItem) -> Unit
) {
    val favorites by favViewModel.favorites.collectAsStateWithLifecycle()
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F))
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.my_list),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        FavoritesContent(
            favorites = favorites,
            onAnimeClick = onAnimeClick,
            onRemoveFavorite = { animeId -> favViewModel.toggleFavorite(animeId) },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun EmptyFavoritesPlaceholder() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.FavoriteBorder,
                contentDescription = null,
                tint = Color.DarkGray,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = stringResource(R.string.no_my_list),
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun FavoritesContent(
    favorites: List<AnimeItem>,
    onAnimeClick: (AnimeItem) -> Unit,
    onRemoveFavorite: (AnimeItem) -> Unit,
    modifier: Modifier = Modifier
) {
    if(favorites.isEmpty()) {
        EmptyFavoritesPlaceholder()
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = modifier.fillMaxSize()
        ) {
            items(
                items = favorites,
                key = {anime -> anime.id}
            ) { anime ->
                FavoriteListItem(
                    anime = anime,
                    onAnimeClick = onAnimeClick,
                    onRemoveFavorite = onRemoveFavorite
                )
            }
        }
    }
}