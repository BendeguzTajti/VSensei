package com.example.vsensei.view.ui

import android.content.pm.ActivityInfo
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.example.vsensei.databinding.FragmentSplashScreenBinding

class SplashScreenFragment : Fragment() {

    private var _binding: FragmentSplashScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val appIcon = binding.appIcon.drawable
        if (appIcon is AnimatedVectorDrawableCompat) {
            appIcon.start()
        } else if (appIcon is AnimatedVectorDrawable) {
            appIcon.start()
        }
        Handler(Looper.getMainLooper()).postDelayed({
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            val action = SplashScreenFragmentDirections.actionSplashScreenFragmentToPracticeHomeFragment()
            findNavController().navigate(action)
        }, 1400)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}