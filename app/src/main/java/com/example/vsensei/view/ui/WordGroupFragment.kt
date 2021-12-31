package com.example.vsensei.view.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.transition.Fade
import com.example.vsensei.R
import com.example.vsensei.databinding.FragmentWordGroupBinding
import com.example.vsensei.view.adapter.WordAdapter
import com.example.vsensei.view.adapter.SwipeDeleteItemTouchHelper
import com.example.vsensei.view.contract.BottomNavActivity
import com.example.vsensei.viewmodel.WordViewModel
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class WordGroupFragment : Fragment() {

    private var bottomNavActivity: BottomNavActivity? = null
    private var _binding: FragmentWordGroupBinding? = null
    private val binding get() = _binding!!
    private val args: WordGroupFragmentArgs by navArgs()

    private val wordViewModel: WordViewModel by viewModel()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        bottomNavActivity = requireActivity() as BottomNavActivity
    }

    override fun onDetach() {
        super.onDetach()
        bottomNavActivity = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        exitTransition = Fade().apply {
            duration = 150
        }
        bottomNavActivity?.hideBottomNav()
        _binding = FragmentWordGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addNewWordButton.setOnClickListener {
            val action = WordGroupFragmentDirections.actionWordGroupFragmentToNewWordFragment(args.wordGroup, null)
            findNavController().navigate(action)
        }
        wordsRecyclerViewInit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun wordsRecyclerViewInit() {
        val adapter = WordAdapter { word ->
            val action = WordGroupFragmentDirections.actionWordGroupFragmentToNewWordFragment(args.wordGroup, word)
            findNavController().navigate(action)
        }
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