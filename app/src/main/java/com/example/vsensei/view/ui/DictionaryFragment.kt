package com.example.vsensei.view.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.transition.Fade
import com.example.vsensei.R
import com.example.vsensei.databinding.FragmentDictionaryBinding
import com.example.vsensei.view.adapter.WordGroupAdapter
import com.example.vsensei.view.adapter.SwipeDeleteItemTouchHelper
import com.example.vsensei.viewmodel.WordViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.Hold

class DictionaryFragment : Fragment() {

    private var _binding: FragmentDictionaryBinding? = null
    private val binding get() = _binding!!

    private lateinit var wordViewModel: WordViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        exitTransition = Fade().apply {
            duration = 150
        }
        _binding = FragmentDictionaryBinding.inflate(inflater, container, false)
        wordViewModel = ViewModelProvider(this).get(WordViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addNewGroupButton.setOnClickListener {
            val action = DictionaryFragmentDirections.actionDictionaryFragmentToNewGroupFragment()
            findNavController().navigate(action)
        }
        wordGroupsRecyclerViewInit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun wordGroupsRecyclerViewInit() {
        val adapter = WordGroupAdapter { wordGroupWithWords ->
            reenterTransition = Hold()
            val action = DictionaryFragmentDirections.actionDictionaryFragmentToWordGroupFragment(
                wordGroupWithWords.wordGroup.groupName,
                wordGroupWithWords.wordGroup
            )
            findNavController().navigate(action)
        }
        binding.wordGroupRecyclerView.apply {
            this.adapter = adapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            val itemTouchHelperCallback = SwipeDeleteItemTouchHelper(0, ItemTouchHelper.LEFT) { position: Int ->
                val swipedWordGroup = adapter.currentList[position]
                wordViewModel.deleteWordGroup(swipedWordGroup)
                Snackbar.make(binding.root, context.getString(R.string.item_removed, swipedWordGroup.wordGroup.groupName), Snackbar.LENGTH_LONG)
                    .setAction(context.getString(R.string.undo)) {
                        wordViewModel.restoreWordGroup(swipedWordGroup)
                    }
                    .show()
            }
            ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(this)
        }
        wordViewModel.allWordGroups.observe(viewLifecycleOwner, { wordGroupsWithWords ->
            adapter.submitList(wordGroupsWithWords)
        })
    }
}