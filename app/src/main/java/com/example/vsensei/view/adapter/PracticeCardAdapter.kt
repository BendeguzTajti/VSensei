package com.example.vsensei.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.vsensei.data.Word
import com.example.vsensei.view.ui.PracticeCardFragment
import com.example.vsensei.view.ui.PracticeResultFragment

class PracticeCardAdapter(
    private val words: List<Word>,
    private val selectedLanguage: String,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return words.size + 1
    }

    override fun createFragment(position: Int): Fragment {
        return if (position < words.size) {
            PracticeCardFragment.newInstance(words[position], selectedLanguage, position)
        } else {
            PracticeResultFragment()
        }
    }

    interface WordGuessCallback {
        fun sayWord(word: String)
        fun onWordGuessed(position: Int, isCorrectGuess: Boolean)
    }
}