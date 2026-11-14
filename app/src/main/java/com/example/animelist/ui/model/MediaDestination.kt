package com.example.animelist.ui.model

import com.example.animelist.graphql.type.MediaType

data class MediaDestination(
    val id: Int,
    val type: MediaType = MediaType.ANIME
)
