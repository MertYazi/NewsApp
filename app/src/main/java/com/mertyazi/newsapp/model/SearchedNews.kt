package com.mertyazi.newsapp.model

data class SearchedNews(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)