package com.example.vsensei.view.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.vsensei.databinding.FragmentDictionaryBinding
import com.example.vsensei.view.adapter.WordGroupAdapter
import com.example.vsensei.view.adapter.SwipeDeleteItemTouchHelper
import com.example.vsensei.view.contract.BottomNavActivity
import com.example.vsensei.viewmodel.WordViewModel
import com.google.android.material.transition.Hold
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DictionaryFragment : Fragment() {

    private var bottomNavActivity: BottomNavActivity? = null
    private var _binding: FragmentDictionaryBinding? = null
    private val binding get() = _binding!!

    private val wordViewModel: WordViewModel by sharedViewModel()

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
        bottomNavActivity?.showBottomNav()
        _binding = FragmentDictionaryBinding.inflate(inflater, container, false)
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
            }
            ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(this)
        }
        wordViewModel.allWordGroups.observe(viewLifecycleOwner, { wordGroupsWithWords ->
            adapter.submitList(wordGroupsWithWords)
        })
    }
}