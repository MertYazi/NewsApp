package com.mertyazi.newsapp.application

import android.app.Application
import com.mertyazi.newsapp.database.NewsDatabase
import com.mertyazi.newsapp.database.NewsRepository

class NewsApplication: Application() {

    private val database by lazy { NewsDatabase.getDatabase(this@NewsApplication) }

    val repository by lazy { NewsRepository(database.getNewsDao()) }
}