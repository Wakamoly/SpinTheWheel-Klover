package com.lucidsoftworksllc.spinthewheel.util

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.lucidsoftworksllc.spinthewheel.base.BaseViewModel

object Extensions {

    fun Context.toastShort(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    fun Context.toastLong(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    fun View.visible(isVisible: Boolean){
        visibility = if(isVisible) View.VISIBLE else View.GONE
    }

    fun Fragment.startBaseObservables(viewModel: BaseViewModel){
        viewModel.showToast.observe(this, {
            this.activity?.toastShort(it)
        })
    }

}

