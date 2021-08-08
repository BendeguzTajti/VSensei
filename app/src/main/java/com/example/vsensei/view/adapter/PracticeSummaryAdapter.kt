package com.example.vsensei.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.vsensei.R
import com.example.vsensei.data.PracticeSummary

class PracticeSummaryAdapter : ListAdapter<PracticeSummary, PracticeSummaryAdapter.PracticeSummaryHolder>(PracticeDiffCallback) {

    inner class PracticeSummaryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(practiceSummary: PracticeSummary) {
            val practicedGroupName: TextView = itemView.findViewById(R.id.practiced_group_name)
            val practiceType: TextView = itemView.findViewById(R.id.practice_type)
            val practicePercent: TextView = itemView.findViewById(R.id.practice_percent)

            practicedGroupName.text = practiceSummary.practicedGroupName
            practiceType.text = itemView.context.getString(practiceSummary.practiceType.labelResId)
            practicePercent.text = itemView.context.getString(R.string.practice_percent, practiceSummary.getPercent())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PracticeSummaryHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.practice_summary_item, parent, false)
        return PracticeSummaryHolder(view)
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