package com.example.vsensei.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.vsensei.R
import com.example.vsensei.data.WordGroupWithWords
import com.example.vsensei.databinding.WordGroupItemBinding

class WordGroupAdapter(
    private val onWordGroupClicked: (View, WordGroupWithWords) -> Unit
) : ListAdapter<WordGroupWithWords, WordGroupAdapter.WordGroupHolder>(WordGroupDiffCallback) {

    inner class WordGroupHolder(
        private val binding: WordGroupItemBinding,
        private val onWordGroupClicked: (View, Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(wordGroupWithWords: WordGroupWithWords) {
            binding.root.transitionName = wordGroupWithWords.wordGroup.groupId.toString()
            itemView.setOnClickListener { onWordGroupClicked(binding.root, adapterPosition) }
            binding.shareButton.setOnClickListener {
                // TODO navigate to QR code fragment
            }
            binding.groupName.text = wordGroupWithWords.wordGroup.groupName
            binding.wordCount.text =
                itemView.context.getString(R.string.num_of_words, wordGroupWithWords.words.size)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordGroupHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.word_group_item, parent, false)
        val binding = WordGroupItemBinding.bind(view)
        return WordGroupHolder(binding) { wordGroupView, position ->
            onWordGroupClicked(
                wordGroupView,
                getItem(position)
            )
        }
    }

    override fun onBindViewHolder(holder: WordGroupHolder, position: Int) {
        val wordGroupWithWords = getItem(position)
        holder.bind(wordGroupWithWords)
    }

    object WordGroupDiffCallback : DiffUtil.ItemCallback<WordGroupWithWords>() {

        override fun areItemsTheSame(
            oldItem: WordGroupWithWords,
            newItem: WordGroupWithWords
        ): Boolean {
            return oldItem.wordGroup.groupId == newItem.wordGroup.groupId
        }

        override fun areContentsTheSame(
            oldItem: WordGroupWithWords,
            newItem: WordGroupWithWords
        ): Boolean {
            return oldItem == newItem
        }
    }
}