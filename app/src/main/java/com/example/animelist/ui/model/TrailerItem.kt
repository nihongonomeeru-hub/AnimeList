package com.example.animelist.ui.model

data class TrailerItem (
    val id: String,
    val site: String?,
    val thumbnailUrl: String? = if (site?.equals("youtube", ignoreCase = true) == true) {
        "https://img.youtube.com/vi/$id/hqdefault.jpg"
    } else null
)