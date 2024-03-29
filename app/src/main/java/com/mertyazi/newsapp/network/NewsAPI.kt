package com.mertyazi.newsapp.network

import com.mertyazi.newsapp.BuildConfig
import com.mertyazi.newsapp.model.SearchedNews
import com.mertyazi.newsapp.utils.Constants.PAGE_SIZE
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {

    @GET("v2/top-headlines")
    suspend fun getLatestNews(
        @Query("country") code: String = "tr",
        @Query("page") number: Int = 1,
        @Query("apiKey") key: String = BuildConfig.API_KEY
    ): Response<SearchedNews>

    @GET("v2/everything")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("page") number: Int = 1,
        @Query("pageSize") size: Int = PAGE_SIZE,
        @Query("apiKey") key: String = BuildConfig.API_KEY
    ): Response<SearchedNews>

}