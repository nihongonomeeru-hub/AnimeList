package com.example.animelist.ui.screens.main_screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.animelist.ui.components.AnimeListContent
import com.example.animelist.ui.components.HeroBanner
import com.example.animelist.view_model.HomePageViewModel
import com.example.animelist.view_model.uiState.HomeUIState

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    viewModel: HomePageViewModel = viewModel(),
) {
    when(val state = viewModel.uiState) {
        is HomeUIState.Loading -> {
            Box(
                modifier = modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }

        is HomeUIState.Error -> {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = state.message, color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        is HomeUIState.Success -> {
            val sections = state.animeList
            AnimeListContent(
                sections = sections,
                onAnimeClick = { anime -> viewModel.selectedAnime(anime) },
                headerContent = {
                    viewModel.dailyRecommendation?.let { anime ->
                        HeroBanner(
                            animeOfDay = anime,
                            onDetailsClick = { viewModel.selectedAnime(it) }
                        )
                    }
                },
                listState = viewModel.listState,
                getRawState = {sectionId -> viewModel.getRawState(sectionId)},
                onSeeAllClick = { section -> viewModel.openSection(section) }
            )
        }
    }
}