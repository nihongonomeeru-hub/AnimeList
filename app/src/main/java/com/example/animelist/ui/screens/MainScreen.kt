package com.example.animelist.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.animelist.ui.components.AppBottomBar
import com.example.animelist.ui.navigation.BottomNavItem
import com.example.animelist.ui.screens.main_screens.FavoritesScreen
import com.example.animelist.ui.screens.main_screens.HomePage
import com.example.animelist.ui.screens.main_screens.ProfileScreen
import com.example.animelist.ui.screens.main_screens.SearchScreen
import com.example.animelist.view_model.FavoriteViewModel
import com.example.animelist.view_model.HomePageViewModel

@Composable
fun MainScreen(
    favoriteViewModel: FavoriteViewModel = viewModel(),
    viewModel: HomePageViewModel = viewModel()
) {
    val selectedAnime = viewModel.selectedAnime
    var currentRoute by remember { mutableStateOf(BottomNavItem.Home.route) }

    if (selectedAnime != null) {
        DetailScreen(
            animeItem = selectedAnime,
            onBackClick = { viewModel.clearSelection() }, // или viewModel.selectedAnime(null)
            viewModel = favoriteViewModel
        )
    } else {
        // Иначе показываем табы с BottomBar
        Scaffold(
            bottomBar = {
                AppBottomBar(
                    currentRoute = currentRoute,
                    onItemClick = { item -> currentRoute = item.route }
                )
            },
            containerColor = Color(0xFF0F0F0F)
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (currentRoute) {
                    BottomNavItem.Home.route -> HomePage(
                        favoriteViewModel = favoriteViewModel,
                        viewModel = viewModel
                    )

                    BottomNavItem.Search.route -> SearchScreen()
                    BottomNavItem.Favorites.route -> FavoritesScreen(
                        favoriteViewModel = favoriteViewModel,
                        onAnimeClick = { anime -> viewModel.selectedAnime(anime) },
                        viewModel = viewModel
                    )

                    BottomNavItem.Profile.route -> ProfileScreen()
                }
            }
        }
    }
}






