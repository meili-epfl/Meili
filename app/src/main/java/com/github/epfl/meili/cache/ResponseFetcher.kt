package com.github.epfl.meili.cache

interface ResponseFetcher<T> {
    fun fetchResponse(arg: Any, onSuccess: ((T)->Unit)?, onError: ((Error)->Unit))
}