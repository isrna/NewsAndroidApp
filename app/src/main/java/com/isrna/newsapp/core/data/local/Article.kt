package com.isrna.newsapp.core.data.local

import com.isrna.newsapp.feature_article_list.utility.ArticleListCategory

/**
 * Article object
 * @param id Int
 * @param author String?
 * @param content String?
 * @param description String?
 * @param publishedAt String?
 * @param title String?
 * @param url String?
 * @param urlToImage String?
 * @param articleListCategory ArticleListCategory
 */
data class Article(
    val id: Int,
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val title: String?,
    val url: String?,
    val urlToImage: String?,
    val articleListCategory: ArticleListCategory
) {
    override fun toString(): String {
        return "$title - $description"
    }
}
