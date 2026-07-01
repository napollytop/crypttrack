package com.example.test_crypt

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.test_crypt.databinding.ItemNewsBinding

class NewsAdapter(private var articles: List<Article>) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = articles[position]
        holder.binding.apply {
            newsTitle.text = article.title
            newsSource.text = article.sourceId
            newsDate.text = article.pubDate

            Glide.with(root.context)
                .load(article.imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(newsImage)

            root.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.link))
                root.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = articles.size

    fun updateData(newArticles: List<Article>) {
        articles = newArticles
        notifyDataSetChanged()
    }
}
