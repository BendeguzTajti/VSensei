package com.example.vsensei.view.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.vsensei.data.PracticeType
import com.example.vsensei.databinding.FragmentPracticeHomeBinding
import com.example.vsensei.view.contract.BottomNavActivity
import com.example.vsensei.viewmodel.PracticeViewModel
import com.example.vsensei.viewmodel.WordViewModel
import com.google.android.material.transition.MaterialFadeThrough
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class PracticeHomeFragment : Fragment() {

    private var bottomNavActivity: BottomNavActivity? = null
    private var _binding: FragmentPracticeHomeBinding? = null
    private val binding get() = _binding!!

    private val wordViewModel: WordViewModel by sharedViewModel()
    private val practiceViewModel: PracticeViewModel by sharedViewModel()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        bottomNavActivity = requireActivity() as BottomNavActivity
    }

    override fun onDetach() {
        super.onDetach()
        bottomNavActivity = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bottomNavActivity?.showBottomNav()
        _binding = FragmentPracticeHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wordViewModel.wordGroupsWithEnoughWords.observe(viewLifecycleOwner, { wordGroupsWithWords ->
            binding.emptyGroupsDisplay.isVisible = wordGroupsWithWords.isEmpty()
            binding.practiceDisplay.isVisible = wordGroupsWithWords.isNotEmpty()
            binding.guessTheWordButton.setOnClickListener {
                practiceViewModel.setCurrentCardPosition(0, 0)
                if (wordGroupsWithWords.size > 1) {
                    val action = PracticeHomeFragmentDirections.actionPracticeHomeFragmentToGroupSelectFragment(
                        PracticeType.GUESS_THE_WORD
                    )
                    reenterTransition = MaterialFadeThrough()
                    findNavController().navigate(action)
                } else {
                    wordGroupsWithWords.first().words.shuffle()
                    val practiceType = PracticeType.GUESS_THE_WORD
                    val action = PracticeHomeFragmentDirections.actionPracticeHomeFragmentToPracticeFragment(
                        practiceType,
                        wordGroupsWithWords.first(),
                        getString(practiceType.labelResId)
                    )
                    reenterTransition = MaterialFadeThrough()
                    findNavController().navigate(action)
                }
            }
            binding.guessTheMeaningButton.setOnClickListener {
                practiceViewModel.setCurrentCardPosition(0, 0)
                if (wordGroupsWithWords.size > 1) {
                    val action = PracticeHomeFragmentDirections.actionPracticeHomeFragmentToGroupSelectFragment(
                        PracticeType.GUESS_THE_MEANING
                    )
                    reenterTransition = MaterialFadeThrough()
                    findNavController().navigate(action)
                } else {
                    wordGroupsWithWords.first().words.shuffle()
                    val practiceType = PracticeType.GUESS_THE_MEANING
                    val action = PracticeHomeFragmentDirections.actionPracticeHomeFragmentToPracticeFragment(
                        practiceType,
                        wordGroupsWithWords.first(),
                        getString(practiceType.labelResId)
                    )
                    reenterTransition = MaterialFadeThrough()
                    findNavController().navigate(action)
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}