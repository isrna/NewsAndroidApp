package com.isrna.newsapp.core.data.remote

import com.isrna.newsapp.core.data.local.database.entities.ArticleEntity
import com.isrna.newsapp.core.data.local.database.entities.SearchArticleEntity
import com.isrna.newsapp.feature_article_list.utility.ArticleListCategory

/**
 * This class is used to hold the parsed json data from the API
 * @param author String?
 * @param content: String?
 * @param description: String?
 * @param publishedAt: String?
 * @param sourceApi: [SourceApi]?
 * @param title: String?
 * @param url: String?
 * @param urlToImage: String?
 */
data class ArticleApi(
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val sourceApi: SourceApi?,
    val title: String?,
    val url: String?,
    val urlToImage: String?
)
{
    fun toArticleEntity(articleListCategory: ArticleListCategory): ArticleEntity {
        return ArticleEntity(
            id = 0,
            author = author,
            content = content,
            description = description,
            publishedAt = publishedAt,
            title = title,
            url = url,
            urlToImage = urlToImage,
            articleListCategory = articleListCategory
        )
    }

    fun toSearchArticleEntity(keyword: String) : SearchArticleEntity {
        return SearchArticleEntity(
            id = 0,
            author = author,
            content = content,
            description = description,
            publishedAt = publishedAt,
            title = title,
            url = url,
            urlToImage = urlToImage,
            articleListCategory = ArticleListCategory.None,
            keyword = keyword
        )
    }
}