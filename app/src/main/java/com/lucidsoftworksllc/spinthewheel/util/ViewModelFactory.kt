package com.lucidsoftworksllc.spinthewheel.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lucidsoftworksllc.spinthewheel.base.BaseRepository
import com.lucidsoftworksllc.spinthewheel.ui.fragments.repositories.WheelGameRepository
import com.lucidsoftworksllc.spinthewheel.ui.fragments.view_models.WheelGameViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val repository: BaseRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(WheelGameViewModel::class.java) -> WheelGameViewModel(repository as WheelGameRepository) as T
            else -> throw IllegalArgumentException("ViewModel Class Not Found")
        }
    }
}