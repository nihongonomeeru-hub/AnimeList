package com.example.animelist.ui.screens.main_screens

import android.app.Application
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
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
import kotlinx.coroutines.launch

@Composable
fun FavoritesScreen(
    onAnimeClick: (AnimeItem) -> Unit,
    modifier: Modifier= Modifier,
    favViewModel: FavoriteViewModel = viewModel(
        factory = FavoriteViewModel.provideFactory(
            LocalContext.current.applicationContext as Application
        )
    ),
) {
    val snackBarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val favorites by favViewModel.favorites.collectAsStateWithLifecycle()
    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHost) },
        containerColor = Color(0xFF0F0F0F)
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF0F0F0F))
                .statusBarsPadding()
                .padding(innerPadding)
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
                onRemoveFavorite = { animeId ->
                    favViewModel.toggleFavorite(animeId)

                    scope.launch {
                        val result = snackBarHost.showSnackbar(
                            message = "${animeId.title}を削除しました",
                            actionLabel = "元に戻す",
                            withDismissAction = true,
                            duration = SnackbarDuration.Short
                        )

                        if(result == SnackbarResult.ActionPerformed) {
                            favViewModel.toggleFavorite(animeId)
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
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
        val haptic = LocalHapticFeedback.current

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            modifier = modifier
                .fillMaxSize()
        ) {
            items(
                items = favorites,
                key = {anime -> anime.id}
            ) { anime ->
                var resetTrigger by remember { mutableIntStateOf(0) }

                key(anime.id, resetTrigger) {
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            if (value == SwipeToDismissBoxValue.EndToStart) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                resetTrigger++
                                onRemoveFavorite(anime)
                                true
                            } else {
                                false
                            }
                        }
                    )
                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = false,
                        enableDismissFromEndToStart = true,
                        modifier = Modifier
                            .animateItem(
                                fadeInSpec = spring(stiffness = Spring.StiffnessMediumLow),
                                placementSpec = spring(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessMediumLow
                                ),
                                fadeOutSpec = spring(stiffness = Spring.StiffnessMedium)
                            )
                            .clip(RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp)),
                        backgroundContent = {
                            val progress = if(dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                                dismissState.progress
                            } else {
                                0f
                            }
                            val backgroundColor = androidx.compose.ui.graphics.lerp(
                                start = Color(0xFF2C2C2E),
                                stop = Color(0xFFE53935),
                                fraction = (progress * 1.5f).coerceIn(0f, 1f)
                            )
                            val iconScale = 0.8f + (progress * 0.5f).coerceAtMost(0.5f)
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(backgroundColor)
                                    .padding(end = 24.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "削除",
                                    tint = Color.White,
                                    modifier = Modifier.scale(iconScale)
                                )
                            }
                        }
                    ) {
                        FavoriteListItem(
                            anime = anime,
                            onAnimeClick = onAnimeClick,
                            onRemoveFavorite = onRemoveFavorite
                        )
                    }
                }
            }
        }
    }
}