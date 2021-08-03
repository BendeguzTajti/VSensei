package com.example.vsensei.view.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.vsensei.R
import com.example.vsensei.databinding.FragmentPracticeHomeBinding
import com.example.vsensei.viewmodel.WordViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class PracticeHomeFragment : Fragment() {

    private var _binding: FragmentPracticeHomeBinding? = null
    private val binding get() = _binding!!

    private val wordViewModel: WordViewModel by activityViewModels()

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
        wordViewModel.wordGroupsWithWords.observe(viewLifecycleOwner, { wordGroupsWithWords ->
            binding.emptyGroupsDisplay.isVisible = wordGroupsWithWords.isEmpty()
            binding.practiceDisplay.isVisible = wordGroupsWithWords.isNotEmpty()
            binding.guessTheWordButton.setOnClickListener {
                val action = PracticeHomeFragmentDirections.actionPracticeHomeFragmentToGroupSelectFragment(
                    wordGroupsWithWords.toTypedArray(),
                    getString(R.string.guess_the_word)
                )
                findNavController().navigate(action)
            }
            binding.guessTheMeaningButton.setOnClickListener {
                val action = PracticeHomeFragmentDirections.actionPracticeHomeFragmentToGroupSelectFragment(
                    wordGroupsWithWords.toTypedArray(),
                    getString(R.string.guess_the_meaning)
                )
                findNavController().navigate(action)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}