package com.example.vsensei.view.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.vsensei.R
import com.example.vsensei.data.WordGroup
import com.example.vsensei.databinding.FragmentNewGroupBinding
import com.example.vsensei.viewmodel.WordViewModel
import com.google.android.material.transition.MaterialContainerTransform

class NewGroupFragment : Fragment() {

    private var _binding: FragmentNewGroupBinding? = null
    private val binding get() = _binding!!

    private lateinit var wordViewModel: WordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val enterTransition = MaterialContainerTransform().apply {
            scrimColor = Color.TRANSPARENT
            drawingViewId = R.id.nav_host_fragment
        }
        sharedElementEnterTransition = enterTransition
    }

    override fun onResume() {
        super.onResume()
        val languages = resources.getStringArray(R.array.languages)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, languages)
        binding.languageSelector.setAdapter(arrayAdapter)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewGroupBinding.inflate(inflater, container, false)
        wordViewModel = ViewModelProvider(this).get(WordViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val latestPosition = wordViewModel.getLatestSelectedLanguageIndex()
        val languages = resources.getStringArray(R.array.languages)
        binding.languageSelector.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val inputManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
        binding.languageSelector.setText(languages[latestPosition], false)
        binding.languageSelector.setOnItemClickListener { _, _, position, _ ->
            wordViewModel.saveSelectedLanguageIndex(position)
        }
        binding.closeButton.setOnClickListener { findNavController().navigateUp() }
        binding.createGroupButton.setOnClickListener {
            val inputManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.windowToken, 0)
            if (isValidData()) {
                val groupName = binding.groupNameInput.text.toString()
                val selectedLanguageIndex = wordViewModel.getLatestSelectedLanguageIndex()
                val wordGroup = WordGroup(0, groupName, 0, selectedLanguageIndex, System.currentTimeMillis())
                wordViewModel.addWordGroup(wordGroup)
                findNavController().navigateUp()
            }
        }
    }

    private fun isValidData(): Boolean {
        if (binding.groupNameInput.text.isNullOrBlank()) {
            binding.groupNameInputContainer.error = getString(R.string.group_name_error)
        } else {
            binding.groupNameInputContainer.isErrorEnabled = false
        }
        return !binding.groupNameInputContainer.isErrorEnabled
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}