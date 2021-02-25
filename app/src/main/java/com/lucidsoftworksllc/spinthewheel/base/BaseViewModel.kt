package com.lucidsoftworksllc.spinthewheel.base

import androidx.lifecycle.ViewModel
import com.lucidsoftworksllc.taxidi.utils.SingleLiveEvent

/**
 * Base class for View Models to declare the common LiveData objects in one place
 */
abstract class BaseViewModel(
        private val repository: BaseRepository
) : ViewModel() {

    val showToast: SingleLiveEvent<String> = SingleLiveEvent()

}