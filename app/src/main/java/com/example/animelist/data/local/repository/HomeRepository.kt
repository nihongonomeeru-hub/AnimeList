package com.example.animelist.data.local.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.apollographql.apollo.exception.ApolloException
import com.example.animelist.graphql.GetAnimeListQuery
import com.example.animelist.graphql.fragment.AnimeCardFields
import com.example.animelist.ui.model.AnimeItem
import com.example.animelist.ui.model.AnimeSection
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.collections.orEmpty
import kotlin.math.round

interface HomeRepository {
    suspend fun getHomeSections(year: Int): Result<List<AnimeSection>>
}

class HomeRepositoryImpl(
    private val apolloClient: ApolloClient,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): HomeRepository {
    override suspend fun getHomeSections(year: Int): Result<List<AnimeSection>> =
        withContext(ioDispatcher) {
            try {
                val response = apolloClient
                    .query(GetAnimeListQuery(
                        year = Optional.present(year)))
                    .execute()

                if (response.hasErrors()) {
                    throw IllegalStateException(
                        response.errors?.firstOrNull()?.message ?: "アニメの情報が見つかりませんでした"
                    )
                }

                val data = response.data
                    ?: throw Exception("データが取得できませんでした")

                Result.success(data.toAnimeSections())

            } catch (e: CancellationException) {
                throw e
            } catch(e: ApolloException) {
                Result.failure(e)
            } catch(e: Exception) {
                Result.failure(e)
            }
        }
}

private fun GetAnimeListQuery.Data.toAnimeSections(): List<AnimeSection> = listOf(
    AnimeSection(
        id = 1,
        title = "人気アニメ",
        items = popular?.media?.mapNotNull { it?.animeCardFields }.orEmpty().toAnimeItems(
            defaultGenres = listOf("人気", "アクション", "ドラマ")
        )
    ),
    AnimeSection(
        id = 2,
        title = "高評価アニメ",
        items = topRated?.media?.mapNotNull { it?.animeCardFields }.orEmpty().toAnimeItems(
            defaultGenres = listOf("名作", "高評価")
        )
    ),
    AnimeSection(
        id = 3,
        title = "今年のアニメ",
        items = this_year?.media?.mapNotNull { it?.animeCardFields }.orEmpty().toAnimeItems(
            defaultGenres = listOf("放送中", "最新作")
        )
    ),
    AnimeSection(
        id = 4,
        title = "放送中のアニメ",
        items =airing?.media?.mapNotNull { it?.animeCardFields }.orEmpty().toAnimeItems(
            defaultGenres = listOf("放送中", "最新作")
        )
    ),
    AnimeSection(
        id = 5,
        title = "今期のアニメ",
        items = season?.media?.mapNotNull { it?.animeCardFields }.orEmpty().toAnimeItems(
            defaultGenres = listOf("今期", "最新作")
        )
    ),
    AnimeSection(
        id = 6,
        title = "おすすめ",
        items = recommendations?.media?.mapNotNull { it?.animeCardFields }.orEmpty().toAnimeItems(
            defaultGenres = listOf("今期", "最新作")
        )
    ),
    AnimeSection(
        id = 7,
        title = "アクション特集",
        items = action?.media?.mapNotNull { it?.animeCardFields }.orEmpty().toAnimeItems(
            defaultGenres = listOf("バトル", "冒険", "少年")
        )
    ),
    AnimeSection(
        id = 8,
        title = "コメディ",
        items = comedy?.media?.mapNotNull { it?.animeCardFields }.orEmpty().toAnimeItems(
            defaultGenres = listOf("コメディ")
        )
    )
)

private fun List<AnimeCardFields>.toAnimeItems(
    defaultGenres: List<String> = listOf("アニメ")
): List<AnimeItem> = map { fields ->
    val parsedRating = fields.weightedScore?.let { score ->
        round(score.toDouble()) / 10.0
    }

    val titleName = fields.title?.native
        ?: fields.title?.romaji
        ?: fields.title?.english
        ?: "タイトル不明"

    AnimeItem(
        id = fields.id,
        title = titleName,
        imageUrl = fields.coverImage?.large ?: fields.coverImage?.medium ?: "",
        rating = parsedRating,
        description = fields.description?.trim().orEmpty(),
        genres = fields.genres?.filterNotNull()?.takeIf { it.isNotEmpty() } ?: defaultGenres,
        episodes = fields.episodes,
        year = fields.seasonYear
    )
}