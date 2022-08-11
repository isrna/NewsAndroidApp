package com.isrna.newsapp.core.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.isrna.newsapp.core.data.local.Article
import com.isrna.newsapp.feature_article_list.utility.ArticleListCategory

@Entity
class SearchArticleEntity(@PrimaryKey(autoGenerate = true) val id: Int,
                          val author: String?,
                          val content: String?,
                          val description: String?,
                          val publishedAt: String?,
                          val title: String?,
                          val url: String?,
                          val urlToImage: String?,
                          val articleListCategory: ArticleListCategory,
                          val keyword: String
) {
    fun toArticle(): Article {
        return Article(
            id = id,
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
}