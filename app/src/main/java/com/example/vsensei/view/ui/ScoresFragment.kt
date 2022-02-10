package com.example.vsensei.view.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.example.vsensei.R
import com.example.vsensei.data.PracticeSummary
import com.example.vsensei.databinding.FragmentScoresBinding
import com.example.vsensei.util.*
import com.example.vsensei.viewmodel.PracticeViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import org.koin.androidx.viewmodel.ext.android.viewModel

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
        practiceViewModel.allPracticeSummaries.observe(viewLifecycleOwner, { practiceSummaries ->
            if (practiceSummaries.isEmpty()) {
                binding.emptyScoresText.isVisible = true
                binding.emptyScoresIcon.isVisible = true
            } else {
                binding.emptyScoresText.isVisible = false
                binding.emptyScoresIcon.isVisible = false
                lineChartInit(practiceSummaries)
                pieChartInit(practiceSummaries)
                barChartInit(practiceSummaries)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun lineChartInit(practiceSummaries: List<PracticeSummary>) {
        val lineDataSet = LineDataSet(
            practiceSummaries.toEntries(),
            getString(R.string.practice_percentages)
        ).apply {
            setDrawFilled(true)
            color = requireContext().getColorFromAttr(R.attr.colorPrimary)
            setCircleColor(requireContext().getColorFromAttr(R.attr.colorPrimary))
            fillColor = requireContext().getColorFromAttr(R.attr.colorPrimary)
            valueTextColor = requireContext().getColorFromAttr(R.attr.colorOnSurface)
            valueFormatter = DefaultValueFormatter(0)
            valueTextSize = 10f
        }
        binding.scoresLineChart.apply {
            data = LineData(lineDataSet)
            description = null
            xAxis.position = XAxis.XAxisPosition.BOTH_SIDED
            xAxis.setDrawLabels(false)
            axisLeft.axisMinimum = 0f
            axisLeft.axisMaximum = 100f
            axisRight.setDrawLabels(false)
            axisRight.setDrawGridLines(false)
            legend.textColor = requireContext().getColorFromAttr(R.attr.colorOnSurface)
            axisLeft.textColor = requireContext().getColorFromAttr(R.attr.colorOnSurface)
            setScaleEnabled(false)
            isVisible = true
            invalidate()
        }
    }

    private fun barChartInit(practiceSummaries: List<PracticeSummary>) {
        val barColors = listOf(
            requireContext().getColorFromAttr(R.attr.colorPositive),
            requireContext().getColorFromAttr(R.attr.colorNegative)
        )
        val barDataSet = BarDataSet(
            practiceSummaries.toMinMaxPercentageBarEntries(),
            getString(R.string.guesses_percentage)
        ).apply {
            colors = barColors
            valueTextColor = requireContext().getColorFromAttr(R.attr.colorOnSurface)
            valueFormatter = DefaultValueFormatter(0)
            valueTextSize = 11f
        }
        binding.scoresBarChart.apply {
            data = BarData(barDataSet)
            description = null
            xAxis.position = XAxis.XAxisPosition.BOTH_SIDED
            xAxis.setDrawLabels(false)
            axisLeft.axisMinimum = 0f
            axisLeft.axisMaximum = 100f
            axisRight.setDrawLabels(false)
            axisRight.setDrawGridLines(false)
            legend.textColor = requireContext().getColorFromAttr(R.attr.colorOnSurface)
            axisLeft.textColor = requireContext().getColorFromAttr(R.attr.colorOnSurface)
            setScaleEnabled(false)
            isVisible = true
            invalidate()
        }
    }

    private fun pieChartInit(practiceSummaries: List<PracticeSummary>) {
        val pieEntries = listOf(
            PieEntry(
                practiceSummaries.floatOfCorrectGuesses(),
                getString(R.string.correct)
            ),
            PieEntry(
                practiceSummaries.floatOfWrongGuesses(),
                getString(R.string.wrong)
            )
        )
        val pieColors = listOf(
            requireContext().getColorFromAttr(R.attr.colorPositive),
            requireContext().getColorFromAttr(R.attr.colorNegative)
        )
        val pieDataSet = PieDataSet(pieEntries, getString(R.string.guesses)).apply {
            colors = pieColors
            valueFormatter = DefaultValueFormatter(0)
            valueTextSize = 12f
            valueTextColor = requireContext().getColorFromAttr(R.attr.colorOnPrimary)
        }
        binding.scoresPieChart.apply {
            data = PieData(pieDataSet)
            description = null
            isDrawHoleEnabled = false
            legend.textColor = requireContext().getColorFromAttr(R.attr.colorOnSurface)
            isVisible = true
            invalidate()
        }
    }
}