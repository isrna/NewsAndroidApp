package com.isrna.newsapp.feature_article_list.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.isrna.newsapp.core.data.local.Article
import com.isrna.newsapp.databinding.CardArticleBinding

/**
 * Custom adapter which inherits from [ListAdapter]<[Article],[ArticleListAdapter.CardArticleViewHolder]>([DiffUtil.ItemCallback]<[Article]>).
 * And implements an [ArticleAdapterInterface] listener.
 * @param listener [ArticleAdapterInterface]
 */
class ArticleListAdapter(private val listener: ArticleAdapterInterface) :
    ListAdapter<Article, ArticleListAdapter.CardArticleViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean = oldItem.title == newItem.title
        }
    }

    /**
     * Custom Card view holder, inherits [RecyclerView.ViewHolder]
     */
    inner class CardArticleViewHolder(val binding: CardArticleBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardArticleViewHolder {
        return CardArticleViewHolder(CardArticleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: CardArticleViewHolder, position: Int) {
        holder.binding.apply {
            val article = getItem(position)

            article?.let {
                articleTitle.text = article.title
                articleDescription.text = article.description
                articleImage.load(article.urlToImage) {
                    crossfade(true)
                }

                articleTitle.setOnClickListener{
                    listener.onTitleClick(article)
                }
            }

        }
    }

    /**
     * Simple function to append to the current list of items instead of submiting a fresh one
     * @param list [List]<[Article]>
     */
    fun appendList(list: List<Article>) {
        val newList = currentList.toMutableList() // get the current adapter list as a mutated list
        newList.addAll(list)
        submitList(newList)
    }


}