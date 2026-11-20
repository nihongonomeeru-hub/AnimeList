package com.example.animelist.ui.screens

import android.app.Activity
import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.animelist.ui.components.AppBottomBar
import com.example.animelist.ui.navigation.BottomNavItem
import com.example.animelist.view_model.HomePageViewModel
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.animelist.ui.model.AnimeItem
import com.example.animelist.ui.navigation.AppNavHost
import com.example.animelist.ui.navigation.FavouriteScreenRoute
import com.example.animelist.ui.navigation.HomePageRoute
import com.example.animelist.ui.navigation.SearchScreenRoute
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
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val favorites by favoriteViewModel.favoritesCombine.collectAsStateWithLifecycle()
    val selectedGenre by favoriteViewModel.selectedGenre.collectAsStateWithLifecycle()
    val handleToggleFavorite: (AnimeItem) -> Unit = { anime ->
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
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
                modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
                searchView = searchViewModel,
                favViewModel = favoriteViewModel,
                favorites = favorites,
                selectedGenre = selectedGenre
            )
        }
    }
}