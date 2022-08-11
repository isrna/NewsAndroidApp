package com.isrna.newsapp.core.data.remote

/**
 * This class is used to hold the parsed json data from the API
 * @param articles List<[ArticlesApi]>
 * @param status String
 * @param totalResults Int
 */
data class ArticleListApi(
    val articles: List<ArticleApi>,
    val status: String,
    val totalResults: Int
)