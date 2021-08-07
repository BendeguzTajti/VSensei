package com.example.vsensei.view.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.vsensei.R
import com.example.vsensei.data.PracticeSummary
import com.example.vsensei.databinding.FragmentPracticeBinding
import com.example.vsensei.view.adapter.PracticeCardAdapter
import com.example.vsensei.viewmodel.PracticeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.transition.MaterialSharedAxis
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.util.*

class PracticeFragment : Fragment(), PracticeCardAdapter.WordGuessCallback {

    private val args by navArgs<PracticeActivityArgs>()

    private var _binding: FragmentPracticeBinding? = null
    private val binding get() = _binding!!

    private val practiceViewModel: PracticeViewModel by sharedViewModel()
    private val textToSpeech: TextToSpeech? by inject()

    private lateinit var practiceSummary: PracticeSummary
    private lateinit var correctAnswerSoundPlayer: MediaPlayer
    private lateinit var wrongAnswerSoundPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val locale = Locale(args.wordGroupWithWords.wordGroup.localeLanguage)
        textToSpeech?.language = locale
        correctAnswerSoundPlayer = MediaPlayer.create(requireContext(), R.raw.correct_answer)
        wrongAnswerSoundPlayer = MediaPlayer.create(requireContext(), R.raw.wrong_answer)
    }

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
        practiceSummary = savedInstanceState?.getParcelable(PRACTICE_SUMMARY_KEY) ?: PracticeSummary(
            args.practiceType,
            args.wordGroupWithWords.wordGroup.groupName,
            arrayListOf(),
            arrayListOf(),
            System.currentTimeMillis()
        )
        val wordGroupWithWords = args.wordGroupWithWords
        val selectedLanguageIndex = wordGroupWithWords.wordGroup.selectedLanguageIndex
        val displayLanguages = resources.getStringArray(R.array.display_languages)
        val adapter = PracticeCardAdapter(
            args.practiceType,
            wordGroupWithWords.words,
            displayLanguages[selectedLanguageIndex],
            childFragmentManager,
            lifecycle
        )
        binding.practiceCardsViewPager.apply {
            isUserInputEnabled = false
            offscreenPageLimit = 1
            this.adapter = adapter
        }
        practiceViewModel.currentCardPosition().observe(viewLifecycleOwner, { currentPosition ->
            if (currentPosition < args.wordGroupWithWords.words.size) {
                binding.practiceCardsViewPager.currentItem = currentPosition
            } else {
                navigateToPracticeResult()
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(PRACTICE_SUMMARY_KEY, practiceSummary)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        correctAnswerSoundPlayer.release()
        wrongAnswerSoundPlayer.release()
        textToSpeech?.stop()
    }

    override fun sayWord(word: String) {
        textToSpeech?.speak(word, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onWordGuessed(position: Int, isCorrectGuess: Boolean) {
        if (isCorrectGuess) {
            practiceSummary.correctGuesses.add(args.wordGroupWithWords.words[position])
            correctAnswerSoundPlayer.start()
        } else {
            practiceSummary.wrongGuesses.add(args.wordGroupWithWords.words[position])
            wrongAnswerSoundPlayer.start()
        }
    }

    private fun navigateToPracticeResult() {
        practiceViewModel.setCurrentPracticeSummary(practiceSummary)
        practiceViewModel.savePracticeSummary(practiceSummary)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        val action = PracticeFragmentDirections.actionPracticeFragmentToPracticeResultFragment(getString(args.practiceType.labelResId))
        findNavController().navigate(action)
    }

    companion object {
        private const val PRACTICE_SUMMARY_KEY = "PRACTICE_SUMMARY_KEY"
    }
}