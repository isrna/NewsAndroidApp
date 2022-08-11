package com.isrna.newsapp.feature_article.data.repository

import com.isrna.newsapp.core.data.local.Article
import com.isrna.newsapp.core.data.local.database.dao.ArticleDao
import com.isrna.newsapp.core.data.local.database.dao.SearchArticleDao
import com.isrna.newsapp.core.ErrorResponses
import com.isrna.newsapp.core.ResponseState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class ArticleRepository(
    private val articleDao: ArticleDao,
    private val searchArticleDao: SearchArticleDao
    ) {
    /**
     * Get the article from local database to show to the user
     * @param articleId Int The ID of the Article
     * @param searchBased Boolean - true if the article is from search view, false if its not
     * @return Flow<[ResponseState]<[Article]>>
     */
    fun getArticle(articleId: Int, searchBased: Boolean = false) : Flow<ResponseState<Article>> = flow {
        try {
            val article: Article = if(searchBased) searchArticleDao.getArticle(articleId).toArticle() else articleDao.getArticle(articleId).toArticle()

            emit(ResponseState.Success(article))
        }
        catch (e: Exception)
        {
            emit(ResponseState.Error(errorResponse = ErrorResponses.FailedToOpenArticle))
        }
    }
}