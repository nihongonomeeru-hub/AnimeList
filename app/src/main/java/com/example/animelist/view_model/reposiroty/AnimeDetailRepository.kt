package com.example.animelist.view_model.reposiroty

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.example.animelist.graphql.GetAnimeCharacterAndCastQuery
import com.example.animelist.ui.model.CharacterVoiceActorUIModel

class AnimeDetailRepository(
    private val apolloClient: ApolloClient
) {
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