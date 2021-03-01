package com.lucidsoftworksllc.spinthewheel.ui.fragments.repositories.api

import com.lucidsoftworksllc.spinthewheel.models.WheelSpinnerResponseModel
import retrofit2.http.GET

interface WheelGameAPI {

    @GET("539dc092-8367-414a-8892-ed3b2d666dbe.php")
    suspend fun getWheelInfo(): WheelSpinnerResponseModel

}
