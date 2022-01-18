package com.example.vsensei.view.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.vsensei.R
import com.example.vsensei.databinding.FragmentDictionaryBinding
import com.example.vsensei.view.adapter.WordGroupAdapter
import com.example.vsensei.view.adapter.SwipeDeleteItemTouchHelper
import com.example.vsensei.viewmodel.WordViewModel
import com.google.android.material.transition.Hold
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DictionaryFragment : Fragment() {

    private var _binding: FragmentDictionaryBinding? = null
    private val binding get() = _binding!!

    private val wordViewModel: WordViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDictionaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exitTransition = null
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        wordGroupsRecyclerViewInit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun wordGroupsRecyclerViewInit() {
        val adapter = WordGroupAdapter { view, wordGroupWithWords ->
            exitTransition = Hold().apply {
                duration = resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
            }
            reenterTransition = Hold().apply {
                duration = resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
            }
            val extras = FragmentNavigatorExtras(
                view to getString(R.string.word_group_transition_name)
            )
            val action = DictionaryFragmentDirections.actionDictionaryFragmentToWordGroupFragment(
                wordGroupWithWords.wordGroup.groupName,
                wordGroupWithWords.wordGroup
            )
            findNavController().navigate(action, extras)
        }
        binding.wordGroupRecyclerView.apply {
            this.adapter = adapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            val itemTouchHelperCallback = SwipeDeleteItemTouchHelper(0, ItemTouchHelper.LEFT) { position: Int ->
                val swipedWordGroup = adapter.currentList[position]
                wordViewModel.deleteWordGroup(swipedWordGroup)
            }
            ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(this)
        }
        wordViewModel.allWordGroups.observe(viewLifecycleOwner, { wordGroupsWithWords ->
            adapter.submitList(wordGroupsWithWords)
        })
    }
}