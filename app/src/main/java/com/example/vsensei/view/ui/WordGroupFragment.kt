package com.example.vsensei.view.ui

import android.graphics.Color
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
import com.example.vsensei.viewmodel.WordViewModel
import com.google.android.material.transition.MaterialContainerTransform
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class WordGroupFragment : Fragment() {

    private var _binding: FragmentWordGroupBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<WordGroupFragmentArgs>()

    private val wordViewModel: WordViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
            scrimColor = Color.TRANSPARENT
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        exitTransition = Fade().apply {
            duration = 150
        }
        _binding = FragmentWordGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            }
            ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(this)
        }
        wordViewModel.wordsByGroupId(args.wordGroup.groupId).observe(viewLifecycleOwner, { words ->
            adapter.submitList(words)
        })
    }
}