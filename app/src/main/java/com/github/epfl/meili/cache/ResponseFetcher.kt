package com.github.epfl.meili.cache

interface ResponseFetcher<T> {
    /**
     * Gets info from the API using arg and calling back either onSuccess or onError
     */
    fun fetchResponse(arg: Any?, onSuccess: ((T)->Unit)?, onError: ((Error)->Unit))
}