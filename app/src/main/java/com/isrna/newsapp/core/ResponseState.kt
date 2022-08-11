package com.isrna.newsapp.core

/**
 * This class is used for throwing events for UI updates
 * @param data Any?
 * @param errorResponse [ErrorResponses]?
 */
sealed class ResponseState<T>(val data: T? = null, val errorResponse: ErrorResponses? = null) {
    class Loading<T>(data: T? = null): ResponseState<T>(data)
    class Success<T>(data: T?): ResponseState<T>(data)
    class Error<T>(data: T? = null, errorResponse: ErrorResponses): ResponseState<T>(data, errorResponse)
    class Unknown<T>(data: T? = null): ResponseState<T>(data)
}

enum class ErrorResponses {
    TryAgainError,
    DeleteError,
    UnknownError,
    NothingFound,
    NoInternet,
    ServerProblem,
    NoMoreResults,
    UnknownCategory,
    NoMoreDataFromApi,
    FailedToOpenArticle
}