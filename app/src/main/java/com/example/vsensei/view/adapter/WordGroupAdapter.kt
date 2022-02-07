package com.example.vsensei.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.vsensei.R
import com.example.vsensei.data.WordGroupWithWords
import com.example.vsensei.databinding.WordGroupItemBinding

class WordGroupAdapter(
    private val onWordGroupClicked: (View, WordGroupWithWords) -> Unit,
    private val onShareButtonClicked: (View, WordGroupWithWords) -> Unit
) : ListAdapter<WordGroupWithWords, WordGroupAdapter.WordGroupHolder>(WordGroupDiffCallback) {

    inner class WordGroupHolder(
        private val binding: WordGroupItemBinding,
        private val onWordGroupClicked: (View, WordGroupWithWords) -> Unit,
        private val onShareButtonClicked: (View, WordGroupWithWords) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(wordGroupWithWords: WordGroupWithWords) {
            itemView.transitionName = wordGroupWithWords.wordGroup.groupId.toString()
            itemView.setOnClickListener { onWordGroupClicked(itemView, wordGroupWithWords) }
            binding.shareButton.setOnClickListener { onShareButtonClicked(itemView, wordGroupWithWords) }
            binding.groupName.text = wordGroupWithWords.wordGroup.groupName
            binding.wordCount.text =
                itemView.context.getString(R.string.num_of_words, wordGroupWithWords.words.size)
            binding.shareButton.isVisible = wordGroupWithWords.words.size > 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordGroupHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.word_group_item, parent, false)
        val binding = WordGroupItemBinding.bind(view)
        return WordGroupHolder(
            binding,
            onWordGroupClicked = { wordGroupView, wordGroupWithWords ->
                onWordGroupClicked(wordGroupView, wordGroupWithWords)
            },
            onShareButtonClicked = { wordGroupView, wordGroupWithWords ->
                onShareButtonClicked(wordGroupView, wordGroupWithWords)
            }
        )
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