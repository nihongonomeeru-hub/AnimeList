package com.example.animelist.ui.model

import com.example.animelist.graphql.type.MediaType

data class RelatedMediaItem (
    val id: Int,
    val mediaType: MediaType,
    val title: String,
    val imageUrl: String,
    val formatLabel: String,   // "原作漫画", "ライトノベル", "関連作品"
    val relationLabel: String, // "原作", "スピンオフ", "外伝"
    val rating: Double?,
    val chapters: Int? = null,
    val volumes: Int? = null
)