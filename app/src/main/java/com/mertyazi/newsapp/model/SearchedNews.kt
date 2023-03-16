package com.mertyazi.newsapp.model

data class SearchedNews(
    var articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)