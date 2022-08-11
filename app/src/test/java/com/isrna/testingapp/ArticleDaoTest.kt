package com.isrna.testingapp

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.isrna.newsapp.core.data.local.database.NewsAppDatabase
import com.isrna.newsapp.core.data.local.database.dao.ArticleDao
import com.isrna.newsapp.core.data.local.database.entities.ArticleEntity
import com.isrna.newsapp.feature_article_list.utility.ArticleListCategory
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ArticleDaoTest {
    private lateinit var db: NewsAppDatabase
    private lateinit var articleDao: ArticleDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, NewsAppDatabase::class.java).build()
        articleDao = db.articleDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertArticlesTest() = runTest {
        val article = ArticleEntity(
                0,
        "author",
        "content",
        "description",
        "publishedAt",
        "title",
        "url",
        "urlImage",
        ArticleListCategory.TopHeadLines)

        val testList: List<ArticleEntity> = listOf(article)

        articleDao.insertArticles(testList)

        val allArticles = articleDao.getArticlesPage(ArticleListCategory.TopHeadLines, 10, 0)

        assertThat(allArticles.count()).isEqualTo(1)
    }

    @Test
    @Throws(Exception::class)
    fun deleteAllArticlesTest() = runTest {
        val article = ArticleEntity(
            0,
            "author",
            "content",
            "description",
            "publishedAt",
            "title",
            "url",
            "urlImage",
            ArticleListCategory.TopHeadLines)

        val testList: List<ArticleEntity> = listOf(article)

        articleDao.insertArticles(testList)

        val allArticles = articleDao.getArticlesPage(ArticleListCategory.TopHeadLines, 10, 0)

        assertThat(allArticles.count()).isEqualTo(1)

        articleDao.deleteAllArticles(ArticleListCategory.TopHeadLines)

        val checkIfDeleted = articleDao.getArticlesPage(ArticleListCategory.TopHeadLines, 10, 0)

        assertThat(checkIfDeleted.count()).isEqualTo(0)
    }

    @Test
    @Throws(Exception::class)
    fun getArticlesPageTest() = runTest {
        val article = ArticleEntity(
            0,
            "author",
            "content",
            "description",
            "publishedAt",
            "title",
            "url",
            "urlImage",
            ArticleListCategory.TopHeadLines)

        val testList: List<ArticleEntity> = listOf(article, article, article, article)

        articleDao.insertArticles(testList)

        val allArticles = articleDao.getArticlesPage(ArticleListCategory.TopHeadLines, 2, 2)

        assertThat(allArticles.count()).isEqualTo(2)
    }

    @Test
    @Throws(Exception::class)
    fun getArticleTest() = runTest {
        val article = ArticleEntity(
            0,
            "author",
            "content",
            "description",
            "publishedAt",
            "title",
            "url",
            "urlImage",
            ArticleListCategory.TopHeadLines)

        val testList: List<ArticleEntity> = listOf(article)

        articleDao.insertArticles(testList)

        val allArticles = articleDao.getArticlesPage(ArticleListCategory.TopHeadLines, 1, 0)

        val getLatestArticle = articleDao.getArticle(allArticles[0].id)


        assertThat(getLatestArticle.title).isEqualTo(article.title)
    }
}