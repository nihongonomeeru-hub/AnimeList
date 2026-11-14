package com.example.animelist.data.local.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.exception.ApolloException
import com.example.animelist.graphql.GetCharacterDetailQuery
import com.example.animelist.graphql.GetStaffDetailQuery
import com.example.animelist.graphql.type.CharacterRole
import com.example.animelist.graphql.type.MediaType
import com.example.animelist.ui.model.CharacterAppearanceItem
import com.example.animelist.ui.model.CharacterDetailUIModel
import com.example.animelist.ui.model.StaffDetailUIModel
import com.example.animelist.ui.model.StaffRoleItem
import com.example.animelist.ui.model.VoiceActorSummary
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.round

interface PersonDetailRepository {
    suspend fun getCharacterDetail(characterId: Int): Result<CharacterDetailUIModel>
    suspend fun getStaffDetail(staffId: Int): Result<StaffDetailUIModel>
}

class PersonDetailRepositoryImpl(
    private val apolloClient: ApolloClient,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): PersonDetailRepository {
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

    override suspend fun getCharacterDetail(
        characterId: Int
    ): Result<CharacterDetailUIModel> = safeApiCall() {
        val response = apolloClient
            .query(GetCharacterDetailQuery(characterId = characterId))
            .execute()

        if (response.hasErrors()) {
            throw IllegalStateException(
                response.errors?.firstOrNull()?.message ?: "キャラクター情報が見つかりませんでした"
            )
        }

        val character = response.data?.Character
            ?: throw Exception("データが取得できませんでした")

        character.toUiModel()
    }

    override suspend fun getStaffDetail(
        staffId: Int
    ): Result<StaffDetailUIModel> = safeApiCall() {
        val response = apolloClient
            .query(GetStaffDetailQuery(staffId = staffId))
            .execute()

        if (response.hasErrors()) {
            throw IllegalStateException(
                response.errors?.firstOrNull()?.message ?: "キャラクター情報が見つかりませんでした"
            )
        }

        val staff = response.data?.Staff
            ?: throw Exception("データが取得できませんでした")

        staff.toUiModel()
    }

    private fun GetCharacterDetailQuery.Character.toUiModel(): CharacterDetailUIModel {
        val edges = media?.edges.orEmpty().filterNotNull()

        val uniqueVoiceActors = edges
            .flatMap { it.voiceActors.orEmpty().filterNotNull() }
            .distinctBy { it.id }
            .map { va ->
                VoiceActorSummary(
                    id = va.id,
                    nativeName = va.name?.native ?: va.name?.full ?: "不明",
                    fullName = va.name?.full.orEmpty(),
                    imageUrl = va.image?.large ?: va.image?.medium ?: "",
                    language = va.languageV2 ?: "Japanese"
                )
            }

        val appearanceList = edges.mapNotNull { edge ->
            val node = edge.node ?: return@mapNotNull null
            val mediaType = node.type ?: return@mapNotNull null
            if(mediaType != MediaType.MANGA && mediaType != MediaType.ANIME) return@mapNotNull null

            val parsedRating = node.weightedScore?.let { score ->
                round(score.toDouble()) / 10.0
            }

            val titleName = node.title?.native
                ?: node.title?.romaji
                ?: node.title?.english
                ?: "タイトル不明"

            CharacterAppearanceItem(
                mediaId = node.id,
                mediaType = mediaType,
                mediaTitle = titleName,
                mediaImageUrl = node.coverImage?.large ?: node.coverImage?.medium ?: "",
                roleLabel = edge.characterRole.toJapaneseRole(),
                rating = parsedRating
            )
        }

        val birthDateText = if(dateOfBirth?.month != null && dateOfBirth.day != null) {
            "${dateOfBirth.month}月${dateOfBirth.day}日"
        } else null

        return CharacterDetailUIModel(
            id = id,
            nativeName = name?.native ?: name?.full ?: "不明",
            fullName = name?.full.orEmpty(),
            imageUrl = image?.large ?: image?.medium ?: "",
            gender = gender.toJapaneseGender(),
            birthDate = birthDateText,
            age = age,
            bloodType = bloodType?.let { "${it}型" },
            description = description?.trim().orEmpty(),
            voiceActors = uniqueVoiceActors,
            appearances = appearanceList
        )
    }
}

private fun GetStaffDetailQuery.Staff.toUiModel(): StaffDetailUIModel {
    val birthDateText = buildString {
        if (dateOfBirth?.year != null) append("${dateOfBirth.year}年")
        if (dateOfBirth?.month != null && dateOfBirth.day != null) {
            append("${dateOfBirth.month}月${dateOfBirth.day}日")
        }
    }.takeIf { it.isNotEmpty() }

    val activeYearsText = yearsActive?.filterNotNull()?.let { years ->
        when (years.size) {
            0 -> null
            1 -> "${years[0]}年〜現在"
            else -> "${years[0]}年〜${years[1]}年"
        }
    }

    val roleItems = characters?.edges.orEmpty()
        .mapNotNull { edge ->
            val charNode = edge?.node ?: return@mapNotNull null
            val mainMedia = edge.media.orEmpty()
                .filterNotNull()
                .sortedBy { if (it.type == MediaType.ANIME) 0 else 1 }
                .firstOrNull() ?: return@mapNotNull null

            val mediaType = mainMedia.type ?: MediaType.ANIME
            val mediaTitle = mainMedia.title?.native
                ?: mainMedia.title?.romaji
                ?: mainMedia.title?.english
                ?: "タイトル不明"

            StaffRoleItem(
                characterId = charNode.id,
                characterName = charNode.name?.native ?: charNode.name?.full ?: "不明",
                characterImageUrl = charNode.image?.large ?: charNode.image?.medium ?: "",
                roleLabel = edge.role.toJapaneseRole(),
                mediaId = mainMedia.id,
                mediaType = mediaType,
                mediaTitle = mediaTitle,
                mediaImageUrl = mainMedia.coverImage?.large ?: mainMedia.coverImage?.medium ?: ""
            )
        }
        .distinctBy { "${it.characterId}_${it.mediaId}" }

    return StaffDetailUIModel(
        id = id,
        nativeName = name?.native ?: name?.full ?: "不明",
        fullName = name?.full.orEmpty(),
        imageUrl = image?.large ?: image?.medium ?: "",
        homeTown = homeTown,
        birthDate = birthDateText,
        age = age?.toString()?.let { "${it}歳" },
        bloodType = bloodType?.let { "${it}型" },
        yearsActive = activeYearsText,
        description = description?.trim().orEmpty(),
        roles = roleItems
    )
}

private fun CharacterRole?.toJapaneseRole(): String = when (this) {
    CharacterRole.MAIN -> "メイン"
    CharacterRole.SUPPORTING -> "サブ"
    CharacterRole.BACKGROUND -> "背景"
    else -> "出演"
}

private fun String?.toJapaneseGender(): String? = when (this?.lowercase()) {
    "male" -> "男性"
    "female" -> "女性"
    else -> this
}