package com.mertyazi.newsapp.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.*
import com.mertyazi.newsapp.application.NewsApplication
import com.mertyazi.newsapp.database.NewsRepository
import com.mertyazi.newsapp.model.Article
import com.mertyazi.newsapp.model.SearchedNews
import com.mertyazi.newsapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.lang.IllegalArgumentException

class NewsViewModel(
    app: Application,
    private val repository: NewsRepository
    ): AndroidViewModel(app) {

    val latestNews: MutableLiveData<Resource<SearchedNews>> = MutableLiveData()
    var latestNewsPage = 1
    var latestNewsResponse: SearchedNews? = null

    val searchNews: MutableLiveData<Resource<SearchedNews>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: SearchedNews? = null

    init {
        getLatestNews("tr")
    }

    fun getLatestNews(code: String) = viewModelScope.launch {
        safeLatestNewsCall(code)
    }

    fun searchNews(query: String) = viewModelScope.launch {
        safeSearchNewsCall(query)
    }

    private fun handleLatestNewsResponse(response: Response<SearchedNews>): Resource<SearchedNews> {
        if (response.isSuccessful) {
            response.body()?.let {
                latestNewsPage++
                if (latestNewsResponse == null) {
                    latestNewsResponse = it
                } else {
                    val oldNews = latestNewsResponse?.articles
                    val newArticles = it.articles
                    oldNews?.addAll(newArticles)
                }
                return Resource.Success(latestNewsResponse ?: it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<SearchedNews>): Resource<SearchedNews> {
        if (response.isSuccessful) {
            response.body()?.let {
                searchNewsPage++
                if (searchNewsResponse == null) {
                    searchNewsResponse = it
                } else {
                    val oldNews = searchNewsResponse?.articles
                    val newArticles = it.articles
                    oldNews?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: it)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        repository.insert(article)
    }

    fun getSavedNews() = repository.getSavedNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        repository.deleteArticle(article)
    }

    private suspend fun safeLatestNewsCall(code: String) {
        latestNews.postValue(Resource.Loading())
        try {
            if (checkConnection()) {
                val response = repository.getLatestNews(code, latestNewsPage)
                latestNews.postValue(handleLatestNewsResponse(response))
            } else {
                latestNews.postValue(Resource.Error("No connection"))
            }
        } catch(t: Throwable) {
            when (t) {
                is IOException -> latestNews.postValue(Resource.Error("Network error"))
                else -> latestNews.postValue(Resource.Error("Conversion error"))
            }
        }
    }

    private suspend fun safeSearchNewsCall(query: String) {
        searchNews.postValue(Resource.Loading())
        try {
            if (checkConnection()) {
                val response = repository.searchNews(query, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            } else {
                searchNews.postValue(Resource.Error("No connection"))
            }
        } catch(t: Throwable) {
            when (t) {
                is IOException -> searchNews.postValue(Resource.Error("Network error"))
                else -> searchNews.postValue(Resource.Error("Conversion error"))
            }
        }
    }

    private fun checkConnection(): Boolean {
        val connectivityManager = getApplication<NewsApplication>()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}

class NewsViewModelFactory(
    val app: Application,
    private val repository: NewsRepository
    ): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            return NewsViewModel(app, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}