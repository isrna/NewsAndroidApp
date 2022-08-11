package com.isrna.newsapp.feature_article.ui

import com.isrna.newsapp.core.ErrorResponses
import com.isrna.newsapp.core.data.local.Article

sealed class ArticleUIEvent {
    data class UpdateData(val data: Article) : ArticleUIEvent()
    data class Error(val errorType: ErrorResponses): ArticleUIEvent()
}