package com.isrna.newsapp.feature_search.data.repository

import android.net.ConnectivityManager
import com.isrna.newsapp.core.data.local.Article
import com.isrna.newsapp.core.data.local.database.dao.SearchArticleDao
import com.isrna.newsapp.core.data.local.database.dao.SearchKeywordDao
import com.isrna.newsapp.core.data.local.database.entities.SearchKeywordEntity
import com.isrna.newsapp.core.data.remote.api.NewsApiOrgInterface
import com.isrna.newsapp.core.services.EventPastSearchService
import com.isrna.newsapp.feature_search.ui.SearchUIEvent
import com.isrna.newsapp.core.ErrorResponses
import com.isrna.newsapp.core.ResponseState
import com.isrna.newsapp.utilitiy.Utilities
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception

/**
 * Repository used to manage search requests, it uses a combination of [NewsApiOrgInterface] requests,
 * and stores them in local database as cached [SearchKeywordDao] [SearchArticleDao]
 * @param searchRemoteDataSource [NewsApiOrgInterface]
 * @param searchArticleDao [SearchArticleDao]
 * @param searchKeywordDao [SearchKeywordDao]
 * @param connectivityManager [ConnectivityManager] Used to detect online state of the device
 */
class SearchRepository(
    private val searchRemoteDataSource: NewsApiOrgInterface,
    private val searchArticleDao: SearchArticleDao,
    private val searchKeywordDao: SearchKeywordDao,
    private val connectivityManager: ConnectivityManager
) {

    /**
     * This function saves the Search by keyword for later use to notify the user for new Articles
     * @param keyword [String]
     * @return Flow<[SearchUIEvent]>
     */
    fun saveSearchByKeyword(keyword: String) : Flow<SearchUIEvent> = flow {
        searchKeywordDao.insertSearchKeyword(SearchKeywordEntity(id = 0, keyword = keyword))
        emit(SearchUIEvent.RefreshSavedSearches)
    }

    /**
     * This function executes the search logic by keyword and retrieves articles.
     * @param keyword [String]
     * @param pageSize [Int] Amount of Articles per page
     * @param pageIndex [Int] Page index
     * @return Flow<[SearchUIEvent]>
     */
    fun searchByKeyword(keyword: String, pageSize: Int = 20, pageIndex: Int = 1) : Flow<ResponseState<List<Article>>>  = flow {
        emit(ResponseState.Loading())
        try {
            if(Utilities.isNetworkAvailable(connectivityManager)) {
                if(pageIndex == 1)
                    searchArticleDao.deleteAllArticles(keyword)

                val apiData = searchRemoteDataSource.searchByKeyword(keyword, pageSize, pageIndex)

                if(apiData.articles.isNotEmpty())
                {
                    val apiToDbData = apiData.articles.map { it.toSearchArticleEntity(keyword) }
                    searchArticleDao.insertArticles(apiToDbData)
                }
                else
                    emit(ResponseState.Error(errorResponse = ErrorResponses.NothingFound))

            } else {
                emit(ResponseState.Error(errorResponse = ErrorResponses.NoInternet))
            }
        }
        catch (e: IOException)
        {
            emit(ResponseState.Error(errorResponse = ErrorResponses.NoInternet))
        }
        catch (e: HttpException)
        {
            emit(ResponseState.Error(errorResponse = ErrorResponses.ServerProblem))
        }

        val offset = (pageIndex - 1) * pageSize

        val dbData = searchArticleDao.getArticlesPage(keyword, pageSize, offset).map { it.toArticle() }

        if (dbData.isEmpty()) {
            emit(ResponseState.Error(errorResponse = ErrorResponses.NoMoreResults))
            return@flow
        }
        emit(ResponseState.Success(dbData))
    }

    /**
     * Tgis function loads stored search keywords which are used to notify the user for new Articles
     * @return Flow<ResponseState<List<String>>>
     */
    fun loadPastSearches() : Flow<ResponseState<List<String>>> = flow {
        try {
            val data = searchKeywordDao.getAllSearchKeywords()

            val list: List<String> = data.map { it.toString() }

            emit(ResponseState.Success(list))
        }
        catch (e: Exception)
        {
            emit(ResponseState.Error(errorResponse = ErrorResponses.UnknownError))
        }
    }

    /**
     * This function is called by the [PastSearchService] to check if new articles are available
     * @return Flow<[EventPastSearchService]>
     */
    fun checkForNewSearchData() : Flow<EventPastSearchService> = flow {
        try {
            val savedSearches = searchKeywordDao.getAllSearchKeywords()

            val newArticleMap: MutableList<Pair<String, String>> = mutableListOf()

            savedSearches.forEach { searchKeywordEntity ->
                val lastSearchArticle =
                    searchKeywordDao.getLastKeywordArticle(searchKeywordEntity.keyword)


                if(lastSearchArticle == null) {
                    val getLastArticleForKeyword =
                        searchRemoteDataSource.searchByKeyword(searchKeywordEntity.keyword, 1, 1)

                    if(getLastArticleForKeyword.articles.isNotEmpty()) {
                        searchArticleDao.insertArticles(
                            getLastArticleForKeyword.articles.map
                            { it.toSearchArticleEntity(searchKeywordEntity.keyword) }
                        )
                        getLastArticleForKeyword.articles[0].title?.let { title ->
                            newArticleMap.add(Pair(searchKeywordEntity.keyword, title))
                        }
                    }
                    return@forEach
                }
                else
                {
                    val lastSearchApiArticle =
                        searchRemoteDataSource.searchByKeyword(searchKeywordEntity.keyword, 1, 1)
                    if (lastSearchApiArticle.articles.isNotEmpty())
                        if (lastSearchArticle.publishedAt != lastSearchApiArticle.articles[0].publishedAt)
                            lastSearchApiArticle.articles[0].title?.let { title ->
                                newArticleMap.add(Pair(searchKeywordEntity.keyword, title))
                                searchArticleDao.deleteAllArticles(searchKeywordEntity.keyword)
                                searchArticleDao.insertArticles(
                                    lastSearchApiArticle.articles.map
                                    { it.toSearchArticleEntity(searchKeywordEntity.keyword) }
                                )
                            }
                }
            }

            if(newArticleMap.isNotEmpty())
                emit(EventPastSearchService.NewResults(newArticleMap))
            else
                emit(EventPastSearchService.NoResults)
        }
        catch (e: IOException) {
            emit(EventPastSearchService.Error(ErrorResponses.NoInternet))
        }
        catch (e: HttpException) {
            emit(EventPastSearchService.Error(ErrorResponses.ServerProblem))
        }
    }

    /**
     * This functions deletes saved Search Keyword which is used to notify the user for new Articles
     */
    fun deleteSearchKeyword(keyword: String) : Flow<SearchUIEvent> = flow {
        try {
            searchKeywordDao.deleteSearchKeyword(keyword)
            searchKeywordDao.deleteAllSearchArticlesByKeywords(keyword)
            emit(SearchUIEvent.RefreshSavedSearches)
        }
        catch (e: Exception)
        {
            emit(SearchUIEvent.Error(ErrorResponses.DeleteError))
        }
    }
}