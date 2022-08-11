package com.isrna.newsapp.feature_article_list.ui

import androidx.recyclerview.widget.RecyclerView

/**
 * Listener for [RecyclerView.OnScrollListener], used to load more data when the user reaches the end of list.
 */
abstract class ArticleRecyclerViewerOnScrollListener : RecyclerView.OnScrollListener() {
    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (!recyclerView.canScrollVertically(1)) {
            if(!isLoading())
                loadMoreItems()
        }
    }
    protected abstract fun loadMoreItems()
    abstract fun isLoading(): Boolean
}