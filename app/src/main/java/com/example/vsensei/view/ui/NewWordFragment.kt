package com.example.vsensei.view.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.vsensei.R
import com.example.vsensei.data.Word
import com.example.vsensei.databinding.FragmentNewWordBinding
import com.example.vsensei.viewmodel.WordViewModel
import com.google.android.material.transition.MaterialContainerTransform

class NewWordFragment : Fragment() {

    private var _binding: FragmentNewWordBinding? = null
    private val binding get() = _binding!!
    private val args: NewWordFragmentArgs by navArgs()

    private lateinit var wordViewModel: WordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val enterTransition = MaterialContainerTransform().apply {
            scrimColor = Color.TRANSPARENT
            drawingViewId = R.id.nav_host_fragment
        }
        sharedElementEnterTransition = enterTransition
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewWordBinding.inflate(inflater, container, false)
        wordViewModel = ViewModelProvider(this).get(WordViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.closeButton.setOnClickListener { findNavController().navigateUp() }
        val languages = resources.getStringArray(R.array.languages)
        val selectedLanguageIndex = args.wordGroup.selectedLanguageIndex
        when(languages[selectedLanguageIndex]) {
            getString(R.string.japanese) -> {
                binding.wordPrimaryContainer.setHint(R.string.hiragana)
                binding.wordPrimaryContainer.helperText = getString(R.string.hiragana_example)
            }
            else -> {
                binding.wordPrimaryContainer.hint = getString(R.string.new_word_hint, languages[selectedLanguageIndex])
                binding.wordPrimaryVariantContainer.visibility = View.GONE
            }
        }
        binding.addWordButton.setOnClickListener {
            val inputManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.windowToken, 0)
            if (isValidData()) {
                val word = Word(
                    0,
                    args.wordGroup.groupId,
                    binding.wordPrimaryInput.text.toString(),
                    binding.wordPrimaryVariantInput.text.toString(),
                    binding.wordMeaningInput.text.toString()
                )
                wordViewModel.addWord(word)
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun isValidData(): Boolean {
        if (binding.wordPrimaryInput.text.isNullOrBlank()) {
            binding.wordPrimaryContainer.error = getString(R.string.main_word_error)
        } else {
            binding.wordPrimaryContainer.isErrorEnabled = false
        }
        if (binding.wordMeaningInput.text.isNullOrBlank()) {
            binding.wordMeaningContainer.error = getString(R.string.word_meaning_error)
        } else {
            binding.wordMeaningContainer.isErrorEnabled = false
        }
        return !binding.wordPrimaryContainer.isErrorEnabled && !binding.wordMeaningContainer.isErrorEnabled
    }
}