package com.isrna.newsapp.feature_article_list.data.repository

import android.net.ConnectivityManager
import com.isrna.newsapp.core.data.local.Article
import com.isrna.newsapp.core.data.local.database.dao.ArticleDao
import com.isrna.newsapp.core.data.remote.ArticleListApi
import com.isrna.newsapp.feature_article_list.data.remote.interfaces.ArticleListRemoteDataSourceInterface
import com.isrna.newsapp.feature_article_list.utility.ArticleListCategory
import com.isrna.newsapp.core.ErrorResponses
import com.isrna.newsapp.core.ResponseState
import com.isrna.newsapp.utilitiy.Utilities
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException

/**
 * This repository is used to retrive the listing of [Article]s.
 * It is getting data From [ArticleListRemoteDataSourceInterface] and caching it in [ArticleDao]
 * @param articleListRemoteDataSourceInterface [ArticleListRemoteDataSourceInterface]
 * @param articleDao [ArticleDao]
 * @param connectivityManager [ConnectivityManager] Used to detect online state of the device
 */
class ArticleListRepository(
    private val articleListRemoteDataSourceInterface: ArticleListRemoteDataSourceInterface,
    private val articleDao: ArticleDao,
    private val connectivityManager: ConnectivityManager
)  {
    /**
     * This function is holding the logic for loading paginated articles from [ArticleListRemoteDataSourceInterface] and [ArticleDao]
     * @param articleListCategory [ArticleListCategory]
     * @param page [Int] Page index
     * @param pageSize [Int] Amount of articles per page
     * @return [Flow]<[ResponseState]<[List]<[Article]>>>
     */
    fun loadArticles(articleListCategory: ArticleListCategory, page: Int = 1, pageSize: Int = 20) : Flow<ResponseState<List<Article>>> = flow {
        emit(ResponseState.Loading())
        try {
            if(Utilities.isNetworkAvailable(connectivityManager)) {
                if(page == 1)
                    articleDao.deleteAllArticles(articleListCategory)

                val data: ArticleListApi = when(articleListCategory) {
                    ArticleListCategory.TopHeadLines -> articleListRemoteDataSourceInterface.getTopHeadlines(pageSize, page)
                    ArticleListCategory.Business -> articleListRemoteDataSourceInterface.getBusiness(pageSize, page)
                    ArticleListCategory.Sports -> articleListRemoteDataSourceInterface.getSports(pageSize, page)
                    ArticleListCategory.Entertainment -> articleListRemoteDataSourceInterface.getEntertainment(pageSize, page)
                    ArticleListCategory.None -> {
                        emit(ResponseState.Error(errorResponse = ErrorResponses.UnknownCategory))
                        return@flow
                    }
                }

                if(data.articles.isEmpty())
                {
                    emit(ResponseState.Error(errorResponse = ErrorResponses.NoMoreDataFromApi))
                    return@flow
                }
                else
                    articleDao.insertArticles(data.articles.map { it.toArticleEntity(articleListCategory) })
            }
            else {
                emit(ResponseState.Error(errorResponse = ErrorResponses.NoInternet))
            }
        }
        catch (e: IOException)
        {
            emit(ResponseState.Error(errorResponse = ErrorResponses.NoInternet))
            return@flow
        }
        catch (e: HttpException)
        {
            emit(ResponseState.Error(errorResponse = ErrorResponses.ServerProblem))
            return@flow
        }

        val offset = (page - 1) * pageSize

        val dbData = articleDao.getArticlesPage(articleListCategory, pageSize, offset)

        if(dbData.isEmpty())
        {
            emit(ResponseState.Error(errorResponse = ErrorResponses.NoMoreResults))
            return@flow
        }

        emit(ResponseState.Success(dbData.map { it.toArticle() }))
    }
}