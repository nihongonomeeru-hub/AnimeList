package com.example.animelist.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home: BottomNavItem("home", "ホーム", Icons.Default.Home)
    object Search: BottomNavItem("search", "検索", Icons.Default.Search)
    object Favorites: BottomNavItem("favourites", "マイリスト", Icons.Default.Favorite)
    object Profile: BottomNavItem("profile", "マイページ", Icons.Default.Person)
}