package com.example.animelist.ui.screens.main_screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.animelist.ui.data.AnimeDataSource
import com.example.animelist.ui.components.AnimeListContent
import com.example.animelist.ui.components.HeroBanner
import com.example.animelist.view_model.HomePageViewModel

@Composable
fun HomePage(
    viewModel: HomePageViewModel
) {
    val sections = remember { AnimeDataSource.loadSections() }
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



