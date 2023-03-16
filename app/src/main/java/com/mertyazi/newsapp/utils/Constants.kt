package com.mertyazi.newsapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import com.mertyazi.newsapp.application.NewsApplication

object Constants {
    const val BASE_URL: String = "https://newsapi.org/"
    const val SEARCH_DELAY: Long = 500L
    const val PAGE_SIZE: Int = 20
    const val MAX_PAGE_COUNT: Int = 6
    const val NEWS_LANGUAGE: String = "tr"

    const val NOTIFICATION_ID = "NewsApp_notification_id"
    const val NOTIFICATION_NAME = "NewsApp"
    const val NOTIFICATION_CHANNEL = "NewsApp_channel_01"

    fun checkConnection(viewModel: AndroidViewModel): Boolean {
        val connectivityManager = viewModel.getApplication<NewsApplication>()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}