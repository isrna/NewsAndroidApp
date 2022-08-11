package com.isrna.newsapp.feature_search.ui

import com.isrna.newsapp.core.data.local.Article
import com.isrna.newsapp.core.ErrorResponses

/**
 * Class used to manage emitted events to update the UI for Search data
 * Events:
 * UpdateArticleListAdapter
 * NewSearchArticleListAdapter
 * Loading
 * UpdateSearchHistory
 * SearchClosed
 * RefreshSavedSearches
 * Error
 */
sealed class SearchUIEvent {
    data class UpdateArticleListAdapter(val data: List<Article>): SearchUIEvent()
    data class NewSearchArticleListAdapter(val data: List<Article>): SearchUIEvent()
    object Loading : SearchUIEvent()
    data class UpdateSearchHistory(val data: List<String>) : SearchUIEvent()
    object SearchClosed : SearchUIEvent()
    object RefreshSavedSearches: SearchUIEvent()
    data class Error(val errorType: ErrorResponses) : SearchUIEvent()
}