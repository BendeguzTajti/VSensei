package com.example.vsensei.view.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import androidx.core.view.isVisible
import com.example.vsensei.R
import com.example.vsensei.data.PracticeType
import com.example.vsensei.data.Word
import com.example.vsensei.data.WordGuess
import com.example.vsensei.databinding.FragmentPracticeCardBinding
import com.example.vsensei.view.adapter.PracticeCardAdapter
import com.example.vsensei.viewmodel.PracticeViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.util.*

class PracticeCardFragment : Fragment() {

    private var _binding: FragmentPracticeCardBinding? = null
    private val binding get() = _binding!!
    private var wordGuessCallback: PracticeCardAdapter.WordGuessCallback? = null

    private val practiceViewModel: PracticeViewModel by sharedViewModel()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        wordGuessCallback = try {
            requireParentFragment() as PracticeCardAdapter.WordGuessCallback
        } catch (e: ClassCastException) {
            null
        }
    }

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
        val currentWord: Word = requireArguments().get(CURRENT_WORD) as Word
        val isAnswerVisible = savedInstanceState?.getBoolean(IS_ANSWER_VISIBLE, false) ?: false
        val isGuessEnabled = savedInstanceState?.getBoolean(IS_GUESS_ENABLED, true) ?: true
        binding.guessLayout.isEnabled = isGuessEnabled
        binding.answer.visibility = if (isAnswerVisible) View.VISIBLE else View.INVISIBLE
        if (practiceType == PracticeType.GUESS_THE_MEANING) {
            guessTheMeaningCardInit(currentWord)
        } else {
            guessTheWordCardInit(currentWord)
        }
        binding.guessLayout.setEndIconOnClickListener {
            if (!binding.guess.text.isNullOrBlank()) {
                binding.guessLayout.isEnabled = false
                binding.cardPracticeItemRoot.transitionToState(R.id.merged)
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

    override fun onDetach() {
        super.onDetach()
        wordGuessCallback = null
    }

    private fun guessTheMeaningCardInit(currentWord: Word) {
        val hasVariants = requireArguments().getBoolean(HAS_VARIANTS)
        binding.hint.text =
            if (currentWord.wordPrimaryVariant.isNullOrBlank()) currentWord.wordPrimary else currentWord.wordPrimaryVariant
        binding.hintVariant.apply {
            isVisible = hasVariants
            if (!currentWord.wordPrimaryVariant.isNullOrBlank()) {
                text = "(${currentWord.wordPrimary})"
            }
        }
        binding.answer.text = currentWord.wordMeanings.joinToString(separator = ", ")
        val currentPosition: Int = requireArguments().get(CURRENT_POSITION) as Int
        binding.cardPracticeItemRoot.setTransitionListener(object : TransitionAdapter() {
            override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
                super.onTransitionCompleted(motionLayout, currentId)
                if (currentId == R.id.merged) {
                    val hint = binding.hint.text.toString()
                    val hintVariant = binding.hintVariant.text.toString()
                    val guesses = binding.guess.text.toString().split(",").map { it.trim().toLowerCase(Locale.getDefault()) }
                    val matches = currentWord.wordMeanings.filter { guesses.contains(it.toLowerCase(Locale.getDefault())) }
                    if (matches.isNotEmpty()) {
                        val wordGuess = WordGuess(hint, hintVariant, binding.answer.text.toString(), true)
                        wordGuessCallback?.onWordGuessed(wordGuess)
                        motionLayout.transitionToState(R.id.success)
                        practiceViewModel.setCurrentCardPosition(currentPosition + 1, 1400)
                    } else {
                        val wordGuess = WordGuess(hint, hintVariant, binding.answer.text.toString(), false)
                        wordGuessCallback?.onWordGuessed(wordGuess)
                        motionLayout.transitionToState(R.id.failure)
                        practiceViewModel.setCurrentCardPosition(currentPosition + 1, 2000)
                    }
                }
                if (currentId == R.id.success || currentId == R.id.failure) {
                    binding.answer.isVisible = true
                }
            }
        })
        audioButtonInit(currentWord)
    }

    private fun guessTheWordCardInit(currentWord: Word) {
        binding.hint.text = currentWord.wordMeanings.joinToString(separator = ", ")
        binding.hintVariant.isVisible = false
        binding.answer.text = currentWord.wordPrimaryVariant ?: currentWord.wordPrimary
        val currentPosition: Int = requireArguments().get(CURRENT_POSITION) as Int
        binding.cardPracticeItemRoot.setTransitionListener(object : TransitionAdapter() {
            override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
                super.onTransitionCompleted(motionLayout, currentId)
                if (currentId == R.id.merged) {
                    val hint = binding.hint.text.toString()
                    val guess = binding.guess.text.toString().toLowerCase(Locale.getDefault())
                    val answer = currentWord.wordPrimary.toLowerCase(Locale.getDefault())
                    val answerVariant =
                        currentWord.wordPrimaryVariant?.toLowerCase(Locale.getDefault())
                    if (guess == answer || guess == answerVariant) {
                        val wordGuess = WordGuess(hint, null, binding.answer.text.toString(), true)
                        wordGuessCallback?.onWordGuessed(wordGuess)
                        motionLayout.transitionToState(R.id.success)
                        practiceViewModel.setCurrentCardPosition(currentPosition + 1, 1400)
                    } else {
                        val wordGuess = WordGuess(hint, null, binding.answer.text.toString(), false)
                        wordGuessCallback?.onWordGuessed(wordGuess)
                        motionLayout.transitionToState(R.id.failure)
                        practiceViewModel.setCurrentCardPosition(currentPosition + 1, 2000)
                    }
                }
                if (currentId == R.id.success || currentId == R.id.failure) {
                    binding.answer.isVisible = true
                }
            }
        })
        audioButtonInit(currentWord)
    }

    private fun audioButtonInit(currentWord: Word) {
        val groupLanguage: String = requireArguments().get(GROUP_LANGUAGE) as String
        binding.audioButton.text = groupLanguage
        binding.audioButton.setOnClickListener {
            wordGuessCallback?.sayWord(currentWord.wordPrimary)
        }
    }

    companion object {
        private const val PRACTICE_TYPE = "PRACTICE_TYPE"
        private const val CURRENT_WORD = "CURRENT_WORD"
        private const val GROUP_LANGUAGE = "GROUP_LANGUAGE"
        private const val HAS_VARIANTS = "HAS_VARIANTS"
        private const val CURRENT_POSITION = "CURRENT_POSITION"
        private const val IS_ANSWER_VISIBLE = "IS_ANSWER_VISIBLE"
        private const val IS_GUESS_ENABLED = "IS_GUESS_ENABLED"

        @JvmStatic
        fun newInstance(
            practiceType: PracticeType,
            currentWord: Word,
            groupLanguage: String,
            hasVariants: Boolean,
            currentPosition: Int
        ) =
            PracticeCardFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(PRACTICE_TYPE, practiceType)
                    putParcelable(CURRENT_WORD, currentWord)
                    putString(GROUP_LANGUAGE, groupLanguage)
                    putBoolean(HAS_VARIANTS, hasVariants)
                    putInt(CURRENT_POSITION, currentPosition)
                }
            }
    }
}