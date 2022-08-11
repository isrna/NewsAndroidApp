package com.isrna.newsapp.feature_article_list.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isrna.newsapp.core.ErrorResponses
import com.isrna.newsapp.feature_article_list.data.repository.ArticleListRepository
import com.isrna.newsapp.feature_article_list.utility.ArticleListCategory
import com.isrna.newsapp.core.ResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * [ViewModel] used for listing articles
 * @param articleListRepository [ArticleListRepository]
 */
@HiltViewModel
class ArticleListViewModel @Inject constructor(
    private val articleListRepository: ArticleListRepository,
) : ViewModel() {
    private val _articleListViewModelUiEvent = MutableSharedFlow<ArticleListUIEvent>()
    val articleListViewModelUiEvent = _articleListViewModelUiEvent.asSharedFlow()
    private val pageSize = 20

    /**
     * This function is loading articles from [ArticleListRepository].
     * @param articleListCategory [ArticleListCategory]
     * @param page [Int] Page index
     * @param refresh [Boolean] whether to refresh the whole list
     */
    fun loadArticles(articleListCategory: ArticleListCategory, page: Int, refresh: Boolean = false) {
        viewModelScope.launch {
            articleListRepository.loadArticles(articleListCategory, page, pageSize).collectLatest {
                when(it) {
                    is ResponseState.Success -> {
                        it.data?.let { articles ->
                            if(refresh)
                                _articleListViewModelUiEvent.emit(ArticleListUIEvent.RefreshArticleListAdapter(articles, articles[0].articleListCategory))
                            else
                                _articleListViewModelUiEvent.emit(ArticleListUIEvent.UpdateArticleListAdapter(articles, articles[0].articleListCategory))
                        }
                    }
                    is ResponseState.Error -> {
                        it.errorResponse?.let { errorResponse ->
                            _articleListViewModelUiEvent.emit(ArticleListUIEvent.Error(errorResponse))
                        }
                    }
                    is ResponseState.Loading -> {
                        _articleListViewModelUiEvent.emit(ArticleListUIEvent.Loading)
                    }
                    is ResponseState.Unknown -> _articleListViewModelUiEvent.emit(ArticleListUIEvent.Error(ErrorResponses.UnknownError))
                }
            }
        }
    }
}