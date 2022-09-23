package com.mertyazi.newsapp.database

import com.mertyazi.newsapp.model.Article
import com.mertyazi.newsapp.network.NewsAPIService

class NewsRepository(private val newsDao: NewsDao) {

    suspend fun getLatestNews(code: String, page: Int) = NewsAPIService.api.getLatestNews(code, page)

    suspend fun searchNews(query: String, page: Int) = NewsAPIService.api.searchNews(query, page)

    suspend fun insert(article: Article) = newsDao.insert(article)

    fun getSavedNews() = newsDao.getAllArticles()

    suspend fun deleteArticle(article: Article) = newsDao.deleteArticle(article)

}