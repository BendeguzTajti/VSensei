package com.example.vsensei.view.ui

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
import com.example.vsensei.databinding.FragmentPracticeCardBinding
import com.example.vsensei.viewmodel.PracticeViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel

class PracticeCardFragment : Fragment() {

    private var _binding: FragmentPracticeCardBinding? = null
    private val binding get() = _binding!!

    private val practiceViewModel: PracticeViewModel by lazy {
        requireParentFragment().getViewModel()
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
        if (practiceType == PracticeType.GUESS_THE_WORD) {
            guessTheWordCardInit(currentWord)
        } else {
            guessTheMeaningCardInit(currentWord)
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

    private fun guessTheWordCardInit(currentWord: Word) {
        binding.hint.text = currentWord.wordMeanings.joinToString(separator = ", ")
        binding.answer.text = currentWord.wordPrimaryVariant ?: currentWord.wordPrimary
        val currentPosition: Int = requireArguments().get(CURRENT_POSITION) as Int
        binding.cardPracticeItemRoot.setTransitionListener(object : TransitionAdapter() {
            override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
                super.onTransitionCompleted(motionLayout, currentId)
                if (currentId == R.id.merged) {
                    val guess = binding.guess.text.toString().lowercase()
                    val answer = currentWord.wordPrimary.lowercase()
                    val answerVariant =
                        currentWord.wordPrimaryVariant?.lowercase()
                    if (guess == answer || guess == answerVariant) {
                        motionLayout.transitionToState(R.id.success)
                        practiceViewModel.onWordGuess(currentPosition, true)
                    } else {
                        motionLayout.transitionToState(R.id.failure)
                        practiceViewModel.onWordGuess(currentPosition, false)
                    }
                }
                if (currentId == R.id.success || currentId == R.id.failure) {
                    binding.answer.isVisible = true
                }
            }
        })
    }

    private fun guessTheMeaningCardInit(currentWord: Word) {
        binding.hint.text =
            if (currentWord.wordPrimaryVariant.isNullOrBlank()) currentWord.wordPrimary else currentWord.wordPrimaryVariant
        binding.hintVariant.apply {
            if (!currentWord.wordPrimaryVariant.isNullOrBlank()) {
                text = "(${currentWord.wordPrimary})"
                isVisible = true
            }
        }
        binding.answer.text = currentWord.wordMeanings.joinToString(separator = ", ")
        val currentPosition: Int = requireArguments().get(CURRENT_POSITION) as Int
        binding.cardPracticeItemRoot.setTransitionListener(object : TransitionAdapter() {
            override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
                super.onTransitionCompleted(motionLayout, currentId)
                if (currentId == R.id.merged) {
                    val guesses = binding.guess.text.toString().split(",").map { it.trim().lowercase() }
                    val matches = currentWord.wordMeanings.filter { guesses.contains(it.lowercase()) }
                    if (matches.isNotEmpty()) {
                        motionLayout.transitionToState(R.id.success)
                        practiceViewModel.onWordGuess(currentPosition, true)
                    } else {
                        motionLayout.transitionToState(R.id.failure)
                        practiceViewModel.onWordGuess(currentPosition, false)
                    }
                }
                if (currentId == R.id.success || currentId == R.id.failure) {
                    binding.answer.isVisible = true
                }
            }
        })
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