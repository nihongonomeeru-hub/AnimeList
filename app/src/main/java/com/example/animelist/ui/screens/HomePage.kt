package com.example.animelist.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.animelist.ui.data.AnimeDataSource

@Composable
fun HomePage(
    modifier: Modifier = Modifier
) {
    AnimeScreen(
        sections = AnimeDataSource.loadSections(),
        modifier = modifier
    )
}



