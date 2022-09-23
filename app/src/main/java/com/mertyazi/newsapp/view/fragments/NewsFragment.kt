package com.mertyazi.newsapp.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.mertyazi.newsapp.application.NewsApplication
import com.mertyazi.newsapp.databinding.FragmentNewsBinding
import com.mertyazi.newsapp.view.activities.MainActivity
import com.mertyazi.newsapp.viewmodel.NewsViewModel
import com.mertyazi.newsapp.viewmodel.NewsViewModelFactory

class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NewsViewModel by viewModels {
        NewsViewModelFactory(requireActivity().application, (requireActivity().application as NewsApplication).repository)
    }
    private val args: NewsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val article = args.article
        article.source?.name.let {
            (requireActivity() as MainActivity).supportActionBar?.title = article.source?.name
        }
        binding.wvNews.webViewClient = WebViewClient()
        article.url?.let { binding.wvNews.loadUrl(it) }

        binding.fab.setOnClickListener {
            viewModel.saveArticle(article)
            Toast.makeText(requireActivity(), "Article added to favorites.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}