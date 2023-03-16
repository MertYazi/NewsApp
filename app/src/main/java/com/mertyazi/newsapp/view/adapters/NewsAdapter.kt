package com.mertyazi.newsapp.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mertyazi.newsapp.databinding.ItemNewsBinding
import com.mertyazi.newsapp.model.Article
import com.mertyazi.newsapp.view.fragments.SavedNewsFragment

class NewsAdapter(private val fragment: Fragment): RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    class ViewHolder(view: ItemNewsBinding): RecyclerView.ViewHolder(view.root) {
        val newsImage = view.ivNews
        val newsSource = view.contentSource
        val newsCaption = view.contentCaption
        val newsText = view.contentText
        val newsDelete = view.ibNews
    }

    private val differCallback = object: DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemNewsBinding = ItemNewsBinding.inflate(
            LayoutInflater.from(fragment.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.apply {
            Glide.with(fragment)
                .load(article.urlToImage)
                .into(newsImage)
            newsSource.text = article.source?.name
            newsCaption.text = article.title
            newsText.text = article.description
            if (fragment is SavedNewsFragment) {
                newsDelete.visibility = View.VISIBLE
                newsDelete.setOnClickListener {
                    fragment.deleteSavedNews(article)
                }
            }
            itemView.setOnClickListener {
                onItemClickListener?.let {
                    it(article)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

}