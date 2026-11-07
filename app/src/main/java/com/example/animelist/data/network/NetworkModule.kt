package com.example.animelist.data.network

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.network.okHttpClient
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object NetworkModule {
    private const val BASE_URL = "https://graphql.anilist.co"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    val apolloClient: ApolloClient = ApolloClient.Builder()
        .serverUrl(BASE_URL)
        .okHttpClient(okHttpClient)
        .build()
}