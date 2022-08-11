package com.isrna.newsapp.feature_article_list.ui.adapters

import com.isrna.newsapp.core.data.local.Article

/**
 * Interface to for managing when user clicks on the Title in a RecycleView item
 */
interface ArticleAdapterInterface {
    /**
     * Callback to trigger when the Title in a RecycleView item is clicked
     * @param article [Article]
     * @return void
     */
    fun onTitleClick(article: Article)
}