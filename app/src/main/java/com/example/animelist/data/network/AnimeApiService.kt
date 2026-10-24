package com.example.animelist.data.network

import com.example.animelist.data.network.model.KitsuResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AnimeApiService {
    @GET("anime?sort=-user_count")
    suspend fun getPopularAnime(
        @Query("page[limit]") limit: Int = 20
    ): KitsuResponse

    @GET("anime?sort=-average_rating")
    suspend fun getTopRatedAnime(
        @Query("page[limit]") limit: Int = 20
    ): KitsuResponse

    @GET("anime?filter[status]=current&sort=-user_count")
    suspend fun getAiringAnime(
        @Query("page[limit]") limit: Int = 20
    ): KitsuResponse

    @GET("anime?filter[categories]=action&sort=-user_count")
    suspend fun getActionAnime(
        @Query("page[limit]") limit: Int = 20
    ): KitsuResponse

    @GET("anime?filter[categories]=comedy")
    suspend fun getComedyAnime(
        @Query("page[limit]") limit: Int = 20
    ): KitsuResponse

    @GET("anime")
    suspend fun searchAnime(
        @Query("filter[text]") query: String,
        @Query("page[limit]") limit: Int = 20
    ): KitsuResponse
}