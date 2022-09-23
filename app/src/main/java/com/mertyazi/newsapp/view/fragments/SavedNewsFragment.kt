package com.mertyazi.newsapp.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mertyazi.newsapp.R
import com.mertyazi.newsapp.application.NewsApplication
import com.mertyazi.newsapp.databinding.FragmentSavedNewsBinding
import com.mertyazi.newsapp.model.Article
import com.mertyazi.newsapp.view.adapters.NewsAdapter
import com.mertyazi.newsapp.viewmodel.NewsViewModel
import com.mertyazi.newsapp.viewmodel.NewsViewModelFactory

class SavedNewsFragment : Fragment() {

    private var _binding: FragmentSavedNewsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NewsViewModel by viewModels {
        NewsViewModelFactory(requireActivity().application, (requireActivity().application as NewsApplication).repository)
    }
    lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle()
            bundle.putSerializable("article", it)
            findNavController().navigate(R.id.action_navigation_saved_news_to_newsFragment, bundle)
        }

        viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { articles ->
            newsAdapter.articlesList(articles)
        })
    }

    fun deleteSavedNews(article: Article) {
        viewModel.deleteArticle(article)
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter(this)
        binding.rvSavedNews.adapter = newsAdapter
        binding.rvSavedNews.layoutManager = LinearLayoutManager(activity)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}