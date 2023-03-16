package com.mertyazi.newsapp.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.navArgs
import com.mertyazi.newsapp.R
import com.mertyazi.newsapp.databinding.FragmentNewsBinding
import com.mertyazi.newsapp.model.Article
import com.mertyazi.newsapp.view.activities.MainActivity

class NewsFragment : BaseFragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!
    private val args: NewsFragmentArgs by navArgs()
    private lateinit var mArticle: Article

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mArticle = args.article
        mArticle.source?.name.let {
            (requireActivity() as MainActivity).supportActionBar?.title = mArticle.source?.name
        }
        binding.wvNews.webViewClient = WebViewClient()
        mArticle.url?.let { binding.wvNews.loadUrl(it) }

        binding.fab.setOnClickListener {
            viewModel.saveArticle(mArticle)
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.article_added),
                Toast.LENGTH_SHORT
            ).show()
        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.share_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_share_news -> {
                        shareArticle()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun shareArticle() {
        val type = "text/plain"
        val subject = "Checkout this news"
        var extraText = ""
        val shareWith = "Share with"

        mArticle.let {
            extraText =
                "\n Title: \n ${it.title} \n\n Source: \n ${it.source?.name} \n\n Author: \n ${it.author}" +
                        "\n\n Description: \n ${it.description} \n\n Details: \n ${it.content}" +
                        "\n\n Published at: \n ${it.publishedAt}"
        }

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = type
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, extraText)
        startActivity(Intent.createChooser(intent, shareWith))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}