package com.lucidsoftworksllc.spinthewheel.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.lucidsoftworksllc.spinthewheel.R
import com.lucidsoftworksllc.spinthewheel.databinding.WheelGameFragmentBinding
import com.lucidsoftworksllc.spinthewheel.models.WheelSpinnerResponseModel
import com.lucidsoftworksllc.spinthewheel.ui.fragments.repositories.WheelGameRepository
import com.lucidsoftworksllc.spinthewheel.ui.fragments.repositories.api.WheelGameAPI
import com.lucidsoftworksllc.spinthewheel.ui.fragments.view_models.WheelGameViewModel
import com.lucidsoftworksllc.spinthewheel.util.Extensions.startBaseObservables
import com.lucidsoftworksllc.spinthewheel.util.RemoteDataSource
import com.lucidsoftworksllc.spinthewheel.util.ViewModelFactory
import com.lucidsoftworksllc.spinthewheel.wheel_view.GameWheelView

class WheelGameFragment : Fragment() {

    companion object {
        private const val TAG = "WheelGameFragment"
    }

    private lateinit var binding: WheelGameFragmentBinding
    private val viewModel: WheelGameViewModel by viewModels {
        ViewModelFactory(WheelGameRepository(RemoteDataSource().buildApi(WheelGameAPI::class.java)))
    }

    override fun onStop() {
        super.onStop()
        binding.gameWheelView.stop()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = WheelGameFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        startBaseObservables(viewModel)
        initListeners()
        return binding.root
    }

    private fun initListeners() {
        binding.spinThatDangOlWheelButton.setOnClickListener {
            binding.gameWheelView.spin()
        }
        binding.gameWheelView.setOnTickListener(object : GameWheelView.OnTickerTickListener {
            override fun onTick(v: View) {
                tickTicker()
            }

            override fun onFinished(
                v: View,
                data: WheelSpinnerResponseModel.WheelSpinnerResponseModelItem
            ) {
                val resultString = getString(R.string.you_won_yay) + " ${data.displayText} ${data.currency}!"
                viewModel.showToast.value = resultString
            }

            override fun onError(e: String) {
                viewModel.showToast.value = e
            }

        })

    }

    private fun tickTicker() {
        val rotateView = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_ticker)
        binding.stopperImage.startAnimation(rotateView)
    }

}