package com.isrna.newsapp.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.isrna.newsapp.core.data.local.database.dao.ArticleDao
import com.isrna.newsapp.core.data.local.database.dao.SearchArticleDao
import com.isrna.newsapp.core.data.local.database.dao.SearchKeywordDao
import com.isrna.newsapp.core.data.local.database.entities.ArticleEntity
import com.isrna.newsapp.core.data.local.database.entities.SearchArticleEntity
import com.isrna.newsapp.core.data.local.database.entities.SearchKeywordEntity


@Database(
    entities = [ArticleEntity::class, SearchArticleEntity::class, SearchKeywordEntity::class],
    version = 1
)
@TypeConverters(JsonConverters::class)
abstract class NewsAppDatabase: RoomDatabase() {
    abstract val articleDao: ArticleDao
    abstract val searchArticleDao: SearchArticleDao
    abstract val searchKeywordDao: SearchKeywordDao
}