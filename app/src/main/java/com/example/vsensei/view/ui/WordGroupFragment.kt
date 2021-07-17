package com.example.vsensei.view.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.transition.Fade
import com.example.vsensei.R
import com.example.vsensei.databinding.FragmentWordGroupBinding
import com.example.vsensei.view.adapter.WordAdapter
import com.example.vsensei.view.adapter.SwipeDeleteItemTouchHelper
import com.example.vsensei.viewmodel.WordViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.Hold

class WordGroupFragment : Fragment() {

    private var _binding: FragmentWordGroupBinding? = null
    private val binding get() = _binding!!
    private val args: WordGroupFragmentArgs by navArgs()

    private lateinit var wordViewModel: WordViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        exitTransition = Fade().apply {
            duration = 150
        }
        _binding = FragmentWordGroupBinding.inflate(inflater, container, false)
        wordViewModel = ViewModelProvider(this).get(WordViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addNewWordButton.setOnClickListener {
            exitTransition = Hold()
            reenterTransition = Hold()
            val action = WordGroupFragmentDirections.actionWordGroupFragmentToNewWordFragment(args.groupName, args.wordGroup)
            val extras = FragmentNavigatorExtras(it to "newWordCardTransition")
            findNavController().navigate(action, extras)
        }
        wordsRecyclerViewInit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun wordsRecyclerViewInit() {
        val adapter = WordAdapter()
        binding.wordRecyclerView.apply {
            this.adapter = adapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            val itemTouchHelperCallback = SwipeDeleteItemTouchHelper(0, ItemTouchHelper.LEFT) { position: Int ->
                val swipedWord = adapter.currentList[position]
                wordViewModel.deleteWord(swipedWord)
                val wordName = if (swipedWord.wordPrimaryVariant.isNullOrBlank()) swipedWord.wordPrimary else swipedWord.wordPrimaryVariant
                Snackbar.make(binding.root, context.getString(R.string.item_removed, wordName), Snackbar.LENGTH_LONG)
                    .setAction(context.getString(R.string.undo)) {
                        wordViewModel.addWord(swipedWord)
                    }
                    .show()
            }
            ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(this)
        }
        wordViewModel.wordsByGroupId(args.wordGroup.groupId).observe(viewLifecycleOwner, { words ->
            adapter.submitList(words)
        })
    }
}