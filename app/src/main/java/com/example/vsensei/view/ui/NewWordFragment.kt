package com.example.vsensei.view.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.navigation.fragment.navArgs
import com.example.vsensei.R
import com.example.vsensei.data.Word
import com.example.vsensei.databinding.FragmentNewWordBinding
import com.example.vsensei.viewmodel.WordViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.viewmodel.ext.android.sharedViewModel

class NewWordFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentNewWordBinding? = null
    private val binding get() = _binding!!
    private val args: NewWordFragmentArgs by navArgs()

    private val wordViewModel: WordViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewWordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val displayLanguages = resources.getStringArray(R.array.display_languages)
        val selectedLanguageIndex = args.wordGroup.selectedLanguageIndex
        when(displayLanguages[selectedLanguageIndex]) {
            getString(R.string.japanese) -> {
                binding.wordPrimaryContainer.setHint(R.string.hiragana_or_katakana)
                binding.wordPrimaryContainer.helperText = getString(R.string.hiragana_example)
            }
            else -> {
                binding.wordPrimaryContainer.hint = getString(R.string.new_word_hint, displayLanguages[selectedLanguageIndex])
                binding.wordPrimaryVariantContainer.visibility = View.GONE
            }
        }
        binding.addWordButton.setOnClickListener {
            val inputManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.windowToken, 0)
            if (isValidData()) {
                val wordPrimary = binding.wordPrimaryInput.text.toString()
                val wordPrimaryVariant = if (binding.wordPrimaryVariantInput.text.isNullOrBlank()) null else binding.wordPrimaryVariantInput.text.toString()
                val wordMeaning = binding.wordMeaningInput.text.toString()
                val word = Word(
                    0,
                    args.wordGroup.groupId,
                    wordPrimary,
                    wordPrimaryVariant,
                    wordMeaning
                )
                wordViewModel.addWord(word)
                dismiss()
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