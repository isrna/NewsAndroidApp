package com.isrna.newsapp.core.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.isrna.newsapp.core.data.local.database.entities.SearchArticleEntity
import com.isrna.newsapp.core.data.local.database.entities.SearchKeywordEntity

/**
 * Database calls for Search keyword table
 */
@Dao
interface SearchKeywordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchKeyword(keyword: SearchKeywordEntity)

    @Query("DELETE FROM searchkeywordentity")
    suspend fun deleteAllSearchKeywords()

    @Query("DELETE FROM searchkeywordentity WHERE keyword = :keyword")
    suspend fun deleteSearchKeyword(keyword: String)

    @Query("SELECT * FROM searchkeywordentity ORDER BY id DESC")
    suspend fun getAllSearchKeywords(): List<SearchKeywordEntity>

    @Query("SELECT * FROM searcharticleentity WHERE keyword = :keyword ORDER BY publishedAt, id LIMIT 1")
    suspend fun getLastKeywordArticle(keyword: String): SearchArticleEntity?

    @Query("DELETE FROM searcharticleentity WHERE keyword = :keyword")
    suspend fun deleteAllSearchArticlesByKeywords(keyword: String)
}