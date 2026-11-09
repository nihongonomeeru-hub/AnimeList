package com.example.animelist.view_model.reposiroty

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.example.animelist.graphql.GetAnimeCharacterAndCastQuery
import com.example.animelist.graphql.GetAnimeDetailQuery
import com.example.animelist.graphql.GetAnimeListQuery
import com.example.animelist.ui.model.AnimeItem
import com.example.animelist.ui.model.CharacterVoiceActorUIModel
import com.example.animelist.ui.model.TrailerItem

class AnimeDetailRepository(
    private val apolloClient: ApolloClient
) {
    suspend fun getAnimeDetail(
        animeId: Int,
        defaultGenres: List<String> = listOf("アニメ")
    ): AnimeItem {
        val response = apolloClient
            .query(GetAnimeDetailQuery(animeId = animeId))
            .execute()

        val media = response.data?.Media
            ?: throw IllegalStateException("アニメの情報が見つかりませんでした")

        val parsedRating = media.weightedScore?.let { score ->
            String.format(java.util.Locale.US, "%.1f", score / 10.0).toDoubleOrNull()
        }


        val titleName = media.title?.native
            ?: media.title?.romaji
            ?: media.title?.english
            ?: "タイトル不明"

        val trailerItem = media.trailer?.id?.let { trailerId ->
            TrailerItem(
                id = trailerId,
                site = media.trailer.site,
                thumbnailUrl = media.trailer.thumbnail
                    ?: if (media.trailer.site.equals("youtube", ignoreCase = true)) {
                        "https://img.youtube.com/vi/$trailerId/hqdefault.jpg"
                    } else null
            )
        }

        return AnimeItem(
            id = media.id,
            stringResourceId = titleName,
            imageResourceId = media.coverImage?.large ?: media.coverImage?.medium ?: "",
            rating = parsedRating,
            description = media.description?.trim().orEmpty(),
            genres = media.genres?.filterNotNull()?.takeIf { it.isNotEmpty() } ?: defaultGenres,
            episodes = media.episodes,
            year = media.seasonYear,
            trailer = trailerItem
        )
    }
    suspend fun getCharacterAndCast(animeId: Int): List<CharacterVoiceActorUIModel> {
        val response = apolloClient
            .query(GetAnimeCharacterAndCastQuery(animeId = animeId, perPage = Optional.present(24)))
            .execute()
        val edges = response.data?.Media?.characters?.edges ?: emptyList()

        return edges.mapNotNull { edge ->
            val character = edge?.node ?: return@mapNotNull null
            val voiceActor = edge.voiceActors?.firstOrNull()

            CharacterVoiceActorUIModel(
                characterId = character.id,
                characterName = character.name?.native ?: character.name?.userPreferred ?: "",
                characterRole = edge.role?.name ?: "SUPPORTING",
                characterImageUrl = character.image?.large ?: character.image?.medium,
                voiceActorId = voiceActor?.id,
                voiceActorName = voiceActor?.name?.native ?: voiceActor?.name?.userPreferred,
                voiceActorImageUrl = voiceActor?.image?.large ?: voiceActor?.image?.medium,
                language = voiceActor?.languageV2
            )
        }
    }
}