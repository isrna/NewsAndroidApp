package com.isrna.newsapp.core.services

import com.isrna.newsapp.core.ErrorResponses

/**
 * Event Helper class for PastSearchService
 */
sealed class EventPastSearchService {
    data class NewResults(val articleTitleList: List<Pair<String, String>>) : EventPastSearchService()
    object NoResults : EventPastSearchService()
    data class Error(val errorResponse: ErrorResponses): EventPastSearchService()
}