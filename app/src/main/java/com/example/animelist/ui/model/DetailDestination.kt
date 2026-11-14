package com.example.animelist.ui.model

import com.example.animelist.graphql.type.MediaType

sealed interface DetailDestination {
    data class Media(val id: Int, val type: MediaType = MediaType.ANIME) : DetailDestination
    data class Character(val id: Int) : DetailDestination
    data class Staff(val id: Int) : DetailDestination
}