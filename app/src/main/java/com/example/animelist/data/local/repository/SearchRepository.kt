package com.example.animelist.data.local.repository

import com.apollographql.apollo.ApolloClient
import com.example.animelist.ui.model.AnimeItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.apollographql.apollo.api.Optional
import com.apollographql.apollo.exception.ApolloException
import com.example.animelist.graphql.SearchAnimeQuery
import com.example.animelist.graphql.fragment.AnimeCardFields
import kotlinx.coroutines.CancellationException
import kotlin.math.round

interface SearchRepository {
    suspend fun searchAnime(query: String): Result<List<AnimeItem>>
    suspend fun fetchTrendingAnime(): Result<List<AnimeItem>>
    suspend fun searchByGenre(genre: String): Result<List<AnimeItem>>
}

class SearchRepositoryImpl(
    private val apolloClient: ApolloClient,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): SearchRepository {
    private suspend fun executeSearch(
        search: Optional<String> = Optional.absent(),
        genre: Optional<String> = Optional.absent()
    ): Result<List<AnimeItem>> = withContext(ioDispatcher) {
            try {
                val response = apolloClient
                    .query(
                        SearchAnimeQuery(
                            search = search,
                            page = Optional.present(1),
                            perPage = Optional.present(50),
                            genre = genre
                        )
                    )
                    .execute()

                if (response.hasErrors()) {
                    throw IllegalStateException(
                        response.errors?.firstOrNull()?.message ?: "アニメの情報が見つかりませんでした"
                    )
                }

                val data = response.data ?: throw Exception("データが取得できませんでした")

                val items = data.Page?.media
                    ?.mapNotNull { it?.animeCardFields }
                    ?.toAnimeItems()
                    .orEmpty()

                Result.success(items)
            } catch (e: CancellationException) {
                throw e
            } catch(e: ApolloException) {
                Result.failure(e)
            } catch(e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun fetchTrendingAnime(): Result<List<AnimeItem>> =
        executeSearch()

    override suspend fun searchAnime(query: String): Result<List<AnimeItem>> =
        executeSearch(search = Optional.present(query))


    override suspend fun searchByGenre(genre: String): Result<List<AnimeItem>> =
        executeSearch(genre = Optional.present(genre))


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
}