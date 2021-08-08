package com.example.vsensei.view.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.vsensei.R
import com.example.vsensei.databinding.FragmentPracticeResultBinding
import com.example.vsensei.viewmodel.PracticeViewModel
import com.google.android.material.transition.MaterialSharedAxis
import org.koin.android.viewmodel.ext.android.sharedViewModel

class PracticeResultFragment : Fragment() {

    private var _binding: FragmentPracticeResultBinding? = null
    private val binding get() = _binding!!

    private val practiceViewModel: PracticeViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
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
            requireActivity().finishAfterTransition()
        }
        practiceViewModel.currentPracticeSummary().observe(viewLifecycleOwner, { practiceSummary ->
            val practicePercent = practiceSummary.getPercent()
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
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}