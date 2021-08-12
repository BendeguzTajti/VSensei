package com.example.vsensei.view.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.isVisible
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.example.vsensei.R
import com.example.vsensei.data.PracticeType
import com.example.vsensei.databinding.FragmentPracticeHomeBinding
import com.example.vsensei.viewmodel.WordViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.android.viewmodel.ext.android.sharedViewModel

class PracticeHomeFragment : Fragment() {

    private var _binding: FragmentPracticeHomeBinding? = null
    private val binding get() = _binding!!

    private val wordViewModel: WordViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPracticeHomeBinding.inflate(inflater, container, false)
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)?.isVisible = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addWordsButton.setOnClickListener {
            requireActivity()
                .findViewById<BottomNavigationView>(R.id.bottom_navigation)
                ?.selectedItemId = R.id.dictionaryFragment
        }
        wordViewModel.wordGroupsWithEnoughWords.observe(viewLifecycleOwner, { wordGroupsWithWords ->
            binding.emptyGroupsDisplay.isVisible = wordGroupsWithWords.isEmpty()
            binding.practiceDisplay.isVisible = wordGroupsWithWords.isNotEmpty()
            binding.guessTheWordButton.setOnClickListener {
                if (wordGroupsWithWords.size > 1) {
                    val action = PracticeHomeFragmentDirections.actionPracticeHomeFragmentToGroupSelectFragment(
                        wordGroupsWithWords.toTypedArray(),
                        PracticeType.GUESS_THE_WORD
                    )
                    findNavController().navigate(action)
                } else {
                    wordGroupsWithWords.first().words.shuffle()
                    val practiceType = PracticeType.GUESS_THE_WORD
                    val action = PracticeHomeFragmentDirections.actionPracticeHomeFragmentToPracticeActivity(
                        practiceType,
                        wordGroupsWithWords.first(),
                        getString(practiceType.labelResId)
                    )
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity())
                    val extras = ActivityNavigatorExtras(options)
                    findNavController().navigate(action, extras)
                }
            }
            binding.guessTheMeaningButton.setOnClickListener {
                if (wordGroupsWithWords.size > 1) {
                    val action = PracticeHomeFragmentDirections.actionPracticeHomeFragmentToGroupSelectFragment(
                        wordGroupsWithWords.toTypedArray(),
                        PracticeType.GUESS_THE_MEANING
                    )
                    findNavController().navigate(action)
                } else {
                    wordGroupsWithWords.first().words.shuffle()
                    val practiceType = PracticeType.GUESS_THE_MEANING
                    val action = PracticeHomeFragmentDirections.actionPracticeHomeFragmentToPracticeActivity(
                        practiceType,
                        wordGroupsWithWords.first(),
                        getString(practiceType.labelResId)
                    )
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity())
                    val extras = ActivityNavigatorExtras(options)
                    findNavController().navigate(action, extras)
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}