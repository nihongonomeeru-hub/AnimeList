package com.example.animelist.ui.screens.main_screens

import androidx.compose.runtime.Composable
import com.example.animelist.ui.data.AnimeDataSource
import com.example.animelist.ui.components.AnimeListContent
import com.example.animelist.ui.screens.DetailScreen
import com.example.animelist.view_model.HomePageViewModel
import com.example.animelist.view_model.FavoriteViewModel

@Composable
fun HomePage(
    viewModel: HomePageViewModel,
    favoriteViewModel: FavoriteViewModel
) {
    val selectedAnime = viewModel.selectedAnime
    if(selectedAnime != null) {
        DetailScreen(
            animeItem = selectedAnime,
            onBackClick = { viewModel.clearSelection() },
            viewModel = favoriteViewModel
        )
    } else {
        AnimeListContent(
            sections = AnimeDataSource.loadSections(),
            onAnimeClick = {anime -> viewModel.selectedAnime(anime)}
        )
    }
}



