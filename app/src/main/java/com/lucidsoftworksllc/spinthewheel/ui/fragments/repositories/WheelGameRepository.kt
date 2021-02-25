package com.lucidsoftworksllc.spinthewheel.ui.fragments.repositories

import com.lucidsoftworksllc.spinthewheel.base.BaseRepository
import com.lucidsoftworksllc.spinthewheel.ui.fragments.repositories.api.WheelGameAPI
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class WheelGameRepository (
    private val api: WheelGameAPI,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseRepository(dispatcher) {

    suspend fun getWheelInfo() = safeApiCall {
        api.getWheelInfo()
    }

}
