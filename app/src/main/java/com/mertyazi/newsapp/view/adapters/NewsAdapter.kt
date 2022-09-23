package com.mertyazi.newsapp.view.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mertyazi.newsapp.databinding.ItemNewsBinding
import com.mertyazi.newsapp.model.Article
import com.mertyazi.newsapp.view.fragments.SavedNewsFragment

class NewsAdapter(private val fragment: Fragment): RecyclerView.Adapter<NewsAdapter.ViewHolder>() {
    private var articles: List<Article> = listOf()

    class ViewHolder(view: ItemNewsBinding): RecyclerView.ViewHolder(view.root) {
        val newsImage = view.ivNews
        val newsSource = view.contentSource
        val newsCaption = view.contentCaption
        val newsText = view.contentText
        val newsDelete = view.ibNews
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemNewsBinding = ItemNewsBinding.inflate(
            LayoutInflater.from(fragment.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articles[position]
        Glide.with(fragment)
            .load(article.urlToImage)
            .into(holder.newsImage)
        holder.newsSource.text = article.source?.name
        holder.newsCaption.text = article.title
        holder.newsText.text = article.description
        if (fragment is SavedNewsFragment) {
            holder.newsDelete.visibility = View.VISIBLE
            holder.newsDelete.setOnClickListener {
                fragment.deleteSavedNews(article)
            }
        }
        holder.itemView.setOnClickListener {
            onItemClickListener?.let {
                it(article)
            }
        }
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun articlesList(list: List<Article>) {
        articles = list
        notifyDataSetChanged()
    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

}