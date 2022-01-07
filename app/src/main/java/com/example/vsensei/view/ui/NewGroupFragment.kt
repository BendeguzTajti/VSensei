package com.example.vsensei.view.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import com.example.vsensei.R
import com.example.vsensei.data.WordGroup
import com.example.vsensei.databinding.FragmentNewGroupBinding
import com.example.vsensei.viewmodel.WordViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class NewGroupFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentNewGroupBinding? = null
    private val binding get() = _binding!!

    private val wordViewModel: WordViewModel by sharedViewModel()

    override fun onResume() {
        super.onResume()
        val displayLanguages = resources.getStringArray(R.array.display_languages)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, displayLanguages)
        binding.languageSelector.setAdapter(arrayAdapter)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val latestPosition = wordViewModel.getLatestSelectedLanguageIndex()
        val displayLanguages = resources.getStringArray(R.array.display_languages)
        binding.languageSelector.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val inputManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
        binding.languageSelector.setText(displayLanguages[latestPosition], false)
        binding.languageSelector.setOnItemClickListener { _, _, position, _ ->
            wordViewModel.saveSelectedLanguageIndex(position)
        }
        binding.createGroupButton.setOnClickListener {
            val inputManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.windowToken, 0)
            if (isValidData()) {
                val localeLanguages = resources.getStringArray(R.array.locale_languages)
                val groupName = binding.groupNameInput.text.toString()
                val selectedLanguageIndex = wordViewModel.getLatestSelectedLanguageIndex()
                val wordGroup = WordGroup(
                    0,
                    groupName,
                    selectedLanguageIndex,
                    localeLanguages[selectedLanguageIndex],
                    System.currentTimeMillis()
                )
                wordViewModel.addWordGroup(wordGroup)
                dismiss()
            }
        }
    }

    private fun isValidData(): Boolean {
        if (binding.groupNameInput.text.isNullOrBlank()) {
            binding.groupNameInputContainer.error = getString(R.string.group_name_error)
        } else {
            binding.groupNameInputContainer.error = null
        }
        return !binding.groupNameInput.text.isNullOrBlank()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun getTheme(): Int = R.style.ThemeOverlay_App_BottomSheetDialog
}