package com.mertyazi.newsapp.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mertyazi.newsapp.R
import com.mertyazi.newsapp.databinding.FragmentSearchNewsBinding
import com.mertyazi.newsapp.utils.Constants
import com.mertyazi.newsapp.utils.Resource
import com.mertyazi.newsapp.view.adapters.NewsAdapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : BaseFragment() {

    private var _binding: FragmentSearchNewsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle()
            bundle.putSerializable("article", it)
            findNavController().navigate(R.id.action_navigation_search_news_to_newsFragment, bundle)
        }

        var job: Job? = null
        binding.etSearchNews.addTextChangedListener { text ->
            job?.cancel()
            job = MainScope().launch {
                delay(Constants.SEARCH_DELAY)
                text?.let {
                    if (text.toString().isNotEmpty()) {
                        Log.e("query", text.toString())
                        viewModel.searchNews(text.toString())
                    }
                }
            }
        }

        viewModel.searchNews.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    hideProgressBar(binding.pbSearchNews)
                    it.data?.let { response ->
                        newsAdapter.articlesList(response.articles.toList())
                        val totalPages = response.totalResults / Constants.PAGE_SIZE + 2
                        isLastPage = viewModel.searchNewsPage == totalPages
                        if (isLastPage) {
                            binding.rvSearchNews.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar(binding.pbSearchNews)
                    it.message?.let { message ->
                        Toast.makeText(requireActivity(), "Error on search: $message", Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar(binding.pbSearchNews)
                }
            }
        })
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter(this)
        binding.rvSearchNews.adapter = newsAdapter
        binding.rvSearchNews.layoutManager = LinearLayoutManager(activity)
        binding.rvSearchNews.addOnScrollListener(this@SearchNewsFragment.scrollListener)
    }

    private val scrollListener = object: RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage &&
                    isAtLastItem &&
                    isNotAtBeginning &&
                    isTotalMoreThanVisible &&
                    isScrolling
            if (shouldPaginate) {
                viewModel.searchNews(binding.etSearchNews.toString())
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}