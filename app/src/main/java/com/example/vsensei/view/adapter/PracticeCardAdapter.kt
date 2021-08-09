package com.example.vsensei.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.vsensei.data.PracticeType
import com.example.vsensei.data.Word
import com.example.vsensei.data.WordGuess
import com.example.vsensei.view.ui.PracticeCardFragment

class PracticeCardAdapter(
    private val practiceType: PracticeType,
    private val words: List<Word>,
    private val selectedLanguage: String,
    private val hasVariants: Boolean,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return words.size
    }

    override fun createFragment(position: Int): Fragment {
        return PracticeCardFragment.newInstance(practiceType, words[position], selectedLanguage, hasVariants, position)
    }

    interface WordGuessCallback {
        fun sayWord(word: String)
        fun onWordGuessed(wordGuess: WordGuess)
    }
}