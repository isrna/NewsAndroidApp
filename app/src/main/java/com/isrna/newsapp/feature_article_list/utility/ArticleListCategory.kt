package com.isrna.newsapp.feature_article_list.utility

import com.isrna.newsapp.R

/**
 * Enum which contains Article Categories
 */
enum class ArticleListCategory {
    TopHeadLines,
    Business,
    Entertainment,
    Sports,
    None
}

/**
 * Titles for the Tabs, this is located in [ArticleListCategory].kt files
 */
val TAB_TITLES: Array<Int> = arrayOf(
    R.string.top_headlines,
    R.string.business,
    R.string.entertainment,
    R.string.sports
)
/**
 * Icons for the Tabs, this is located in [ArticleListCategory].kt files
 */
val TAB_ICONS: Array<Int> = arrayOf(
    R.drawable.top_headlines_icon,
    R.drawable.business_icon,
    R.drawable.entertainment_icon,
    R.drawable.sports_icon
)