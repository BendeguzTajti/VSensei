package com.example.vsensei.view.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.vsensei.R
import com.example.vsensei.databinding.FragmentPracticeResultBinding
import com.google.android.material.transition.MaterialSharedAxis

class PracticeResultFragment : Fragment() {

    private var _binding: FragmentPracticeResultBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<PracticeResultFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPracticeResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.homeButton.setOnClickListener {
            findNavController().navigateUp()
        }
        val practicePercent = args.practicePercent
        binding.percentLabel.text = getString(R.string.practice_percent, practicePercent)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            binding.correctGuessPercent.setProgress(practicePercent, true)
        } else {
            binding.correctGuessPercent.progress = practicePercent
        }
        if (practicePercent >= 60) {
            binding.practiceResultTitle.text = getString(R.string.good_job)
        } else {
            binding.practiceResultTitle.text = getString(R.string.better_luck_next_time)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}