package com.isrna.newsapp.feature_article_list.ui

import com.isrna.newsapp.core.data.local.Article
import com.isrna.newsapp.feature_article_list.utility.ArticleListCategory
import com.isrna.newsapp.core.ErrorResponses

/**
 * Class used for Article list UI Events
 * Events:
 * RefreshArticleListAdapter
 * UpdateArticleListAdapter
 * Loading
 * Error
 */
sealed class ArticleListUIEvent {
    data class RefreshArticleListAdapter(val data: List<Article>, val articleListCategory: ArticleListCategory): ArticleListUIEvent()
    data class UpdateArticleListAdapter(val data: List<Article>, val articleListCategory: ArticleListCategory): ArticleListUIEvent()
    object Loading : ArticleListUIEvent()
    data class Error(val errorType: ErrorResponses) : ArticleListUIEvent()
}