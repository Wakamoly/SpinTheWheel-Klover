package com.lucidsoftworksllc.spinthewheel.models

class WheelSpinnerResponseModel : ArrayList<WheelSpinnerResponseModel.WheelSpinnerResponseModelItem>(){
    data class WheelSpinnerResponseModelItem(
        val currency: String?,
        val displayText: String?,
        val id: String?,
        val value: Int?
    )
}