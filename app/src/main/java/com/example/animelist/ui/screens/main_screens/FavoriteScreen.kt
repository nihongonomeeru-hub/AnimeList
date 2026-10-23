package com.example.animelist.ui.screens.main_screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.animelist.ui.components.AnimeCard
import com.example.animelist.ui.components.AnimeListContent
import com.example.animelist.ui.data.AnimeDataSource
import com.example.animelist.ui.model.AnimeItem
import com.example.animelist.ui.screens.DetailScreen
import com.example.animelist.view_model.FavoriteViewModel
import com.example.animelist.view_model.HomePageViewModel

@Composable
fun FavoritesScreen(
    favoriteViewModel: FavoriteViewModel,
    viewModel: HomePageViewModel,
    onAnimeClick: (AnimeItem) -> Unit,
    modifier: Modifier= Modifier
) {
    val favorites = favoriteViewModel.favoriteAnime

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F))
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "マイリスト",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        if(favorites.isEmpty()) {
            EmptyFavoritesPlaceholder()
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(favorites, key = { it.id }) { anime ->
                    AnimeCard(
                        animeItem = anime,
                        onAnimeClick = onAnimeClick
                    )
                }
            }
        }

        val selectedAnime = viewModel.selectedAnime
        if(selectedAnime != null) {
            DetailScreen(
                animeItem = selectedAnime,
                onBackClick = { viewModel.clearSelection() },
                viewModel = favoriteViewModel
            )
        }
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
                text = "マイリストに登録された作品はありません",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}