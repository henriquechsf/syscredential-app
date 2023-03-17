package com.github.henriquechsf.syscredentialapp.ui.base

sealed class ResultState<T>(
    val data: T? = null,
    val message: String? = null,
) {
    class Success<T>(data: T) : ResultState<T>(data)
    class Error<T>(message: String, data: T? = null) : ResultState<T>(data, message)
    class Loading<T> : ResultState<T>()
    class Empty<T> : ResultState<T>()
}