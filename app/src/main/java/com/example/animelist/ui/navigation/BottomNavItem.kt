package com.example.animelist.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.animelist.R

sealed class BottomNavItem(
    val route: String,
    @StringRes val title: Int,
    val icon: ImageVector
) {
    object Home: BottomNavItem("home", R.string.home, Icons.Default.Home)
    object Search: BottomNavItem("search", R.string.search, Icons.Default.Search)
    object Favorites: BottomNavItem("favourites", R.string.my_list, Icons.Default.Favorite)
}