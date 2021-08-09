package com.example.vsensei.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.vsensei.R
import com.example.vsensei.data.PracticeSummary
import com.example.vsensei.databinding.PracticeSummaryItemBinding
import java.text.SimpleDateFormat
import java.util.*

class PracticeSummaryAdapter(
    private val currentNightMode: Int,
    private val simpleDateFormat: SimpleDateFormat
) : ListAdapter<PracticeSummary, PracticeSummaryAdapter.PracticeSummaryHolder>(PracticeDiffCallback) {

    inner class PracticeSummaryHolder(
        private val binding: PracticeSummaryItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(practiceSummary: PracticeSummary) {
            val wordGuessAdapter = WordGuessAdapter(
                currentNightMode,
                practiceSummary.wordGuesses,
                practiceSummary.hasVariants,
                practiceSummary.practiceType
            )
            val practicePercent = practiceSummary.getPercent()
            binding.practicedGroupName.text = practiceSummary.practicedGroupName
            binding.practiceType.text =
                itemView.context.getString(practiceSummary.practiceType.labelResId)
            binding.practiceDate.text = simpleDateFormat.format(Date(practiceSummary.timeCreated))
            binding.practicePercent.progress = practicePercent
            binding.wordGuessRecyclerView.adapter = wordGuessAdapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PracticeSummaryHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.practice_summary_item, parent, false)
        val binding = PracticeSummaryItemBinding.bind(view)
        return PracticeSummaryHolder(binding)
    }

    override fun onBindViewHolder(holder: PracticeSummaryHolder, position: Int) {
        val practiceSummary = getItem(position)
        holder.bind(practiceSummary)
    }

    object PracticeDiffCallback : DiffUtil.ItemCallback<PracticeSummary>() {

        override fun areItemsTheSame(oldItem: PracticeSummary, newItem: PracticeSummary): Boolean {
            return oldItem.practiceSummaryId == newItem.practiceSummaryId
        }

        override fun areContentsTheSame(
            oldItem: PracticeSummary,
            newItem: PracticeSummary
        ): Boolean {
            return oldItem == newItem
        }

    }
}