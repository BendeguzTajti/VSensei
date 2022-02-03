package com.example.vsensei.view.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.vsensei.R
import com.example.vsensei.data.PracticeType
import com.example.vsensei.data.Word
import com.example.vsensei.databinding.FragmentPracticeCardBinding
import com.example.vsensei.viewmodel.PracticeViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel

class PracticeCardFragment : Fragment() {

    private var _binding: FragmentPracticeCardBinding? = null
    private val binding get() = _binding!!

    private val practiceViewModel: PracticeViewModel by lazy {
        requireParentFragment().getViewModel()
    }

    private lateinit var speechRecognizer: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPracticeCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val practiceType: PracticeType = requireArguments().get(PRACTICE_TYPE) as PracticeType
        val currentPosition: Int = requireArguments().get(CURRENT_POSITION) as Int
        val currentWord: Word = requireArguments().get(CURRENT_WORD) as Word
        val isAnswerVisible = savedInstanceState?.getBoolean(IS_ANSWER_VISIBLE, false) ?: false
        val isGuessEnabled = savedInstanceState?.getBoolean(IS_GUESS_ENABLED, true) ?: true
        binding.guessLayout.isEnabled = isGuessEnabled
        binding.answer.visibility = if (isAnswerVisible) View.VISIBLE else View.INVISIBLE
        if (practiceType == PracticeType.GUESS_THE_WORD) {
            speechToTextInit()
            guessTheWordCardInit(currentWord)
        } else {
            guessTheMeaningCardInit(currentWord, currentPosition)
        }
        binding.guessLayout.setEndIconOnClickListener {
            if (!binding.guess.text.isNullOrBlank()) {
                binding.guessLayout.isEnabled = false
                binding.cardPracticeItemRoot.transitionToState(R.id.merged)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                practiceViewModel.onWordGuess.collect {
                    val cardPosition = it.first
                    val isCorrect = it.second
                    if (cardPosition == currentPosition) {
                        if (isCorrect) {
                            binding.cardPracticeItemRoot.transitionToState(R.id.success)
                        } else {
                            binding.cardPracticeItemRoot.transitionToState(R.id.failure)
                        }
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_ANSWER_VISIBLE, binding.answer.isVisible)
        outState.putBoolean(IS_GUESS_ENABLED, binding.guessLayout.isEnabled)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun guessTheWordCardInit(currentWord: Word) {
        binding.hint.text = currentWord.wordMeanings.joinToString(separator = ", ")
        binding.answer.text = currentWord.wordPrimaryVariant ?: currentWord.wordPrimary
        val currentPosition: Int = requireArguments().get(CURRENT_POSITION) as Int
        binding.cardPracticeItemRoot.setTransitionListener(object : TransitionAdapter() {
            override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
                super.onTransitionCompleted(motionLayout, currentId)
                if (currentId == R.id.merged) {
                    val guess = binding.guess.text.toString()
                    val answer = currentWord.wordPrimary
                    val answerVariant = currentWord.wordPrimaryVariant
                    practiceViewModel.onWordGuess(currentPosition, guess, answer, answerVariant)
                }
                if (currentId == R.id.success || currentId == R.id.failure) {
                    binding.answer.isVisible = true
                }
            }
        })
        binding.guessLayout.setStartIconOnClickListener {
            val language = requireArguments().getString(GROUP_LANGUAGE, "")
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
                putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, true)
                putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_recognizer_extra_prompt))
            }
            speechRecognizer.launch(intent)
        }
    }

    private fun guessTheMeaningCardInit(currentWord: Word, currentPosition: Int) {
        binding.guessLayout.startIconDrawable = null
        binding.hint.text =
            if (currentWord.wordPrimaryVariant.isNullOrBlank()) currentWord.wordPrimary else currentWord.wordPrimaryVariant
        binding.hintVariant.apply {
            if (!currentWord.wordPrimaryVariant.isNullOrBlank()) {
                text = getString(R.string.hint_variant_display, currentWord.wordPrimary)
                isVisible = true
            }
        }
        binding.answer.text = currentWord.wordMeanings.joinToString(separator = ", ")
        binding.cardPracticeItemRoot.setTransitionListener(object : TransitionAdapter() {
            override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
                super.onTransitionCompleted(motionLayout, currentId)
                if (currentId == R.id.merged) {
                    val guess = binding.guess.text.toString()
                    practiceViewModel.onWordGuess(currentPosition, guess, currentWord.wordMeanings)
                }
                if (currentId == R.id.success || currentId == R.id.failure) {
                    binding.answer.isVisible = true
                }
            }
        })
    }

    private fun speechToTextInit() {
        speechRecognizer = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val recognizedWords = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (result.resultCode == Activity.RESULT_OK && !recognizedWords.isNullOrEmpty()) {
                binding.guess.setText(recognizedWords.first())
                binding.guessLayout.isEnabled = false
                binding.cardPracticeItemRoot.transitionToState(R.id.merged)
            }
        }
    }

    companion object {
        private const val PRACTICE_TYPE = "PRACTICE_TYPE"
        private const val CURRENT_WORD = "CURRENT_WORD"
        private const val GROUP_LANGUAGE = "GROUP_LANGUAGE"
        private const val CURRENT_POSITION = "CURRENT_POSITION"
        private const val IS_ANSWER_VISIBLE = "IS_ANSWER_VISIBLE"
        private const val IS_GUESS_ENABLED = "IS_GUESS_ENABLED"

        @JvmStatic
        fun newInstance(
            practiceType: PracticeType,
            currentWord: Word,
            groupLanguage: String,
            currentPosition: Int
        ) =
            PracticeCardFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(PRACTICE_TYPE, practiceType)
                    putParcelable(CURRENT_WORD, currentWord)
                    putString(GROUP_LANGUAGE, groupLanguage)
                    putInt(CURRENT_POSITION, currentPosition)
                }
            }
    }
}