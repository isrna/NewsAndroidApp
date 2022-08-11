package com.isrna.newsapp.feature_search.di

import android.net.ConnectivityManager
import com.isrna.newsapp.core.data.local.database.NewsAppDatabase
import com.isrna.newsapp.core.data.remote.api.NewsApiOrgInterface
import com.isrna.newsapp.feature_search.data.repository.SearchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger-Hilt module which contains singletons for Search Feature
 */
@Module
@InstallIn(SingletonComponent::class)
object SearchModule {

    @Provides
    @Singleton
    fun provideSearchRepository(
        remoteDataSource: NewsApiOrgInterface,
        database: NewsAppDatabase,
        connectivityManager: ConnectivityManager
    ): SearchRepository {
        return SearchRepository(remoteDataSource, database.searchArticleDao, database.searchKeywordDao, connectivityManager)
    }
}