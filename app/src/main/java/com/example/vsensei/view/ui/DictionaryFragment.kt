package com.example.vsensei.view.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.vsensei.R
import com.example.vsensei.data.WordGroupWithWords
import com.example.vsensei.databinding.FragmentDictionaryBinding
import com.example.vsensei.view.adapter.WordGroupAdapter
import com.example.vsensei.view.adapter.SwipeDeleteItemTouchHelper
import com.example.vsensei.viewmodel.WordViewModel
import com.google.android.material.transition.MaterialElevationScale
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DictionaryFragment : Fragment() {

    private var _binding: FragmentDictionaryBinding? = null
    private val binding get() = _binding!!

    private val wordViewModel: WordViewModel by sharedViewModel()

    private val wordGroupAdapter: WordGroupAdapter by lazy {
        WordGroupAdapter(
            onWordGroupClicked = { view, wordGroupWithWords ->
                navigateToWordGroupFragment(view, wordGroupWithWords)
            },
            onShareButtonClicked = { view, wordGroupWithWords ->
                navigateToShareFragment(view, wordGroupWithWords)
            }
        )
    }

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
        binding.wordGroupRecyclerView.adapter = null
        _binding = null
    }

    private fun wordGroupsRecyclerViewInit() {
        binding.wordGroupRecyclerView.apply {
            this.adapter = wordGroupAdapter
            val itemTouchHelperCallback =
                SwipeDeleteItemTouchHelper(0, ItemTouchHelper.LEFT) { position: Int ->
                    val swipedWordGroup = wordGroupAdapter.currentList[position]
                    wordViewModel.deleteWordGroup(swipedWordGroup)
                }
            ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(this)
        }
        wordViewModel.allWordGroups.observe(viewLifecycleOwner, { wordGroupsWithWords ->
            wordGroupAdapter.submitList(wordGroupsWithWords)
        })
    }

    private fun navigateToWordGroupFragment(view: View, wordGroupWithWords: WordGroupWithWords) {
        exitTransition = MaterialElevationScale(false).apply {
            duration =
                resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration =
                resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
        }
        val extras = FragmentNavigatorExtras(
            view to getString(R.string.word_group_transition_name)
        )
        val action =
            DictionaryFragmentDirections.actionDictionaryFragmentToWordGroupFragment(
                wordGroupWithWords.wordGroup.groupName,
                wordGroupWithWords.wordGroup
            )
        findNavController().navigate(action, extras)
    }

    private fun navigateToShareFragment(view: View, wordGroupWithWords: WordGroupWithWords) {
        exitTransition = MaterialElevationScale(false).apply {
            duration =
                resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration =
                resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
        }
        val extras = FragmentNavigatorExtras(
            view to getString(R.string.word_group_transition_name)
        )
        val action = DictionaryFragmentDirections.actionDictionaryFragmentToShareFragment(
            wordGroupWithWords
        )
        findNavController().navigate(action, extras)
    }
}