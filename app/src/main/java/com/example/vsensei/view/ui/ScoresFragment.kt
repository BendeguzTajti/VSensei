package com.example.vsensei.view.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.vsensei.databinding.FragmentScoresBinding
import com.example.vsensei.view.adapter.PracticeSummaryAdapter
import com.example.vsensei.viewmodel.PracticeViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class ScoresFragment : Fragment() {

    private var _binding: FragmentScoresBinding? = null
    private val binding get() = _binding!!

    private val practiceViewModel: PracticeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScoresBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = PracticeSummaryAdapter()
        binding.practiceSummaryRecyclerView.apply {
            this.adapter = adapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
        practiceViewModel.allPracticeSummaries.observe(viewLifecycleOwner, { practiceSummaries ->
            binding.emptyGroupsDisplay.isVisible = practiceSummaries.isEmpty()
            binding.practiceSummaryRecyclerView.isVisible = practiceSummaries.isNotEmpty()
            adapter.submitList(practiceSummaries)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}