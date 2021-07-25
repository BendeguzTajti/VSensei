package com.example.vsensei.view.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.example.vsensei.R
import com.example.vsensei.databinding.FragmentPracticeBinding
import com.example.vsensei.view.adapter.PracticeCardAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class PracticeFragment : Fragment() {

    private val args: PracticeFragmentArgs by navArgs()

    private var _binding: FragmentPracticeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPracticeBinding.inflate(inflater, container, false)
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)?.isVisible = false
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val wordGroupWithWords = args.wordGroupWithWords
        val selectedLanguageIndex = wordGroupWithWords.wordGroup.selectedLanguageIndex
        val languages = resources.getStringArray(R.array.languages)
        val adapter = PracticeCardAdapter(
            wordGroupWithWords.words,
            languages[selectedLanguageIndex],
            childFragmentManager,
            lifecycle
        )
        binding.practiceCardsViewPager.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}