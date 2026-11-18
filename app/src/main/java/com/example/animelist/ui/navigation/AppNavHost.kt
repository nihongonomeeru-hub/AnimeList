package com.example.animelist.ui.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.animelist.graphql.type.MediaType
import com.example.animelist.ui.components.SectionDetail
import com.example.animelist.ui.model.AnimeItem
import com.example.animelist.ui.screens.CharacterDetailScreen
import com.example.animelist.ui.screens.DetailScreen
import com.example.animelist.ui.screens.StaffDetailScreen
import com.example.animelist.ui.screens.main_screens.FavoritesScreen
import com.example.animelist.ui.screens.main_screens.HomePage
import com.example.animelist.ui.screens.main_screens.SearchScreen
import com.example.animelist.view_model.HomePageViewModel

private fun NavHostController.isDeepInStack(): Boolean {
    val prev = previousBackStackEntry?.destination?: return false
    return prev.hasRoute<AnimeDetailRoute>() ||
            prev.hasRoute<CharacterDetailRoute>() ||
            prev.hasRoute<StaffDetailRoute>()
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    homeViewModel: HomePageViewModel,
    onToggleFavourite: (AnimeItem) -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomePageRoute,
        modifier = modifier
    ) {
        //Main Screen
        composable<HomePageRoute> {
            HomePage(
                viewModel = homeViewModel,
                onLongClick = onToggleFavourite,
                onAnimeClick = { navController.navigate(AnimeDetailRoute(it.id)) },
                onSectionClick = { navController.navigate(SectionDetailRoute(it.id)) }
            )
        }

        //Search Screen
        composable<SearchScreenRoute> {
            SearchScreen(
                onAnimeClick = { anime -> navController.navigate(AnimeDetailRoute(anime.id)) },
                onLongClick = onToggleFavourite
            )
        }

        //Favorites Screen
        composable<FavouriteScreenRoute> {
            FavoritesScreen(
                onAnimeClick = { anime -> navController.navigate(AnimeDetailRoute(anime.id)) }
            )
        }

        //All Anime Section
        composable<SectionDetailRoute>(
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                ) + fadeIn(tween(250))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                ) + fadeOut(tween(250))
            }
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<SectionDetailRoute>()
            val section = homeViewModel.getSectionById(route.sectionId)
            section?.let { sec ->
                SectionDetail(
                    section = sec,
                    onBackClick = { navController.popBackStack() },
                    onAnimeClick = { anime ->
                        navController.navigate(AnimeDetailRoute(anime.id))
                    },
                    onLongClick = onToggleFavourite
                )
            }
        }
        composable<AnimeDetailRoute>(
            enterTransition = {
                scaleIn(
                    initialScale = 0.92f,
                    animationSpec = tween(250)
                ) + fadeIn(tween(200))
            },
            exitTransition = {
                scaleOut(targetScale = 0.92f, animationSpec = tween(250)) + fadeOut(
                    tween(
                        200
                    )
                )
            }
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<AnimeDetailRoute>()
            val mediaType = runCatching { MediaType.valueOf(route.mediaType) }
                .getOrDefault(MediaType.ANIME)

            DetailScreen(
                animeId = route.animeId,
                mediaType = mediaType,
                onBackClick = { navController.popBackStack() },
                onRelatedMediaClick = { item ->
                    navController.navigate(AnimeDetailRoute(item.id, item.mediaType.name))
                },
                onCharacterClick = { character ->
                    navController.navigate(CharacterDetailRoute(character))
                },
                onVoiceActorClick = { staff ->
                    navController.navigate(StaffDetailRoute(staff))
                },
                showCloseAll = navController.isDeepInStack(),
                onCloseAllClick = {
                    navController.popBackStack(
                        route = HomePageRoute,
                        inclusive = false
                    )
                }
            )
        }

        //person detail
        composable<CharacterDetailRoute> { backStackEntry ->
            val isDeepInStack =
                navController.previousBackStackEntry?.destination?.hasRoute<AnimeDetailRoute>() == true ||
                        navController.previousBackStackEntry?.destination?.hasRoute<CharacterDetailRoute>() == true ||
                        navController.previousBackStackEntry?.destination?.hasRoute<StaffDetailRoute>() == true

            val route = backStackEntry.toRoute<CharacterDetailRoute>()
            CharacterDetailScreen(
                characterId = route.characterId,
                onBackClick = { navController.popBackStack() },
                onMediaClick = { mediaId, mediaType ->
                    navController.navigate(AnimeDetailRoute(mediaId, mediaType.name))
                },
                onVoiceActorClick = { staff ->
                    navController.navigate(StaffDetailRoute(staff))
                },
                onCloseAllClick = {
                    navController.popBackStack(
                        route = HomePageRoute,
                        inclusive = false
                    )
                },
                showCloseAll = isDeepInStack
            )
        }

        //staff detail
        composable<StaffDetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<StaffDetailRoute>()
            StaffDetailScreen(
                staffId = route.staffId,
                onBackClick = { navController.popBackStack() },
                onMediaClick = { mediaId, mediaType ->
                    navController.navigate(AnimeDetailRoute(mediaId, mediaType.name))
                },
                onCharacterClick = { character ->
                    navController.navigate(CharacterDetailRoute(character))
                },
                onCloseAllClick = {
                    navController.popBackStack(
                        route = HomePageRoute,
                        inclusive = false
                    )
                },
                showCloseAll = navController.isDeepInStack()
            )
        }
    }
}