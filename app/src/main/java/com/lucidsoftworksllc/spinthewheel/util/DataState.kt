package com.lucidsoftworksllc.spinthewheel.util

/**
 * A sealed class that encapsulates successful outcome with a value of type [T]
 * or a failure with message and statusCode
 */
sealed class DataState <out T : Any> {
    data class Success<out T : Any>(val data: T) : DataState<T>()
    data class Error(var message: String?, val statusCode: Int? = null) :
        DataState<Nothing>()
    object Loading : DataState<Nothing>()
}