package com.example.vsensei.view.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.vsensei.R
import com.example.vsensei.databinding.FragmentPracticeBinding
import com.example.vsensei.view.adapter.PracticeCardAdapter
import com.example.vsensei.viewmodel.WordViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.android.ext.android.inject
import java.util.*

class PracticeFragment : Fragment(), PracticeCardAdapter.WordGuessCallback {

    private val args: PracticeFragmentArgs by navArgs()

    private var _binding: FragmentPracticeBinding? = null
    private val binding get() = _binding!!

    private val wordViewModel: WordViewModel by activityViewModels()
    private val textToSpeech: TextToSpeech? by inject()


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
        val wordGroupWithWords = args.wordGroupWithWords
        val selectedLanguageIndex = wordGroupWithWords.wordGroup.selectedLanguageIndex
        val displayLanguages = resources.getStringArray(R.array.display_languages)
        val adapter = PracticeCardAdapter(
            wordGroupWithWords.words,
            displayLanguages[selectedLanguageIndex],
            childFragmentManager,
            lifecycle
        )
        binding.practiceCardsViewPager.apply {
            isUserInputEnabled = false
            this.adapter = adapter
        }
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

    override fun onWordGuessed(currentPosition: Int, isCorrectGuess: Boolean) {
        if (isCorrectGuess) {
            correctAnswerSoundPlayer.start()
            Handler(Looper.getMainLooper()).postDelayed(
                {binding.practiceCardsViewPager.currentItem = currentPosition + 1},
                1400
            )
        } else {
            wrongAnswerSoundPlayer.start()
            Handler(Looper.getMainLooper()).postDelayed(
                {binding.practiceCardsViewPager.currentItem = currentPosition + 1},
                2000
            )
        }
    }
}