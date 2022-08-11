package com.isrna.newsapp.feature_article_list.di

import android.content.Context
import android.net.ConnectivityManager
import com.isrna.newsapp.BuildConfig
import com.isrna.newsapp.core.data.local.database.NewsAppDatabase
import com.isrna.newsapp.feature_article_list.data.remote.datasources.ArticleListRemoteDataSource
import com.isrna.newsapp.core.data.remote.api.NewsApiOrgInterface
import com.isrna.newsapp.feature_article_list.data.remote.interfaces.ArticleListRemoteDataSourceInterface
import com.isrna.newsapp.feature_article_list.data.repository.ArticleListRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Dagger-Hilt module which contains singletons for [ArticleListActivity]
 */
@Module
@InstallIn(SingletonComponent::class)
object ArticleListModule {
    @Provides
    @Singleton
    fun provideNewsApiArticlesInterface(): NewsApiOrgInterface {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApiOrgInterface::class.java)
    }

    @Provides
    @Singleton
    fun provideArticleListRepository(
        remoteDataSource: ArticleListRemoteDataSourceInterface,
        database: NewsAppDatabase,
        connectivityManager: ConnectivityManager
    ): ArticleListRepository {
        return ArticleListRepository(remoteDataSource, database.articleDao, connectivityManager)
    }

    @Provides
    @Singleton
    fun provideArticleListRemoteDataSourceInterface(
        newsApiOrgInterface: NewsApiOrgInterface
    ): ArticleListRemoteDataSourceInterface {
        return ArticleListRemoteDataSource(newsApiOrgInterface)
    }

    @Singleton
    @Provides
    fun providesNetworkConnectivityHelper(@ApplicationContext context: Context): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
}