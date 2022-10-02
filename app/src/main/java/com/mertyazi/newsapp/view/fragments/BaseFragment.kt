package com.mertyazi.newsapp.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.viewModels
import com.mertyazi.newsapp.R
import com.mertyazi.newsapp.application.NewsApplication
import com.mertyazi.newsapp.view.adapters.NewsAdapter
import com.mertyazi.newsapp.viewmodel.NewsViewModel
import com.mertyazi.newsapp.viewmodel.NewsViewModelFactory

open class BaseFragment : Fragment() {

    var isLoading = false
    var isLastPage = false
    var isScrolling = false
    lateinit var newsAdapter: NewsAdapter

    val viewModel: NewsViewModel by viewModels {
        NewsViewModelFactory(requireActivity().application, (requireActivity().application as NewsApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_base, container, false)
    }

    fun hideProgressBar(progressBar: ProgressBar) {
        progressBar.visibility = View.GONE
        isLoading = false
    }

    fun showProgressBar(progressBar: ProgressBar) {
        progressBar.visibility = View.VISIBLE
        isLoading = true
    }

}