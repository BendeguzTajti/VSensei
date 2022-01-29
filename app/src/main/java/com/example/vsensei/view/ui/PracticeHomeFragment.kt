package com.example.vsensei.view.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.vsensei.R
import com.example.vsensei.data.PracticeType
import com.example.vsensei.data.WordGroupWithWords
import com.example.vsensei.databinding.FragmentPracticeHomeBinding
import com.example.vsensei.viewmodel.WordViewModel
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class PracticeHomeFragment : Fragment() {

    private var _binding: FragmentPracticeHomeBinding? = null
    private val binding get() = _binding!!

    private val wordViewModel: WordViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPracticeHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.doOnPreDraw {
            exitTransition = null
            reenterTransition = null
        }
        wordViewModel.wordGroupsWithEnoughWords.observe(viewLifecycleOwner, { wordGroupsWithWords ->
            binding.emptyGroupsDisplay.isVisible = wordGroupsWithWords.isEmpty()
            binding.practiceDisplay.isVisible = wordGroupsWithWords.isNotEmpty()
            binding.guessTheWordButton.setOnClickListener {
                if (wordGroupsWithWords.size > 1) {
                    navigateToGroupSelectorDialog(PracticeType.GUESS_THE_WORD)
                } else {
                    navigateToPracticeFragment(wordGroupsWithWords.first(), PracticeType.GUESS_THE_WORD)
                }
            }
            binding.guessTheMeaningButton.setOnClickListener {
                if (wordGroupsWithWords.size > 1) {
                    navigateToGroupSelectorDialog(PracticeType.GUESS_THE_MEANING)
                } else {
                    navigateToPracticeFragment(wordGroupsWithWords.first(), PracticeType.GUESS_THE_MEANING)
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun navigateToGroupSelectorDialog(practiceType: PracticeType) {
        if (findNavController().currentDestination?.id == R.id.practiceHomeFragment) {
            val action = PracticeHomeFragmentDirections.actionPracticeHomeFragmentToGroupSelectFragment(
                practiceType
            )
            reenterTransition = MaterialFadeThrough()
            findNavController().navigate(action)
        }
    }

    private fun navigateToPracticeFragment(wordGroupWithWords: WordGroupWithWords, practiceType: PracticeType) {
        wordGroupWithWords.words.shuffle()
        val action = PracticeHomeFragmentDirections.actionPracticeHomeFragmentToPracticeFragment(
            practiceType,
            wordGroupWithWords,
            getString(practiceType.labelResId)
        )
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
        }
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
        }
        findNavController().navigate(action)
    }
}