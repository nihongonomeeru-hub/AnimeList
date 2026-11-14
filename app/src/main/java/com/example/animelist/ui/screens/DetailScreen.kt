package com.example.animelist.ui.screens

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.animelist.graphql.type.MediaType
import com.example.animelist.ui.components.sections.AnimeDetailHeaderSection
import com.example.animelist.ui.components.sections.AnimeInfoSection
import com.example.animelist.ui.components.sections.AnimeTrailerSection
import com.example.animelist.ui.components.sections.CharactersCastSection
import com.example.animelist.ui.components.sections.RelatedMediaSection
import com.example.animelist.ui.model.RelatedMediaItem
import com.example.animelist.view_model.CharacterCastViewModel
import com.example.animelist.view_model.DetailViewModel
import com.example.animelist.view_model.FavoriteViewModel
import com.example.animelist.view_model.uiState.DetailUiState

@Composable
fun DetailScreen(
    animeId: Int,
    mediaType: MediaType = MediaType.ANIME,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onRelatedMediaClick: (RelatedMediaItem) -> Unit = {},
    onCharacterClick: (Int) -> Unit = {},
    onVoiceActorClick: (Int) -> Unit = {},
    showCloseAll: Boolean = false,
    onCloseAllClick: () -> Unit = {},
    detailViewModel: DetailViewModel = viewModel(),
    characterCastViewModel: CharacterCastViewModel = viewModel(),
    favViewModel: FavoriteViewModel = viewModel(
        factory = FavoriteViewModel.provideFactory(
            LocalContext.current.applicationContext as Application
        )
    )
) {
    val scrollState = key(
        animeId, mediaType
    ) { rememberScrollState() }
    val detailState by detailViewModel.uiState.collectAsStateWithLifecycle()
    val castUiState by characterCastViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(animeId) {
        detailViewModel.loadAnimeDetail(animeId, mediaType)
        if(mediaType == MediaType.ANIME) {
            characterCastViewModel.loadCharactersAndCast(animeId)
        }
    }

    when (val state = detailState) {
        is DetailUiState.Loading -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color(0xFF0F0F0F)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
        is DetailUiState.Error -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color(0xFF0F0F0F)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = state.message, color = Color.Gray)
                Button(
                    onClick = {
                        detailViewModel.loadAnimeDetail(animeId)
                        characterCastViewModel.loadCharactersAndCast(animeId)
                    }
                ) {
                    Text(text = "再読み込み")
                }
            }
        }
        is DetailUiState.Success -> {
            val isFavorite by favViewModel
                .isFavorite(state.anime)
                .collectAsStateWithLifecycle(initialValue = false)

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color(0xFF0F0F0F))
                    .verticalScroll(scrollState)
            ) {
                AnimeDetailHeaderSection(
                    animeItem = state.anime,
                    onBackClick = onBackClick,
                    onToggleFavorite = { favViewModel.toggleFavorite(state.anime) },
                    isFavorite = isFavorite,
                    showCloseAll = showCloseAll,
                    onCloseAllClick = onCloseAllClick,
                    mediaType = mediaType
                )
                AnimeInfoSection(
                    animeItem = state.anime,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                if(state.anime.trailer != null) {
                    Spacer(modifier = Modifier.height(48.dp))

                    AnimeTrailerSection(
                        trailer = state.anime.trailer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }


                val genres = state.anime.genres
                if(!genres.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(32.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(
                            items = genres,
                            key = { genre -> genre }
                        ) { genre ->
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
                }

                Spacer(modifier = Modifier.height(20.dp))

                CharactersCastSection(
                    castUiState = castUiState,
                    modifier = Modifier
                        .fillMaxWidth(),
                    onCharacterClick = onCharacterClick,
                    onVoiceActorClick = onVoiceActorClick
                )

                if(state.anime.relatedMedia?.isNotEmpty() == true) {
                    Spacer(modifier = Modifier.height(28.dp))

                    RelatedMediaSection(
                        items = state.anime.relatedMedia,
                        modifier = Modifier.fillMaxWidth(),
                        onRelatedMediaClick = onRelatedMediaClick
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}