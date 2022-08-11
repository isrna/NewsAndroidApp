package com.isrna.newsapp.core.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.isrna.newsapp.core.data.local.database.entities.SearchArticleEntity

/**
 * Database calls for Searched articles table
 */
@Dao
interface SearchArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<SearchArticleEntity>)

    @Query("DELETE FROM searcharticleentity WHERE keyword = :keyword ")
    suspend fun deleteAllArticles(keyword: String)

    @Query("SELECT * FROM searcharticleentity WHERE keyword = :keyword ORDER BY publishedAt DESC LIMIT :pageSize OFFSET :pageIndex ")
    suspend fun getArticlesPage(keyword: String, pageSize: Int, pageIndex: Int): List<SearchArticleEntity>

    @Query("SELECT * FROM searcharticleentity WHERE id = :articleId")
    suspend fun getArticle(articleId: Int): SearchArticleEntity
}