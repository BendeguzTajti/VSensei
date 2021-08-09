package com.example.vsensei.view.adapter

import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.vsensei.R
import com.example.vsensei.data.PracticeType
import com.example.vsensei.data.WordGuess
import com.example.vsensei.databinding.WordGuessItemBinding

class WordGuessAdapter(
    private val currentNightMode: Int,
    private val wordGuesses: List<WordGuess>,
    private val hasVariants: Boolean,
    private val practiceType: PracticeType
) : RecyclerView.Adapter<WordGuessAdapter.WordGuessHolder>() {

    inner class WordGuessHolder(
        private val binding: WordGuessItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(wordGuess: WordGuess) {
            if (wordGuess.isCorrectGuess) {
                binding.root.setCardBackgroundColor(getPositiveColor(itemView.context))
            } else {
                binding.root.setCardBackgroundColor(getNegativeColor(itemView.context))
            }
            binding.hint.text = wordGuess.hint
            binding.hintVariant.text = wordGuess.hintVariant
            binding.hintVariant.isVisible = hasVariants && practiceType == PracticeType.GUESS_THE_MEANING
            binding.answer.text = wordGuess.answer
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordGuessHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.word_guess_item, parent, false)
        val binding = WordGuessItemBinding.bind(view)
        return WordGuessHolder(binding)
    }

    override fun onBindViewHolder(holder: WordGuessHolder, position: Int) {
        holder.bind(wordGuesses[position])
    }

    override fun getItemCount(): Int {
        return wordGuesses.size
    }

    private fun getPositiveColor(context: Context): Int {
        return when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_YES -> {
                ContextCompat.getColor(context, R.color.green_300)
            }
            else -> ContextCompat.getColor(context, R.color.green_500)
        }
    }

    private fun getNegativeColor(context: Context): Int {
        return when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_YES -> {
                ContextCompat.getColor(context, R.color.red_300)
            }
            else -> ContextCompat.getColor(context, R.color.red_500)
        }
    }
}