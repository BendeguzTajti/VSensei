package com.example.vsensei.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.vsensei.R
import com.example.vsensei.data.Word
import com.example.vsensei.databinding.PracticeCardItemBinding
import java.util.*

class PracticeCardAdapter(
    val words: List<Word>,
    val selectedLanguage: String
    ) : RecyclerView.Adapter<PracticeCardAdapter.PracticeCardHolder>() {

    inner class PracticeCardHolder(
        private val binding: PracticeCardItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(word: Word) {
            binding.wordPrimary.text = if (word.wordPrimaryVariant.isNullOrBlank()) word.wordPrimary else word.wordPrimaryVariant
            binding.wordPrimaryVariant.apply {
                isVisible = !word.wordPrimaryVariant.isNullOrBlank()
                text = "(${word.wordPrimary})"
            }
            binding.audioButton.text = selectedLanguage
            binding.wordMeaning.text = word.wordMeaning
            binding.guessLayout.setEndIconOnClickListener {
                if (!binding.guess.text.isNullOrBlank()) {
                    binding.guessLayout.isEnabled = false
                    binding.cardPracticeItemRoot.transitionToState(R.id.merged)
                }
            }
            binding.cardPracticeItemRoot.setTransitionListener(object : TransitionAdapter() {
                override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
                    super.onTransitionCompleted(motionLayout, currentId)
                    if (currentId == R.id.merged) {
                        val guess = binding.guess.text.toString().toLowerCase(Locale.getDefault())
                        val wordMeaning = word.wordMeaning.toLowerCase(Locale.getDefault())
                        val stateId = if (guess == wordMeaning) R.id.success else R.id.failure
                        motionLayout.transitionToState(stateId)
                    }
                    if (currentId == R.id.success || currentId == R.id.failure) {
                        binding.wordMeaning.isVisible = true
                    }
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PracticeCardHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.practice_card_item, parent, false)
        val binding = PracticeCardItemBinding.bind(view)
        return PracticeCardHolder(binding)
    }

    override fun onBindViewHolder(holder: PracticeCardHolder, position: Int) {
        val word = words[position]
        holder.bind(word)
    }

    override fun getItemCount(): Int {
        return words.size
    }

}