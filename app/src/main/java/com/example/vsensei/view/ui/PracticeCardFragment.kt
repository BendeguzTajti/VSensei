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
import com.example.vsensei.data.Word
import com.example.vsensei.databinding.FragmentPracticeCardBinding
import com.example.vsensei.view.adapter.PracticeCardAdapter
import java.util.*

class PracticeCardFragment : Fragment() {

    private var _binding: FragmentPracticeCardBinding? = null
    private val binding get() = _binding!!
    private var wordGuessCallback: PracticeCardAdapter.WordGuessCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        wordGuessCallback = try {
            requireActivity() as PracticeCardAdapter.WordGuessCallback
        } catch (e: ClassCastException) {
            null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =  FragmentPracticeCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentWord: Word = requireArguments().get(CURRENT_WORD) as Word
        val groupLanguage: String = requireArguments().get(GROUP_LANGUAGE) as String
        val isAnswerVisible = savedInstanceState?.getBoolean(IS_ANSWER_VISIBLE, false) ?: false
        val isGuessEnabled = savedInstanceState?.getBoolean(IS_GUESS_ENABLED, true) ?: true
        binding.wordPrimary.text = if (currentWord.wordPrimaryVariant.isNullOrBlank()) currentWord.wordPrimary else currentWord.wordPrimaryVariant
        binding.guessLayout.isEnabled = isGuessEnabled
        binding.wordPrimaryVariant.apply {
            isVisible = !currentWord.wordPrimaryVariant.isNullOrBlank()
            text = "(${currentWord.wordPrimary})"
        }
        binding.audioButton.text = groupLanguage
        binding.wordMeaning.text = currentWord.wordMeaning
        binding.wordMeaning.visibility = if (isAnswerVisible) View.VISIBLE else View.INVISIBLE
        binding.guessLayout.setEndIconOnClickListener {
            if (!binding.guess.text.isNullOrBlank()) {
                binding.guessLayout.isEnabled = false
                binding.cardPracticeItemRoot.transitionToState(R.id.merged)
            }
        }
        binding.cardPracticeItemRoot.setTransitionListener(object : TransitionAdapter() {
            override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
                super.onTransitionCompleted(motionLayout, currentId)
                if (currentId == R.id.merged) {
                    val guess = binding.guess.text.toString().toLowerCase(Locale.getDefault())
                    val wordMeaning = currentWord.wordMeaning.toLowerCase(Locale.getDefault())
                    if (guess == wordMeaning) {
                        wordGuessCallback?.onCorrectAnswer()
                        motionLayout.transitionToState(R.id.success)
                    } else {
                        wordGuessCallback?.onWrongAnswer()
                        motionLayout.transitionToState(R.id.failure)
                    }
                }
                if (currentId == R.id.success || currentId == R.id.failure) {
                    binding.wordMeaning.isVisible = true
                }
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_ANSWER_VISIBLE, binding.wordMeaning.isVisible)
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

    companion object {
        private const val CURRENT_WORD = "CURRENT_WORD"
        private const val GROUP_LANGUAGE = "GROUP_LANGUAGE"
        private const val IS_ANSWER_VISIBLE = "IS_ANSWER_VISIBLE"
        private const val IS_GUESS_ENABLED = "IS_GUESS_ENABLED"

        @JvmStatic
        fun newInstance(currentWord: Word, groupLanguage: String) =
            PracticeCardFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(CURRENT_WORD, currentWord)
                    putString(GROUP_LANGUAGE, groupLanguage)
                }
            }
    }
}