package com.example.animelist.ui.screens

import android.app.Activity
import android.app.Application
import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.animelist.graphql.type.MediaType
import com.example.animelist.ui.components.SectionDetail
import com.example.animelist.ui.model.AnimeItem
import com.example.animelist.ui.navigation.AnimeDetailRoute
import com.example.animelist.ui.navigation.AppNavHost
import com.example.animelist.ui.navigation.CharacterDetailRoute
import com.example.animelist.ui.navigation.FavouriteScreenRoute
import com.example.animelist.ui.navigation.HomePageRoute
import com.example.animelist.ui.navigation.SearchScreenRoute
import com.example.animelist.ui.navigation.SectionDetailRoute
import com.example.animelist.ui.navigation.StaffDetailRoute
import com.example.animelist.view_model.FavoriteViewModel
import com.example.animelist.view_model.SearchViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    searchViewModel: SearchViewModel = viewModel(),
    homeViewModel: HomePageViewModel = viewModel(),
    favoriteViewModel: FavoriteViewModel = viewModel(
        factory = FavoriteViewModel.provideFactory(
            LocalContext.current.applicationContext as Application
        )
    )
) {
    val context = LocalContext.current
    val navController = rememberNavController()

    val selectedSection = homeViewModel.selectedSection
    val coroutineScope = rememberCoroutineScope()

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

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color(0xFF0F0F0F),
            contentWindowInsets = WindowInsets(top = 0.dp),
            bottomBar = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val showBottomBar = listOf(
                    HomePageRoute::class,
                    SearchScreenRoute::class,
                    FavouriteScreenRoute::class
                ).any { currentDestination?.hasRoute(it) == true }

                if (showBottomBar) {
                    AppBottomBar(
                        currentRoute = when {
                            currentDestination?.hasRoute<HomePageRoute>() == true -> BottomNavItem.Home.route
                            currentDestination?.hasRoute<SearchScreenRoute>() == true -> BottomNavItem.Search.route
                            currentDestination?.hasRoute<FavouriteScreenRoute>() == true -> BottomNavItem.Favorites.route

                            else -> BottomNavItem.Home.route
                        },
                        onItemClick = { item ->
                            val targetRoute: Any = when (item) {
                                BottomNavItem.Home -> HomePageRoute
                                BottomNavItem.Search -> SearchScreenRoute
                                BottomNavItem.Favorites -> FavouriteScreenRoute
                            }

                            if (currentDestination?.hasRoute(targetRoute::class) == true) {
                                if (item == BottomNavItem.Home) {
                                    coroutineScope.launch {
                                        homeViewModel.listState.animateScrollToItem(0)
                                    }
                                } else if (item == BottomNavItem.Search) {
                                    searchViewModel.clearGenre()
                                    searchViewModel.clearQuery()
                                }
                            } else {
                                navController.navigate(targetRoute) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true

                                }
                            }
                        },
                    )
                }
            },
        ) { innerPadding ->
            AppNavHost(
                navController = navController,
                homeViewModel = homeViewModel,
                onToggleFavourite = handleToggleFavorite,
                modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
            )
        }
    }
}