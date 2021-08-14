package com.example.vsensei.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.vsensei.R
import com.example.vsensei.data.Word
import com.example.vsensei.databinding.WordItemBinding

class WordAdapter(
    private val onWordClicked: (Word) -> Unit
) : ListAdapter<Word, WordAdapter.WordHolder>(WordDiffCallback) {

    inner class WordHolder(
        private val binding: WordItemBinding,
        private val onWordClicked: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener { onWordClicked(adapterPosition) }
        }

        fun bind(word: Word) {
            binding.wordPrimary.text = if (word.wordPrimaryVariant.isNullOrBlank()) word.wordPrimary else "${word.wordPrimaryVariant} (${word.wordPrimary})"
            binding.wordMeaning.text = word.wordMeanings.joinToString(separator = ", ")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.word_item, parent, false)
        val binding = WordItemBinding.bind(view)
        return WordHolder(binding) { position -> onWordClicked(getItem(position)) }
    }

    override fun onBindViewHolder(holder: WordHolder, position: Int) {
        val word = getItem(position)
        holder.bind(word)
    }

    object WordDiffCallback : DiffUtil.ItemCallback<Word>() {

        override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem.wordId == newItem.wordId
        }

        override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem == newItem
        }
    }
}