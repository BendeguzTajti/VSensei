package com.example.vsensei.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlin.math.roundToInt

@Parcelize
data class PracticeSummary(
    val practiceType: PracticeType,
    val practicedGroupName: String,
    val correctGuesses: MutableList<Word>,
    val wrongGuesses: MutableList<Word>,
    val timeCreated: Long
) : Parcelable {

    fun getPercent(): Int {
        val allWords = (correctGuesses.size + wrongGuesses.size).toFloat()
        return ((correctGuesses.size / allWords) * 100).roundToInt()
    }
}