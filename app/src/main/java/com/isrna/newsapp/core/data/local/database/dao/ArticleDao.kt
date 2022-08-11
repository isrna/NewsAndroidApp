package com.isrna.newsapp.core.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.isrna.newsapp.core.data.local.database.entities.ArticleEntity
import com.isrna.newsapp.feature_article_list.utility.ArticleListCategory

/**
 * Database calls for Articles table
 */
@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<ArticleEntity>)

    @Query("DELETE FROM articleentity WHERE articlelistcategory = :articleListCategory")
    suspend fun deleteAllArticles(articleListCategory: ArticleListCategory)

    @Query("SELECT * FROM articleentity WHERE articlelistcategory = :articleListCategory ORDER BY publishedAt DESC LIMIT :pageSize OFFSET :pageIndex ")
    suspend fun getArticlesPage(articleListCategory: ArticleListCategory, pageSize: Int, pageIndex: Int): List<ArticleEntity>

    @Query("SELECT * FROM articleentity WHERE id = :articleId")
    suspend fun getArticle(articleId: Int): ArticleEntity
}