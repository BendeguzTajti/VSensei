package com.example.vsensei.view.ui

import android.content.Context
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
import com.example.vsensei.view.contract.BottomNavActivity
import com.example.vsensei.viewmodel.PracticeViewModel
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class PracticeFragment : Fragment(), PracticeCardAdapter.WordGuessCallback {

    private var bottomNavActivity: BottomNavActivity? = null
    private var _binding: FragmentPracticeBinding? = null
    private val binding get() = _binding!!

    private val args: PracticeFragmentArgs by navArgs()
    private val practiceViewModel: PracticeViewModel by sharedViewModel()

    private var textToSpeech: TextToSpeech? = null
    private lateinit var practiceSummary: PracticeSummary
    private lateinit var correctAnswerSoundPlayer: MediaPlayer
    private lateinit var wrongAnswerSoundPlayer: MediaPlayer

    override fun onAttach(context: Context) {
        super.onAttach(context)
        bottomNavActivity = requireActivity() as BottomNavActivity
    }

    override fun onDetach() {
        super.onDetach()
        bottomNavActivity = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        returnTransition = MaterialFadeThrough()
        textToSpeech = TextToSpeech(requireActivity().applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val locale = Locale(args.wordGroupWithWords.wordGroup.localeLanguage)
                textToSpeech?.language = locale
            } else {
                textToSpeech = null
            }
        }
        correctAnswerSoundPlayer = MediaPlayer.create(requireContext(), R.raw.correct_answer)
        wrongAnswerSoundPlayer = MediaPlayer.create(requireContext(), R.raw.wrong_answer)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bottomNavActivity?.hideBottomNav()
        _binding = FragmentPracticeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // THE SELECTED INDEX IS THE SAME AS THE INDEX OF JAPANESE LANGUAGE IN THE STRING ARRAY,
        // BECAUSE TIGHT NOW ONLY JAPANESE CAN HAVE VARIANTS (KANJI, HIRAGANA, KATAKANA)
        val hasVariants = args.wordGroupWithWords.wordGroup.selectedLanguageIndex == 1

        practiceSummary = savedInstanceState?.getParcelable(PRACTICE_SUMMARY_KEY) ?: PracticeSummary(
            0,
            args.practiceType,
            args.wordGroupWithWords.wordGroup.groupName,
            arrayListOf(),
            hasVariants,
            System.currentTimeMillis()
        )
        val wordGroupWithWords = args.wordGroupWithWords
        val selectedLanguageIndex = wordGroupWithWords.wordGroup.selectedLanguageIndex
        val displayLanguages = resources.getStringArray(R.array.display_languages)
        val adapter = PracticeCardAdapter(
            args.practiceType,
            wordGroupWithWords.words,
            displayLanguages[selectedLanguageIndex],
            hasVariants,
            childFragmentManager,
            lifecycle
        )
        binding.practiceCardsViewPager.apply {
            isUserInputEnabled = false
            offscreenPageLimit = 1
            this.adapter = adapter
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
        if (wordGuess.isCorrectGuess) {
            correctAnswerSoundPlayer.start()
        } else {
            wrongAnswerSoundPlayer.start()
        }
        practiceSummary.wordGuesses.add(wordGuess)
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