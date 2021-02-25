package com.lucidsoftworksllc.spinthewheel.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.lucidsoftworksllc.spinthewheel.databinding.WheelGameFragmentBinding
import com.lucidsoftworksllc.spinthewheel.ui.fragments.repositories.WheelGameRepository
import com.lucidsoftworksllc.spinthewheel.ui.fragments.repositories.api.WheelGameAPI
import com.lucidsoftworksllc.spinthewheel.ui.fragments.view_models.WheelGameViewModel
import com.lucidsoftworksllc.spinthewheel.util.Extensions.startBaseObservables
import com.lucidsoftworksllc.spinthewheel.util.RemoteDataSource
import com.lucidsoftworksllc.spinthewheel.util.ViewModelFactory

class WheelGameFragment : Fragment() {

    companion object {
        private const val TAG = "WheelGameFragment"
    }

    private lateinit var binding: WheelGameFragmentBinding
    private val viewModel: WheelGameViewModel by viewModels {
        ViewModelFactory(WheelGameRepository(RemoteDataSource().buildApi(WheelGameAPI::class.java)))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = WheelGameFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        startBaseObservables(viewModel)
        return binding.root
    }

}