package com.example.animelist.data.local.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.apollographql.apollo.exception.ApolloException
import com.example.animelist.graphql.GetAnimeCharacterAndCastQuery
import com.example.animelist.graphql.GetAnimeDetailQuery
import com.example.animelist.graphql.type.MediaFormat
import com.example.animelist.graphql.type.MediaRelation
import com.example.animelist.graphql.type.MediaType
import com.example.animelist.ui.model.AnimeItem
import com.example.animelist.ui.model.CharacterVoiceActorUIModel
import com.example.animelist.ui.model.RelatedMediaItem
import com.example.animelist.ui.model.TrailerItem
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.round

interface AnimeDetailRepository {
    suspend fun getAnimeDetail(
        animeId: Int,
        mediaType: MediaType = MediaType.ANIME,
        defaultGenres: List<String> = listOf(FALLBACK_GENRE)
    ): Result<AnimeItem>

    suspend fun getCharacterAndCast(animeId: Int): Result<List<CharacterVoiceActorUIModel>>

    companion object {
        const val FALLBACK_GENRE = "アニメ"
    }
}

class AnimeDetailRepositoryImpl(
    private val apolloClient: ApolloClient,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): AnimeDetailRepository {
    override suspend fun getAnimeDetail(
        animeId: Int,
        mediaType: MediaType,
        defaultGenres: List<String>,
    ): Result<AnimeItem> = safeApiCall {
        val response = apolloClient
            .query(GetAnimeDetailQuery(animeId = animeId, mediaType = mediaType))
            .execute()

        if (response.hasErrors()) {
            throw IllegalStateException(
                response.errors?.firstOrNull()?.message ?: "アニメの情報が見つかりませんでした"
            )
        }

        val media = response.data?.Media
            ?: throw IllegalStateException("アニメの情報が見つかりませんでした")

        media.toAnimeItem(defaultGenres)
    }

    override suspend fun getCharacterAndCast(
        animeId: Int
    ): Result<List<CharacterVoiceActorUIModel>> = safeApiCall {
        val response = apolloClient
            .query(GetAnimeCharacterAndCastQuery(
                animeId = animeId,
                perPage = Optional.present(CHARACTER_PAGE_SIZE)
            ))
            .execute()


        if (response.hasErrors()) {
            throw IllegalStateException(
                response.errors?.firstOrNull()?.message ?: "アニメの情報が見つかりませんでした"
            )
        }

        val edges = response.data?.Media?.characters?.edges.orEmpty()
        edges.mapNotNull { it?.toCharacterUiModel() }
    }

    private suspend inline fun <T> safeApiCall(
        crossinline block: suspend () -> T
    ): Result<T>  = withContext(ioDispatcher){
        try {
            Result.success(block())
        } catch (e: CancellationException) {
            throw e
        } catch(e: ApolloException) {
            Result.failure(e)
        } catch(e: Exception) {
            Result.failure(e)
        }
    }

    private companion object {
        private const val CHARACTER_PAGE_SIZE = 24
    }
}

private fun GetAnimeDetailQuery.Media.toAnimeItem(
    defaultGenres: List<String>
): AnimeItem {
    val parsedRating = weightedScore?.let { score ->
        round(score.toDouble()) / 10.0
    }

    val titleName = title?.native
        ?: title?.romaji
        ?: title?.english
        ?: "タイトル不明"

    val trailerItem = trailer?.id?.let { trailerId ->
        TrailerItem(
            id = trailerId,
            site = trailer.site,
            thumbnailUrl = trailer.thumbnail
                ?: if (trailer.site.equals("youtube", ignoreCase = true)) {
                    "https://img.youtube.com/vi/$trailerId/hqdefault.jpg"
                } else null
        )
    }

    val relatedMediaList = relations?.edges
        ?.mapNotNull { it?.toRelatedMediaItem() }
        ?.sortedBy { if (it.relationLabel == "原作" || it.relationLabel == "アニメ化") 0 else 1 }
        .orEmpty()

    return AnimeItem(
        id = id,
        title = titleName,
        imageUrl = coverImage?.extraLarge ?: coverImage?.large ?: "",
        rating = parsedRating,
        description = description?.trim().orEmpty(),
        genres = genres?.filterNotNull()?.takeIf { it.isNotEmpty() } ?: defaultGenres,
        episodes = episodes,
        year = seasonYear,
        status = status?.rawValue,
        mediaType = type?.rawValue ?: "ANIME",
        volumes = volumes,
        chapters = chapters,
        trailer = trailerItem,
        relatedMedia = relatedMediaList
    )
}

private fun GetAnimeDetailQuery.Edge.toRelatedMediaItem(): RelatedMediaItem? {

    val parsedRating = node?.weightedScore?.let { score ->
        round(score.toDouble()) / 10.0
    }
    val node = node ?: return null
    val nodeType = node.type ?: return null
    if(node.type != MediaType.MANGA && node.type != MediaType.ANIME) return null

    val formatLabel = when(node.format) {
        MediaFormat.NOVEL -> "ライトノベル"
        MediaFormat.ONE_SHOT -> "読切"
        MediaFormat.MANGA -> "漫画"
        MediaFormat.TV -> "TVアニメ"
        MediaFormat.MOVIE -> "劇場版"
        MediaFormat.OVA -> "OVA"
        else -> if (nodeType == MediaType.MANGA) "漫画" else "アニメ"
    }

    val relationLabel = when(relationType) {
        MediaRelation.SOURCE -> "原作"
        MediaRelation.ADAPTATION -> "アニメ化"
        MediaRelation.SPIN_OFF -> "スピンオフ"
        MediaRelation.SIDE_STORY -> "外伝"
        MediaRelation.SEQUEL -> "続編"
        MediaRelation.PREQUEL -> "前作"
        else -> "関連"
    }

    val titleName = node.title?.native
        ?: node.title?.romaji
        ?: node.title?.english
        ?: "タイトル不明"

    return RelatedMediaItem(
        id = node.id,
        mediaType = nodeType,
        title = titleName,
        imageUrl = node.coverImage?.large ?: node.coverImage?.medium ?: "",
        formatLabel = formatLabel,
        relationLabel = relationLabel,
        rating = parsedRating,
        volumes = node.volumes,
        chapters = node.chapters
    )
}

private fun GetAnimeCharacterAndCastQuery.Edge.toCharacterUiModel(): CharacterVoiceActorUIModel? {
    val character =  node ?: return null
    val voiceActor = voiceActors?.firstOrNull()

    return CharacterVoiceActorUIModel(
        characterId = character.id,
        characterName = character.name?.full.orEmpty(),
        characterRole = role?.name ?: "SUPPORTING",
        characterImageUrl = character.image?.large,
        voiceActorId = voiceActor?.id,
        voiceActorName = voiceActor?.name?.full,
        voiceActorImageUrl = voiceActor?.image?.large,
        language = voiceActor?.languageV2
    )
}