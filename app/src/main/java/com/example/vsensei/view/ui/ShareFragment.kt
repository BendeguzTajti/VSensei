package com.example.vsensei.view.ui

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.example.vsensei.R
import com.example.vsensei.databinding.FragmentShareBinding
import com.example.vsensei.util.Resource
import com.example.vsensei.viewmodel.GroupShareViewModel
import com.google.android.material.transition.MaterialContainerTransform
import org.koin.androidx.viewmodel.ext.android.viewModel

class ShareFragment : Fragment() {

    private var _binding: FragmentShareBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<ShareFragmentArgs>()

    private val groupShareViewModel: GroupShareViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
            scrimColor = Color.TRANSPARENT
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val qrCodeSize = resources.getDimensionPixelSize(R.dimen.qr_code_size)
        groupShareViewModel.qrCode(args.wordGroupWithWords, qrCodeSize)
            .observe(viewLifecycleOwner) { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        binding.qrCodeLoading.isVisible = true
                    }
                    is Resource.Success -> {
                        binding.qrCodeLoading.isVisible = false
                        binding.qrCode.setImageBitmap(resource.data)
                    }
                    is Resource.Error -> {
                        binding.qrCodeLoading.isVisible = false
                    }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}