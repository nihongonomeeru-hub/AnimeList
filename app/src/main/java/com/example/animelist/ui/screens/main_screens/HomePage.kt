package com.example.animelist.ui.screens.main_screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.animelist.ui.components.AnimeListContent
import com.example.animelist.ui.components.HeroBanner
import com.example.animelist.view_model.HomePageViewModel
import com.example.animelist.view_model.HomeUIState

@Composable
fun HomePage(
    viewModel: HomePageViewModel,
    modifier: Modifier = Modifier
) {
    when(val state = viewModel.uiState) {
        is HomeUIState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
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
                Button(onClick = { viewModel.fetchHomeSections() }) {
                    Text("再試行")
                }
            }
        }

        is HomeUIState.Success -> {
            val sections = state.animeList
            val featuredAnime = remember(sections) {
                sections.firstOrNull()?.items?.firstOrNull()
            }
            AnimeListContent(
                sections = sections,
                onAnimeClick = { anime -> viewModel.selectedAnime(anime) },
                headerContent = {
                    if(featuredAnime != null) {
                        HeroBanner(
                            featuredAnime = featuredAnime,
                            onDetailsClick = { anime ->
                                viewModel.selectedAnime(anime)
                            }
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



