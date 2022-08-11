package com.isrna.newsapp.feature_article.di

import com.isrna.newsapp.core.data.local.database.NewsAppDatabase
import com.isrna.newsapp.feature_article.data.repository.ArticleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger-Hilt module which contains singletons for [ArticleActivity]
 */
@Module
@InstallIn(SingletonComponent::class)
object ArticleModule {
    @Provides
    @Singleton
    fun provideArticleRepository(
        database: NewsAppDatabase
    ): ArticleRepository {
        return ArticleRepository(database.articleDao, database.searchArticleDao)
    }
}