package com.isrna.newsapp.feature_article_list.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.isrna.newsapp.feature_article_list.ui.ArticleListFragment

/**
 * A [FragmentStateAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle, private val itemCount: Int) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun createFragment(position: Int): Fragment {
        return ArticleListFragment.newInstance(position)
    }
    override fun getItemCount(): Int = itemCount
}