package com.example.animelist.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.animelist.ui.components.sections.AnimeDetailHeaderSection
import com.example.animelist.ui.components.sections.AnimeInfoSection
import com.example.animelist.ui.components.sections.CharactersCastSection
import com.example.animelist.ui.model.AnimeItem
import com.example.animelist.view_model.CharacterCastViewModel

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    animeItem: AnimeItem,
    onBackClick: () -> Unit,
    characterCastViewModel: CharacterCastViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    val castUiState by characterCastViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(animeItem.id) {
        characterCastViewModel.loadCharactersAndCast(animeItem.id)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F))
            .verticalScroll(scrollState)
    ) {
        AnimeDetailHeaderSection(
            animeItem = animeItem,
            onBackClick = onBackClick
        )
        AnimeInfoSection(
            animeItem = animeItem,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            val genres = animeItem.genres
            genres?.forEach { genre ->
                Surface(
                    color = Color(0xFF1E1E1E),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = genre,
                        color = Color(0xFFB0B0B0),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        CharactersCastSection(
            castUiState = castUiState,
            modifier = Modifier.fillMaxWidth()
        )
    }
}