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
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class NewWordFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentNewWordBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<NewWordFragmentArgs>()

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
        val selectedWord = args.word
        val displayLanguages = resources.getStringArray(R.array.display_languages)
        val selectedLanguageIndex = args.wordGroup.selectedLanguageIndex
        when(displayLanguages[selectedLanguageIndex]) {
            getString(R.string.japanese) -> {
                binding.wordPrimaryContainer.setHint(R.string.hiragana_or_katakana)
            }
            else -> {
                binding.wordPrimaryContainer.hint = getString(R.string.new_word_hint, displayLanguages[selectedLanguageIndex])
                binding.wordPrimaryVariantContainer.visibility = View.GONE
            }
        }
        if (selectedWord != null) {
            binding.cardTitle.text = selectedWord.wordPrimaryVariant ?: selectedWord.wordPrimary
            binding.wordPrimaryInput.setText(selectedWord.wordPrimary)
            binding.wordPrimaryVariantInput.setText(selectedWord.wordPrimaryVariant)
            binding.wordMeaningInput.setText(selectedWord.wordMeanings.joinToString(", "))
            binding.addWordButton.setText(R.string.update_word)
        } else {
            binding.cardTitle.setText(R.string.new_word)
            binding.addWordButton.setText(R.string.add_word)
        }
        binding.addWordButton.setOnClickListener {
            val inputManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.windowToken, 0)
            if (isValidData()) {
                val wordId = selectedWord?.wordId ?: 0
                val wordPrimary = binding.wordPrimaryInput.text.toString()
                val wordPrimaryVariant = if (binding.wordPrimaryVariantInput.text.isNullOrBlank()) null else binding.wordPrimaryVariantInput.text.toString()
                val wordMeaning = binding.wordMeaningInput.text.toString()
                val word = Word(
                    wordId,
                    args.wordGroup.groupId,
                    wordPrimary,
                    wordPrimaryVariant,
                    wordMeaning.split(",").map { it.trim() }
                )
                if (selectedWord != null) {
                    wordViewModel.updateWord(word)
                } else {
                    wordViewModel.addWord(word)
                }
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun getTheme(): Int = R.style.ThemeOverlay_App_BottomSheetDialog

    private fun isValidData(): Boolean {
        if (binding.wordPrimaryInput.text.isNullOrBlank()) {
            binding.wordPrimaryContainer.error = getString(R.string.main_word_error)
        } else {
            binding.wordPrimaryContainer.error = null
        }
        if (binding.wordMeaningInput.text.isNullOrBlank()) {
            binding.wordMeaningContainer.error = getString(R.string.word_meaning_error)
        } else {
            binding.wordMeaningContainer.error = null
        }
        return !binding.wordPrimaryInput.text.isNullOrBlank() && !binding.wordMeaningInput.text.isNullOrBlank()
    }
}