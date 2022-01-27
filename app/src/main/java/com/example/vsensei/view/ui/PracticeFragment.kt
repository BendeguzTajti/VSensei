package com.example.vsensei.view.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.vsensei.R
import com.example.vsensei.data.PracticeSummary
import com.example.vsensei.data.WordGuess
import com.example.vsensei.databinding.FragmentPracticeBinding
import com.example.vsensei.view.adapter.PracticeCardAdapter
import com.example.vsensei.viewmodel.PracticeViewModel
import com.google.android.material.transition.MaterialSharedAxis
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class PracticeFragment : Fragment(), PracticeCardAdapter.WordGuessCallback {

    private var _binding: FragmentPracticeBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<PracticeFragmentArgs>()
    private val practiceViewModel: PracticeViewModel by sharedViewModel()

    private var textToSpeech: TextToSpeech? = null
    private lateinit var practiceSummary: PracticeSummary
    private val practiceCardAdapter: PracticeCardAdapter by lazy {
        val wordGroupWithWords = args.wordGroupWithWords
        val selectedLanguageIndex = wordGroupWithWords.wordGroup.selectedLanguageIndex
        val displayLanguages = resources.getStringArray(R.array.display_languages)
        val hasVariants = args.wordGroupWithWords.wordGroup.selectedLanguageIndex == 1
        PracticeCardAdapter(
            args.practiceType,
            wordGroupWithWords.words,
            displayLanguages[selectedLanguageIndex],
            hasVariants,
            childFragmentManager,
            lifecycle
        )
    }
    private val correctAnswerSoundPlayer: MediaPlayer by lazy {
        MediaPlayer.create(requireContext(), R.raw.correct_answer)
    }
    private val wrongAnswerSoundPlayer: MediaPlayer by lazy {
        MediaPlayer.create(requireContext(), R.raw.wrong_answer)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
        }
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
        }
        textToSpeech = TextToSpeech(requireActivity().applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val locale = Locale(args.wordGroupWithWords.wordGroup.localeLanguage)
                textToSpeech?.language = locale
            } else {
                textToSpeech = null
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPracticeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        practiceSummary = savedInstanceState?.getParcelable(PRACTICE_SUMMARY_KEY) ?: PracticeSummary(0)
        binding.practiceCardsViewPager.apply {
            isUserInputEnabled = false
            offscreenPageLimit = 1
            this.adapter = practiceCardAdapter
        }
        practiceViewModel.currentCardPosition.observe(viewLifecycleOwner, { currentPosition ->
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
        binding.practiceCardsViewPager.adapter = null
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        correctAnswerSoundPlayer.release()
        wrongAnswerSoundPlayer.release()
        textToSpeech?.shutdown()
    }

    override fun sayWord(word: String) {
        textToSpeech?.speak(word, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onWordGuessed(wordGuess: WordGuess) {
        practiceSummary = if (wordGuess.isCorrectGuess) {
            correctAnswerSoundPlayer.start()
            practiceSummary.copy(
                correctGuesses = practiceSummary.correctGuesses + 1
            )
        } else {
            wrongAnswerSoundPlayer.start()
            practiceSummary.copy(
                wrongGuesses = practiceSummary.wrongGuesses + 1
            )
        }
    }

    private fun navigateToPracticeResult() {
        practiceViewModel.setCurrentPracticeSummary(practiceSummary)
        practiceViewModel.savePracticeSummary(practiceSummary)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        val action = PracticeFragmentDirections.actionPracticeFragmentToPracticeResultFragment(
            args.practiceTypeLabel,
            practiceSummary.getPercent()
        )
        findNavController().navigate(action)
    }

    companion object {
        private const val PRACTICE_SUMMARY_KEY = "PRACTICE_SUMMARY_KEY"
    }
}