package com.example.animelist.ui.screens

import android.app.Activity
import android.app.Application
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.animelist.ui.components.AppBottomBar
import com.example.animelist.ui.navigation.BottomNavItem
import com.example.animelist.ui.screens.main_screens.FavoritesScreen
import com.example.animelist.ui.screens.main_screens.HomePage
import com.example.animelist.ui.screens.main_screens.SearchScreen
import com.example.animelist.view_model.HomePageViewModel
import androidx.compose.runtime.rememberCoroutineScope
import com.example.animelist.graphql.type.MediaType
import com.example.animelist.ui.components.SectionDetail
import com.example.animelist.ui.model.AnimeItem
import com.example.animelist.ui.model.DetailDestination
import com.example.animelist.ui.model.MediaDestination
import com.example.animelist.view_model.FavoriteViewModel
import com.example.animelist.view_model.SearchViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    searchViewModel: SearchViewModel = viewModel(),
    viewModel: HomePageViewModel = viewModel(),
    favoriteViewModel: FavoriteViewModel = viewModel(
        factory = FavoriteViewModel.provideFactory(
            LocalContext.current.applicationContext as Application
        )
    )
) {
    val context = LocalContext.current
    val selectedAnime = viewModel.selectedAnime
    val detailStack = remember { mutableStateListOf<DetailDestination>() }
    var currentRoute by remember { mutableStateOf(BottomNavItem.Home.route) }

    var lastBackPressTime by remember { mutableLongStateOf(0L) }

    val selectedSection = viewModel.selectedSection
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(selectedAnime) {
        if(selectedAnime == null) {
            detailStack.clear()
        }
    }

    val handleDetailBack: () -> Unit = {
        if(detailStack.isNotEmpty()) {
            detailStack.removeAt(detailStack.lastIndex)
        } else {
            viewModel.clearSelection()
        }
    }

    val handleToggleFavorite: (AnimeItem) -> Unit = { anime ->
        favoriteViewModel.toggleFavorite(anime) { isAdded ->
            val text = if (isAdded) {
                "「${anime.title}」をお気に入りに追加しました"
            } else {
                "「${anime.title}」をお気に入りから削除しました"
            }
            (context as? Activity)?.runOnUiThread {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
            }
        }
    }

    BackHandler{
        if(selectedAnime != null) {
            handleDetailBack()
        } else if(viewModel.selectedSection != null) {
            viewModel.closeSection()
        } else {
            val currentTime = System.currentTimeMillis()

            if(currentTime - lastBackPressTime < 2000) {
                (context as? Activity)?.finish()
            } else {
                lastBackPressTime = currentTime
                Toast.makeText(context, "もう一度押すとアプリが終了します", Toast.LENGTH_SHORT).show()
            }
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                if(selectedAnime == null) {
                    AppBottomBar(
                        currentRoute = currentRoute,
                        onItemClick = { item ->
                            if(currentRoute == item.route) {
                                if(item.route == BottomNavItem.Home.route) {
                                    if(viewModel.selectedSection != null) {
                                        viewModel.closeSection()
                                    } else if(viewModel.selectedSection == null) {
                                        coroutineScope.launch {
                                            viewModel.listState.animateScrollToItem(0)
                                        }
                                    }
                                } else if(item.route == BottomNavItem.Search.route) {
                                    searchViewModel.clearGenre()
                                    searchViewModel.clearQuery()
                                }
                            } else {
                                currentRoute = item.route
                            }
                        }
                    )
                }
            },
            containerColor = Color(0xFF0F0F0F),
            contentWindowInsets = WindowInsets(top = 0.dp)
        ) { innerPadding ->
            Box(
                modifier = Modifier.padding(
                    bottom = innerPadding.calculateBottomPadding()
                )
            ) {
                when (currentRoute) {
                    BottomNavItem.Home.route -> {
                        HomePage(
                            viewModel = viewModel,
                            onLongClick = handleToggleFavorite
                        )
                        androidx.compose.animation.AnimatedVisibility(
                            visible = selectedSection != null,
                            enter = androidx.compose.animation.slideInHorizontally(
                                initialOffsetX = { fullWidth -> fullWidth },
                                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                            ) + fadeIn(animationSpec = tween(durationMillis = 250)),
                            exit = androidx.compose.animation.slideOutHorizontally(
                                targetOffsetX = { fullWidth -> fullWidth },
                                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                            ) + fadeOut(animationSpec = tween(durationMillis = 250))
                        ) {
                            selectedSection?.let { section ->
                                SectionDetail(
                                    section = section,
                                    onBackClick = { viewModel.closeSection() },
                                    onAnimeClick = { anime -> viewModel.selectedAnime(anime) },
                                    onLongClick = handleToggleFavorite
                                )
                            }
                        }
                    }

                    BottomNavItem.Search.route -> SearchScreen(
                        onAnimeClick = { anime -> viewModel.selectedAnime(anime) },
                        onLongClick = handleToggleFavorite
                    )
                    BottomNavItem.Favorites.route -> FavoritesScreen(
                        onAnimeClick = { anime -> viewModel.selectedAnime(anime) }
                    )
                }

                AnimatedContent(
                    targetState = selectedAnime,
                    label = "PosterExpandAnimation",
                    transitionSpec = {
                        if(targetState != null) {
                            (fadeIn(animationSpec = tween(350)) +
                                    scaleIn(
                                        initialScale = 0.78f,
                                        transformOrigin = TransformOrigin(0.5f, 0.4f), // Фокус зума чуть выше центра (где постер)
                                        animationSpec = tween(
                                            durationMillis = 200,
                                            easing = CubicBezierEasing(
                                                0.05f,
                                                0.7f,
                                                0.1f,
                                                1.0f
                                            )
                                        )
                                    )).togetherWith(
                                fadeOut(animationSpec = tween(200)) +
                                        scaleOut(
                                            targetScale = 0.95f,
                                            animationSpec = tween(200)
                                        )
                            )
                        } else {
                            (fadeIn(animationSpec = tween(250)) +
                                    scaleIn(
                                        initialScale = 0.95f,
                                        animationSpec = tween(250)
                                    )).togetherWith(
                                fadeOut(animationSpec = tween(250)) +
                                        scaleOut(
                                            targetScale = 0.82f,
                                            transformOrigin = TransformOrigin(0.5f, 0.4f),
                                            animationSpec = tween(
                                                durationMillis = 300,
                                                easing = FastOutSlowInEasing
                                            )
                                        )
                            )
                        }
                    }
                ) { anime ->
                    if (anime != null) {
                        val activeDestination = detailStack.lastOrNull()
                            ?: DetailDestination.Media(id = anime.id, type = MediaType.ANIME)
                        val handleCloseAll: () -> Unit = {
                            detailStack.clear()
                            viewModel.clearSelection()
                        }

                        when(activeDestination) {
                            is DetailDestination.Media -> DetailScreen(
                                animeId = activeDestination.id,
                                mediaType = activeDestination.type,
                                onBackClick = handleDetailBack,
                                onRelatedMediaClick = { related ->
                                    detailStack.add(DetailDestination.Media(related.id, related.mediaType))
                                },
                                onCharacterClick = { characterId ->
                                    detailStack.add(DetailDestination.Character(characterId))
                                },
                                onVoiceActorClick = { voiceActorId ->
                                    detailStack.add(DetailDestination.Staff(voiceActorId))
                                },
                                onCloseAllClick = handleCloseAll,
                                showCloseAll = detailStack.isNotEmpty()
                            )

                            is DetailDestination.Character -> CharacterDetailScreen(
                                characterId = activeDestination.id,
                                onBackClick = handleDetailBack,
                                onVoiceActorClick = { staffId ->
                                    detailStack.add(DetailDestination.Staff(staffId))
                                },
                                onMediaClick = { mediaId, mediaType ->
                                    detailStack.add(DetailDestination.Media(mediaId, mediaType))
                                },
                                onCloseAllClick = handleCloseAll,
                                showCloseAll = detailStack.isNotEmpty()
                            )

                            is DetailDestination.Staff -> StaffDetailScreen(
                                staffId = activeDestination.id,
                                onBackClick = handleDetailBack,
                                onCharacterClick = { charId ->
                                    detailStack.add(DetailDestination.Character(charId))
                                },
                                onMediaClick = { mediaId, mediaType ->
                                    detailStack.add(DetailDestination.Media(mediaId, mediaType))
                                },
                                showCloseAll = detailStack.isNotEmpty(),
                                onCloseAllClick = handleCloseAll
                            )
                        }
                    }
                }
            }

        }
    }
}