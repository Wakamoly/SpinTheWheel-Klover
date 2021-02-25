package com.lucidsoftworksllc.spinthewheel.ui.fragments.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lucidsoftworksllc.spinthewheel.base.BaseViewModel
import com.lucidsoftworksllc.spinthewheel.models.WheelSpinnerResponseModel
import com.lucidsoftworksllc.spinthewheel.ui.fragments.repositories.WheelGameRepository
import com.lucidsoftworksllc.spinthewheel.util.DataState
import kotlinx.coroutines.launch

class WheelGameViewModel (
    private val repository: WheelGameRepository
) : BaseViewModel(repository) {

    // Encapsulation
    private val _wheelInfo = MutableLiveData<WheelSpinnerResponseModel>()
    val wheelInfo : LiveData<WheelSpinnerResponseModel>
        get() = _wheelInfo

    init {
        getWheelInfo()
    }

    private fun getWheelInfo() {
        viewModelScope.launch {
            when (val result = repository.getWheelInfo()) {
                is DataState.Error -> {
                    showToast.value = result.message
                }
                is DataState.Success -> {
                    showToast.value = result.data[0].displayText
                    _wheelInfo.value = result.data
                }
                else -> {} // Do nothing
            }
        }
    }

}