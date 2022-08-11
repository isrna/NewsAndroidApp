package com.isrna.newsapp.feature_article.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isrna.newsapp.core.ErrorResponses
import com.isrna.newsapp.feature_article.data.repository.ArticleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val articleRepository: ArticleRepository,
) : ViewModel(){
    var articleId: Int = 1
    var searchBased: Boolean = false

    private val _articleViewModelUiEvent = MutableSharedFlow<ArticleUIEvent>()
    val articleViewModelUiEvent = _articleViewModelUiEvent.asSharedFlow()

    fun getArticle() {
        viewModelScope.launch {
            articleRepository.getArticle(articleId, searchBased).collectLatest {
                if(it.data == null) {
                    _articleViewModelUiEvent.emit(ArticleUIEvent.Error(ErrorResponses.NothingFound))
                }
                else
                    _articleViewModelUiEvent.emit(ArticleUIEvent.UpdateData(it.data))
            }
        }
    }
}