package com.isrna.newsapp.feature_article_list.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.isrna.newsapp.R
import com.isrna.newsapp.core.data.local.Article
import com.isrna.newsapp.databinding.FragmentArticleListBinding
import com.isrna.newsapp.feature_article.ArticleActivity
import com.isrna.newsapp.feature_article_list.ui.adapters.ArticleAdapterInterface
import com.isrna.newsapp.feature_article_list.ui.adapters.ArticleListAdapter
import com.isrna.newsapp.feature_article_list.utility.ArticleListCategory
import com.isrna.newsapp.feature_search.ui.SearchUIEvent
import com.isrna.newsapp.feature_search.ui.SearchViewModel
import com.isrna.newsapp.feature_webview.WebViewActivity
import com.isrna.newsapp.core.ErrorResponses
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

/**
 * Fragment containing a [RecyclerView], which used for displaying a list of [Article]s.
 */
@AndroidEntryPoint
class ArticleListFragment : Fragment(), ArticleAdapterInterface {

    private val articleListViewModel: ArticleListViewModel by viewModels()
    private val searchViewModel: SearchViewModel by activityViewModels()
    private lateinit var articleListCategory: ArticleListCategory
    private lateinit var articleListAdapter: ArticleListAdapter
    private lateinit var binding: FragmentArticleListBinding
    private lateinit var articleRecyclerViewer: RecyclerView
    private lateinit var loadingBar: ConstraintLayout

    //Used for paging of results in RecyclerViewer
    private var isLoading = false
    private var currentPage = 1
    private var isSearchDataVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        articleListCategory = when(arguments?.getInt(ARG_SECTION_NUMBER))
        {
            0 -> ArticleListCategory.TopHeadLines
            1 -> ArticleListCategory.Business
            2 -> ArticleListCategory.Entertainment
            3 -> ArticleListCategory.Sports
            else -> ArticleListCategory.None
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentArticleListBinding.inflate(inflater, container, false)
        val root = binding.root

        if(articleListCategory == ArticleListCategory.None)
            return root

        articleRecyclerViewer = binding.articleRecyclerViewer
        loadingBar = binding.loadingBarInclude.articleListProgressBar

        setupArticleRecyclerViewer()

        setupArticleViewModelListener()
        setupSearchViewModelListener()

        loadArticleList()

        return root
    }

    private fun setupArticleRecyclerViewer() {
        articleListAdapter = ArticleListAdapter(this)
        articleRecyclerViewer.adapter = articleListAdapter
        articleRecyclerViewer.layoutManager = LinearLayoutManager(binding.root.context)
        articleRecyclerViewer.addOnScrollListener(object : ArticleRecyclerViewerOnScrollListener() {
            override fun loadMoreItems() {
                if(isSearchDataVisible)
                    loadMoreSearchResults()
                else
                    loadArticleList()
            }
            override fun isLoading(): Boolean {
                return isLoading
            }
        })
    }

    private fun setupArticleViewModelListener() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            articleListViewModel.articleListViewModelUiEvent.collectLatest { articleListUiEvent ->
                when(articleListUiEvent) {
                    is ArticleListUIEvent.UpdateArticleListAdapter -> {
                        if(articleListCategory == articleListUiEvent.articleListCategory) {
                            articleListAdapter.appendList(articleListUiEvent.data)
                        }
                        loadingComplete()
                    }
                    is ArticleListUIEvent.RefreshArticleListAdapter -> {
                        if(articleListCategory == articleListUiEvent.articleListCategory) {
                            articleListAdapter.submitList(articleListUiEvent.data)
                        }
                        loadingComplete()
                    }
                    is ArticleListUIEvent.Loading -> {
                        loadingStarted()
                    }
                    is ArticleListUIEvent.Error ->
                    {
                        when(articleListUiEvent.errorType) {
                            ErrorResponses.TryAgainError -> Snackbar.make(binding.root, getString(R.string.try_again_error_message), Snackbar.LENGTH_SHORT).show()
                            ErrorResponses.DeleteError -> Snackbar.make(binding.root, getString(R.string.delete_error_message), Snackbar.LENGTH_SHORT).show()
                            ErrorResponses.UnknownError -> Snackbar.make(binding.root, getString(R.string.unknown_error_message), Snackbar.LENGTH_SHORT).show()
                            ErrorResponses.NothingFound -> Snackbar.make(binding.root, getString(R.string.nothing_found_error_message), Snackbar.LENGTH_SHORT).show()
                            ErrorResponses.NoInternet -> Snackbar.make(binding.root, getString(R.string.no_internet_error_message), Snackbar.LENGTH_SHORT).show()
                            ErrorResponses.ServerProblem -> Snackbar.make(binding.root, getString(R.string.server_problem_error_message), Snackbar.LENGTH_SHORT).show()
                            ErrorResponses.NoMoreResults -> Snackbar.make(binding.root, getString(R.string.no_more_results_error), Snackbar.LENGTH_SHORT).show()
                            ErrorResponses.UnknownCategory -> Snackbar.make(binding.root, getString(R.string.unknown_category_error_message), Snackbar.LENGTH_SHORT).show()
                            ErrorResponses.NoMoreDataFromApi -> Snackbar.make(binding.root, getString(R.string.no_more_data_from_api_error_message), Snackbar.LENGTH_SHORT).show()
                            ErrorResponses.FailedToOpenArticle -> Snackbar.make(binding.root, getString(R.string.failed_to_open_article_error_message), Snackbar.LENGTH_SHORT).show()
                        }
                        loadingComplete()
                    }
                }
            }
        }
    }

    private fun setupSearchViewModelListener() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            searchViewModel.searchViewModelUiEvent.collectLatest { event ->
                when(event) {
                    is SearchUIEvent.UpdateArticleListAdapter -> {
                        articleListAdapter.appendList(event.data)
                        isSearchDataVisible = true
                        loadingComplete()
                    }
                    is SearchUIEvent.NewSearchArticleListAdapter -> {
                        articleListAdapter.submitList(event.data)
                        isSearchDataVisible = true
                        loadingComplete()
                    }
                    is SearchUIEvent.Error -> {
                        if(event.errorType == ErrorResponses.UnknownError) {
                            Snackbar.make(binding.root, getString(R.string.unknown_error_message), Snackbar.LENGTH_SHORT).show()
                        }
                        loadingComplete()
                    }
                    is SearchUIEvent.Loading -> {
                        loadingStarted()
                    }
                    is SearchUIEvent.SearchClosed -> {
                        if(isSearchDataVisible)
                            refreshArticleList()

                        isSearchDataVisible = false
                    }
                    SearchUIEvent.RefreshSavedSearches -> {}
                    is SearchUIEvent.UpdateSearchHistory -> {}
                }
            }
        }
    }

    private fun loadArticleList() {
        if(!isLoading) {
            articleListViewModel.loadArticles(articleListCategory, currentPage++)
        }
    }

    private fun refreshArticleList() {
        if(!isLoading) {
            currentPage = 1
            articleListViewModel.loadArticles(articleListCategory, currentPage, true)
        }
    }

    private fun loadMoreSearchResults() {
        searchViewModel.loadMoreSearchResults()
    }

    private fun loadingStarted() {
        isLoading = true
        loadingBar.visibility = View.VISIBLE
    }

    private fun loadingComplete() {
        isLoading = false
        loadingBar.visibility = View.GONE
    }

    override fun onTitleClick(article: Article) {
        if(article.id > 0) {
            if(article.content == null)
            {
                val webViewActivity = Intent(this.context, WebViewActivity::class.java)
                webViewActivity.putExtra("url", article.url)
                startActivity(webViewActivity)
                return
            }

            val articleActivityIntent = Intent(this.context, ArticleActivity::class.java)
            articleActivityIntent.putExtra("articleId", article.id)
            articleActivityIntent.putExtra("search", isSearchDataVisible)
            startActivity(articleActivityIntent)
        }
    }

    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"
        @JvmStatic
        fun newInstance(sectionNumber: Int): ArticleListFragment {
            return ArticleListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}