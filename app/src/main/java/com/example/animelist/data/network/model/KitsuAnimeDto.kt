package com.example.animelist.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KitsuResponse(
    val data: List<KitsuAnimeDto>
)

@Serializable
data class KitsuAnimeDto(
    val id: String? = null,
    val attributes: KitsuAttributes? = null
)

@Serializable
data class KitsuAttributes(
    val canonicalTitle: String? = null,
    val titles: KitsuTitles? = null,
    val synopsis: String? = null,
    val averageRating: String? = null,
    val posterImage: KitsuPosterImage? = null,
    val episodeCount: Int? = null,
    val startDate: String? = null,
)

@Serializable
data class KitsuTitles(
    @SerialName("ja_jp")
    val jaJp: String? = null,
    val en: String? = null
)

@Serializable
data class KitsuPosterImage(
    val medium: String? = null,
    val large: String? = null,
    val original: String? = null
)