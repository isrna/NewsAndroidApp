package com.isrna.newsapp.core.di

import android.app.Application
import androidx.room.Room
import com.isrna.newsapp.core.data.local.database.NewsAppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger-Hilt module which contains global singletons
 */
@Module
@InstallIn(SingletonComponent::class)
object GlobalModule {
    @Provides
    @Singleton
    fun provideNewsAppDatabase(app: Application): NewsAppDatabase {
        return Room.databaseBuilder(
            app, NewsAppDatabase::class.java, "news_app_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}