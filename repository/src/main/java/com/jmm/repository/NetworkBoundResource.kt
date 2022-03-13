package com.jmm.repository

import kotlinx.coroutines.flow.*

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true }
) = flow {
    val data = query().first()

    val flow = if (shouldFetch(data)) {
        emit(IResource.Loading(data))

        try {
            saveFetchResult(fetch())
            query().map { IResource.Success(it) }
        } catch (throwable: Throwable) {
            query().map { IResource.Error(throwable, it) }
        }
    } else {
        query().map { IResource.Success(it) }
    }

    emitAll(flow)
}

sealed class IResource<T>(
    val data: T? = null,
    val error: Throwable? = null
) {
    class Success<T>(data: T) : IResource<T>(data)
    class Loading<T>(data: T? = null) : IResource<T>(data)
    class Error<T>(throwable: Throwable, data: T? = null) : IResource<T>(data, throwable)
}