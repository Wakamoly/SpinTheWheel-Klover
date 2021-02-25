package com.lucidsoftworksllc.spinthewheel.util

import android.view.View
import androidx.databinding.BindingAdapter
import com.lucidsoftworksllc.spinthewheel.models.WheelSpinnerResponseModel
import com.lucidsoftworksllc.spinthewheel.util.Extensions.fadeIn
import com.lucidsoftworksllc.spinthewheel.util.Extensions.fadeOut
import com.lucidsoftworksllc.spinthewheel.wheel_view.GameWheelView


object BindingAdapters {

    /**
     * Use this binding adapter to show and hide the views using boolean variables
     */
    @BindingAdapter("app:fadeVisible")
    @JvmStatic
    fun setFadeVisible(view: View, visible: Boolean? = true) {
        if (view.tag == null) {
            view.tag = true
            view.visibility = if (visible == true) View.VISIBLE else View.GONE
        } else {
            view.animate().cancel()
            if (visible == true) {
                if (view.visibility == View.GONE)
                    view.fadeIn()
            } else {
                if (view.visibility == View.VISIBLE)
                    view.fadeOut()
            }
        }
    }

    /**
     * Use this binding adapter to send data to the view
     */
    @BindingAdapter("app:wheelGameData")
    @JvmStatic
    fun setWheelGameData(view: GameWheelView, data: WheelSpinnerResponseModel?) {
        data?.let {
            view.setData(data)
        }
    }

}