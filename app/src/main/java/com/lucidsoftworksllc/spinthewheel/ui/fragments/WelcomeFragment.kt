package com.lucidsoftworksllc.spinthewheel.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lucidsoftworksllc.spinthewheel.R
import com.lucidsoftworksllc.spinthewheel.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {

    private lateinit var binding: FragmentWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        private const val TAG = "WelcomeFragment"
    }

}