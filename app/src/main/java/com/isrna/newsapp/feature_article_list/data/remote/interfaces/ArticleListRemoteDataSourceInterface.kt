package com.isrna.newsapp.feature_article_list.data.remote.interfaces

import com.isrna.newsapp.core.data.remote.ArticleListApi

/**
 * Interface for creating [ArticleListRemoteDataSource]
 */
interface ArticleListRemoteDataSourceInterface {
    /**
     * Get Top Headlines
     * @param pageSize [Int] Amount of articles to show on page
     * @param page [Int] Page index
     * @return [ArticleListApi]
     */
    suspend fun getTopHeadlines(pageSize:Int, page: Int): ArticleListApi
    /**
     * Get Top Headlines in Business
     * @param pageSize [Int] Amount of articles to show on page
     * @param page [Int] Page index
     * @return [ArticleListApi]
     */
    suspend fun getBusiness(pageSize:Int, page: Int) : ArticleListApi
    /**
     * Get Top Headlines in Entertainment
     * @param pageSize [Int] Amount of articles to show on page
     * @param page [Int] Page index
     * @return [ArticleListApi]
     */
    suspend fun getEntertainment(pageSize:Int, page: Int) : ArticleListApi
    /**
     * Get Top Headlines in Sports
     * @param pageSize [Int] Amount of articles to show on page
     * @param page [Int] Page index
     * @return [ArticleListApi]
     */
    suspend fun getSports(pageSize:Int, page: Int) : ArticleListApi
}