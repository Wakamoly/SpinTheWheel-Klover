package com.lucidsoftworksllc.spinthewheel.base

import com.lucidsoftworksllc.spinthewheel.util.DataState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException

abstract class BaseRepository(
    private val dispatcher: CoroutineDispatcher
)  {

    suspend fun <T : Any> safeApiCall(
            apiCall: suspend () -> T
    ) : DataState<T> {
        return withContext(dispatcher){
            try {
                DataState.Success(apiCall.invoke())
            }catch(throwable: Throwable){
                when (throwable) {
                    is HttpException -> {
                        DataState.Error(throwable.message, throwable.code())
                    }
                    else -> {
                        DataState.Error(throwable.message, null)
                    }
                }
            }
        }
    }

}
