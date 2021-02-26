package com.lucidsoftworksllc.spinthewheel.util

import androidx.databinding.BindingAdapter
import com.lucidsoftworksllc.spinthewheel.models.WheelSpinnerResponseModel
import com.lucidsoftworksllc.spinthewheel.wheel_view.GameWheelView

object BindingAdapters {

    /**
     * Use this binding adapter to send data to the view
     * TODO Occasionally throws a compiler issue, re-running the build seems to combat it?
     */
    @BindingAdapter("app:wheelGameData")
    @JvmStatic
    fun setWheelGameData(view: GameWheelView, data: WheelSpinnerResponseModel?) {
        data?.let {
            view.setData(data)
        }
    }

}