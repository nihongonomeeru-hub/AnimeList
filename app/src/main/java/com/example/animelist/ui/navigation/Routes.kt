package com.example.animelist.ui.navigation

import com.example.animelist.graphql.type.MediaType
import kotlinx.serialization.Serializable

//BottomNavItem
@Serializable object HomePageRoute
@Serializable object SearchScreenRoute
@Serializable object FavouriteScreenRoute

//DetailScreens
@Serializable data class AnimeDetailRoute(val animeId: Int, val mediaType: String = MediaType.ANIME.name)
@Serializable data class CharacterDetailRoute(val characterId: Int)
@Serializable data class StaffDetailRoute(val staffId: Int)
@Serializable data class SectionDetailRoute(val sectionId: Int)