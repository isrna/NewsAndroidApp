package com.isrna.newsapp.feature_search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isrna.newsapp.feature_search.data.repository.SearchRepository
import com.isrna.newsapp.core.ErrorResponses
import com.isrna.newsapp.core.ResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * This ViewModel is used to manage and display search results
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel()  {
    private val _searchViewModelUiEvent = MutableSharedFlow<SearchUIEvent>()
    val searchViewModelUiEvent = _searchViewModelUiEvent.asSharedFlow()

    private var searchKeyword: String = ""
    private var isNewSearch: Boolean = false
    private val pageSize: Int = 20
    private var pageIndex: Int = 1

    /**
     * This function stores the [keyword] and runs a search functions from the [SearchRepository] in
     * [viewModelScope].
     * After it gets the data from the [SearchRepository] it emits an [SearchUIEvent]
     */
    fun searchByKeyword(keyword: String) {
        isNewSearch = searchKeyword != keyword
        searchKeyword = keyword

        viewModelScope.launch {
            searchRepository.searchByKeyword(keyword, pageSize, pageIndex++).collectLatest { response ->
                when(response){
                    is ResponseState.Success -> {
                        response.data?.let {
                            if(isNewSearch)
                                _searchViewModelUiEvent.emit(SearchUIEvent.NewSearchArticleListAdapter(it))
                            else
                                _searchViewModelUiEvent.emit(SearchUIEvent.UpdateArticleListAdapter(it))
                        }
                    }
                    is ResponseState.Error -> {
                        _searchViewModelUiEvent.emit(SearchUIEvent.Error(ErrorResponses.TryAgainError))
                    }
                    is ResponseState.Loading -> {
                        _searchViewModelUiEvent.emit(SearchUIEvent.Loading)
                    }
                    is ResponseState.Unknown -> {
                        _searchViewModelUiEvent.emit(SearchUIEvent.Error(ErrorResponses.UnknownError))
                    }
                }
            }
        }
    }

    /**
     * This function runs the search functions again from the [SearchRepository] in order to
     * retrieve the next page of results.
     * After it gets the data from the [SearchRepository] it emits an [SearchUIEvent]
     */
    fun loadMoreSearchResults() {
        searchByKeyword(searchKeyword)
    }

    /**
     * This function retrieves saved search keywords from [SearchRepository].
     * After it gets the data from the [SearchRepository] it emits an [SearchUIEvent]
     */
    fun loadPastSearches() {
        viewModelScope.launch {
            searchRepository.loadPastSearches().collectLatest { response ->
                when(response){
                    is ResponseState.Success -> {
                        response.data?.let {
                            _searchViewModelUiEvent.emit(SearchUIEvent.UpdateSearchHistory(it))
                        }
                    }
                    is ResponseState.Error -> {
                        _searchViewModelUiEvent.emit(SearchUIEvent.Error(ErrorResponses.TryAgainError))
                    }
                    is ResponseState.Loading -> {
                        _searchViewModelUiEvent.emit(SearchUIEvent.Loading)
                    }
                    is ResponseState.Unknown -> {
                        _searchViewModelUiEvent.emit(SearchUIEvent.Error(ErrorResponses.UnknownError))
                    }
                }
            }
        }
    }

    /**
     * Call this function when the search is completed/closed view by the user.
     * It resets variables inside the [SearchViewModel]
     */
    fun searchClosed() {
        searchKeyword = ""
        pageIndex = 1
        viewModelScope.launch { _searchViewModelUiEvent.emit(SearchUIEvent.SearchClosed) }
    }

    /**
     * This function triggers the saving process for a keyword on [SearchRepository]
     * After it completes it emits an [SearchUIEvent.RefreshSavedSearches]
     */
    fun saveSearchByKeyword(keyword: String) {
        viewModelScope.launch {searchRepository.saveSearchByKeyword(keyword).collectLatest { _searchViewModelUiEvent.emit(it) } }
    }

    /**
     * This function triggers the deleting process for a keyword on [SearchRepository]
     * After it completes it emits an [SearchUIEvent.RefreshSavedSearches]
     */
    fun deleteSearchKeyword(keyword: String) {
        viewModelScope.launch {searchRepository.deleteSearchKeyword(keyword).collectLatest { _searchViewModelUiEvent.emit(it) } }
    }
}