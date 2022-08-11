package com.isrna.newsapp.feature_article_list.data.remote.datasources

import com.isrna.newsapp.core.data.remote.ArticleListApi
import com.isrna.newsapp.core.data.remote.api.NewsApiOrgInterface
import com.isrna.newsapp.feature_article_list.data.remote.interfaces.ArticleListRemoteDataSourceInterface

/**
 * Remote data source for articles
 * @param newsApiOrgInterface: [NewsApiOrgInterface]
 */
class ArticleListRemoteDataSource(
    private val newsApiOrgInterface: NewsApiOrgInterface
) : ArticleListRemoteDataSourceInterface {
    override suspend fun getTopHeadlines(pageSize:Int, page: Int): ArticleListApi = newsApiOrgInterface.getTopHeadLines(pageSize, page)
    override suspend fun getBusiness(pageSize:Int, page: Int): ArticleListApi = newsApiOrgInterface.getBusiness(pageSize, page)
    override suspend fun getEntertainment(pageSize:Int, page: Int): ArticleListApi = newsApiOrgInterface.getEntertainment(pageSize, page)
    override suspend fun getSports(pageSize:Int, page: Int): ArticleListApi = newsApiOrgInterface.getSports(pageSize, page)
}