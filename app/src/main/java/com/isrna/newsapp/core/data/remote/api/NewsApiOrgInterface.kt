package com.isrna.newsapp.core.data.remote.api

import com.isrna.newsapp.BuildConfig
import com.isrna.newsapp.core.data.remote.ArticleListApi
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

/**
 * This interface is used to reach out to the Newsapi.org API
 * Authorisation API key is stored at [BuildConfig.API_KEY]
 */
interface NewsApiOrgInterface {
    /**
     * Read Top Headlines from USA, sorted by the publishing date
     * @param pageSize Int How many articles to be displayed per page
     * @param page Int Page index
     * @return [ArticleListApi]
     */
    @Headers("Authorization: ${BuildConfig.API_KEY}")
    @GET("top-headlines?country=us&sortBy=publishedAt")
    suspend fun getTopHeadLines(@Query("pageSize") pageSize: Int, @Query("page") page: Int) : ArticleListApi

    /**
     * Read Business Top Headlines from USA, sorted by the publishing date
     * @param pageSize Int How many articles to be displayed per page
     * @param page Int Page index
     * @return [ArticleListApi]
     */
    @Headers("Authorization: ${BuildConfig.API_KEY}")
    @GET("top-headlines?country=us&category=business&sortBy=publishedAt")
    suspend fun getBusiness(@Query("pageSize") pageSize: Int, @Query("page") page: Int) : ArticleListApi

    /**
     * Read Entertainment Top Headlines from USA, sorted by the publishing date
     * @param pageSize Int How many articles to be displayed per page
     * @param page Int Page index
     * @return [ArticleListApi]
     */
    @Headers("Authorization: ${BuildConfig.API_KEY}")
    @GET("top-headlines?country=us&category=entertainment&sortBy=publishedAt")
    suspend fun getEntertainment(@Query("pageSize") pageSize: Int, @Query("page") page: Int) : ArticleListApi

    /**
     * Read Sports Top Headlines from USA, sorted by the publishing date
     * @param pageSize Int How many articles to be displayed per page
     * @param page Int Page index
     * @return [ArticleListApi]
     */
    @Headers("Authorization: ${BuildConfig.API_KEY}")
    @GET("top-headlines?country=us&category=sports&sortBy=publishedAt")
    suspend fun getSports(@Query("pageSize") pageSize: Int, @Query("page") page: Int) : ArticleListApi

    /**
     * Send a search query. This api does not accept country selection, it only accepts language selector
     * @param keyword String Search Query
     * @param pageSize Int How many articles to be displayed per page
     * @param page Int Page index
     * @return [ArticleListApi]
     */
    @Headers("Authorization: ${BuildConfig.API_KEY}")
    @GET("everything?language=en&sortBy=publishedAt")
    suspend fun searchByKeyword(@Query("q") keyword: String, @Query("pageSize") pageSize: Int, @Query("page") page: Int) : ArticleListApi
}